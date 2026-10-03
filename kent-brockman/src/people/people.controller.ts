import { Context } from '@oak/oak'
import { getAllPeople } from './people.service.ts'

export class PeopleController {
  public static async getAll(ctx: Context) {
    const data = await getAllPeople()
    ctx.response.body = data
  }
}
