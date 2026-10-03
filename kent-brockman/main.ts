import { Application, Context, Router } from '@oak/oak'
import { drizzle } from 'drizzle-orm/mysql2'
import ChatServer from './src/chat.server.ts'
import '@std/dotenv/load'
import { log } from './src/log.ts'
import { poolConnection } from './drizzle.config.ts'
import { pingRoutes } from './src/ping/ping.routes.ts'
import { peopleRoutes } from './src/people/people.routes.ts'

export const db = drizzle({ client: poolConnection })

const app = new Application()
const port = Number(Deno.env.get('PORT')) || 8080
const router = new Router()
const server = new ChatServer()

router.get('/start_web_socket', (ctx: Context) => server.handleConnection(ctx))
pingRoutes(router)

app.use(router.routes())
app.use(peopleRoutes.routes())
app.use(router.allowedMethods())
app.use(async (context) => {
  await context.send({
    root: Deno.cwd(),
    index: 'public/index.html',
  })
})

log.info('Listening at http://localhost:' + port)
await app.listen({ port })
