import type { Request, Response } from 'express'
import { getAllPeople } from './people.service.ts'

export class PeopleController {
  public static async getAll(_request: Request, response: Response) {
    const data = await getAllPeople()
    response.json(data)
  }
}
