import type { Request, Response, Router } from 'express'

export function pingRoutes(router: Router) {
  router.get('/ping', (_request: Request, response: Response) => {
    response.json({ message: 'pong' })
  })
}
