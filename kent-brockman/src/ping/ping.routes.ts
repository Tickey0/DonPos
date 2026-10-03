import { Router } from '@oak/oak'

export function pingRoutes(router: Router) {
  router.get('/ping', (context) => {
    context.response.body = { message: 'pong' }
  })
}
