import type { IncomingMessage } from 'node:http'
import type { WebSocket } from 'ws'
import { log } from './log.ts'
import { isValidApiKey } from './auth.ts'

type AppEvent = { event: string; [key: string]: unknown }

export default class ChatServer {
  // Pantallas conectadas, la clave es "<cajero> cliente"; un cajero puede tener varias (tablet, celular, pestañas)
  private connectedClients = new Map<string, Set<WebSocket>>()

  constructor() {
    // Cada 30 segundos avisa a las tablets que el servidor sigue vivo
    setInterval(() => this.broadcast({ event: 'ping' }), 30_000)
  }

  public handleConnection(socket: WebSocket, request: IncomingMessage) {
    const params = new URL(request.url ?? '', 'http://localhost').searchParams
    const cashier = params.get('cashier')

    // El navegador no deja poner cabeceras en un WebSocket, por eso la API viene en la direccion.
    // 4001 le avisa a la tablet que su API esta mal, para que vuelva a Configurar en vez de reintentar
    if (!isValidApiKey(params.get('apiKey'))) {
      log.withMetadata({ ip: request.socket.remoteAddress }).warn('Unauthorized connection')
      socket.close(4001, 'Unauthorized')
      return
    }

    if (!cashier) {
      socket.close(1008, 'Cashier is required')
      return
    }

    const username = `${cashier} cliente`

    // Se suma a las pantallas del cajero sin botar a las demas; asi una tablet que se reconecta no pelea con otra
    const sockets = this.connectedClients.get(username) ?? new Set<WebSocket>()
    sockets.add(socket)
    this.connectedClients.set(username, sockets)

    socket.on('close', () => {
      sockets.delete(socket)

      if (sockets.size === 0) {
        this.connectedClients.delete(username)
      }

      log.withMetadata({ username }).info('Client disconnected')
    })

    log.withMetadata({ username }).info('New client connected')
  }

  // Envia de "<cajero> cajero" a "<cajero> cliente"; devuelve false si la tablet no esta conectada
  public send(cashier: string, event: string, message?: unknown) {
    const username = `${cashier} cajero`
    const recipient = `${cashier} cliente`
    const sockets = this.connectedClients.get(recipient)

    if (!sockets) {
      log.withMetadata({ username, recipient, event }).warn('Recipient is not connected')
      return false
    }

    const data = JSON.stringify({ event, username, recipient, message })
    for (const socket of sockets) {
      socket.send(data)
    }

    // No se guardan los datos del cliente en el log, solo quien envio a quien
    log.withMetadata({ username, recipient, event }).info('Sent message')
    return true
  }

  private broadcast(message: AppEvent) {
    const messageString = JSON.stringify(message)
    for (const sockets of this.connectedClients.values()) {
      for (const client of sockets) {
        client.send(messageString)
      }
    }
  }
}
