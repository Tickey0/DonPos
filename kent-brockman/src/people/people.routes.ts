import { Router } from '@oak/oak'
import { PeopleController } from './people.controller.ts'

const router = new Router()

router.get('/people', PeopleController.getAll)

export { router as peopleRoutes }
