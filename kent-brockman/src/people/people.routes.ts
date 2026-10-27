import { Router } from 'express'
import { PeopleController } from './people.controller.ts'
import { requireApiKey } from '../auth.ts'

const router = Router()

router.get('/people', requireApiKey, PeopleController.getAll)

export { router as peopleRoutes }
