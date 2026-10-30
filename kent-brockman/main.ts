import express from 'express'
import type { NextFunction, Request, Response } from 'express'
import { WebSocketServer } from 'ws'
import ChatServer from './src/chat.server.ts'
import { log } from './src/log.ts'
import { chatRoutes } from './src/chat.routes.ts'
import { pingRoutes } from './src/ping/ping.routes.ts'
import { peopleRoutes } from './src/people/people.routes.ts'

const app = express()
const port = Number(process.env.PORT) || 8080
const router = express.Router()
const server = new ChatServer()

pingRoutes(router)
chatRoutes(router, server)

app.use((request: Request, response: Response, next: NextFunction) => {
  response.set('Access-Control-Allow-Origin', '*')
  response.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
  response.set('Access-Control-Allow-Headers', 'Content-Type, X-API-KEY')

  if (request.method === 'OPTIONS') {
    response.sendStatus(204)
    return
  }

  next()
})

app.use(express.json())
app.use(router)
app.use(peopleRoutes)
app.use(express.static('public'))

const httpServer = app.listen(port, '0.0.0.0', () => {
  log.info('Listening at http://localhost:' + port)
})

new WebSocketServer({ server: httpServer, path: '/start_web_socket' })
  .on('connection', (socket, request) => server.handleConnection(socket, request))
