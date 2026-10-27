import type { NextFunction, Request, Response } from 'express'
import { timingSafeEqual } from 'node:crypto'
import { log } from './log.ts'

// La API que deben mandar DonPos y las tablets; esta solo en el .env del servidor
const apiKey = process.env.API_KEY ?? ''

if (!apiKey) {
  throw new Error('API_KEY is required in .env')
}

const expected = Buffer.from(apiKey)

// Compara de forma que el tiempo de respuesta no da pistas de cuantas letras acerto
export function isValidApiKey(value: unknown) {
  if (typeof value !== 'string') {
    return false
  }

  const received = Buffer.from(value)
  return received.length === expected.length && timingSafeEqual(received, expected)
}

// Deja pasar solo los pedidos que traen la API correcta en la cabecera X-API-KEY
export function requireApiKey(request: Request, response: Response, next: NextFunction) {
  if (isValidApiKey(request.get('X-API-KEY'))) {
    next()
    return
  }

  log.withMetadata({ ip: request.ip, path: request.path }).warn('Unauthorized request')
  response.status(401).json({ error: 'Unauthorized' })
}
