import type { Request, Response, Router } from 'express'
import type ChatServer from './chat.server.ts'
import { requireApiKey } from './auth.ts'

export function chatRoutes(router: Router, server: ChatServer) {
  // DonPos muestra los datos del cliente en la tablet del cajero
  router.post('/send_message', requireApiKey, (request: Request, response: Response) => {
    const { cashier, message } = request.body ?? {}

    if (!isValidCashier(cashier, response)) {
      return
    }

    response.json({ delivered: server.send(cashier, 'send-message', message) })
  })

  // DonPos deja en blanco la tablet del cajero
  router.post('/clear_message', requireApiKey, (request: Request, response: Response) => {
    const { cashier } = request.body ?? {}

    if (!isValidCashier(cashier, response)) {
      return
    }

    response.json({ delivered: server.send(cashier, 'clear-message') })
  })
}

function isValidCashier(cashier: unknown, response: Response): cashier is string {
  if (typeof cashier === 'string' && cashier.trim()) {
    return true
  }

  response.status(400).json({ error: 'Cashier is required' })
  return false
}
