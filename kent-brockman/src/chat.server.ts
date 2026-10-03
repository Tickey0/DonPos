import { Context } from '@oak/oak'
import { log } from './log.ts'

type WebSocketWithUsername = WebSocket & { username: string }
type AppEvent = { event: string; [key: string]: any }

export default class ChatServer {
  private connectedClients = new Map<string, WebSocketWithUsername>()

  public async handleConnection(ctx: Context) {
    const socket = await ctx.upgrade() as WebSocketWithUsername
    const username = ctx.request.url.searchParams.get('username') as string

    if (this.connectedClients.has(username)) {
      socket.close(1008, `Username ${username} is already taken`)
      return
    }

    socket.username = username
    socket.onopen = this.broadcastUsernames.bind(this)
    socket.onclose = () => {
      this.clientDisconnected(socket.username)
    }
    socket.onmessage = (m) => {
      this.send(socket.username, m)
    }
    this.connectedClients.set(username, socket)

    log.withMetadata({ username }).info('New client connected')
  }

  private send(username: string, message: any) {
    const data = JSON.parse(message.data)
    if (data.event !== 'send-message') {
      return
    }

    const recipient = typeof data.recipient === 'string' ? data.recipient : 'all'
    const payload = {
      event: 'send-message',
      username,
      recipient,
      message: data.message,
    }

    if (recipient === 'all') {
      this.broadcast(payload)
      return
    }

    const sockets = new Set<WebSocketWithUsername>()
    const senderSocket = this.connectedClients.get(username)
    const targetSocket = this.connectedClients.get(recipient)

    if (senderSocket) {
      sockets.add(senderSocket)
    }

    if (targetSocket) {
      sockets.add(targetSocket)
    }

    for (const client of sockets) {
      client.send(JSON.stringify(payload))
    }
  }

  private clientDisconnected(username: string) {
    this.connectedClients.delete(username)
    this.broadcastUsernames()

    log.withMetadata({ username }).info('Client disconnected')
  }

  private broadcastUsernames() {
    const usernames = [...this.connectedClients.keys()]
    this.broadcast({ event: 'update-users', usernames })

    log.withMetadata({ usernames }).info('Sent username list')
  }

  private broadcast(message: AppEvent) {
    const messageString = JSON.stringify(message)
    for (const client of this.connectedClients.values()) {
      client.send(messageString)
    }
  }
}
