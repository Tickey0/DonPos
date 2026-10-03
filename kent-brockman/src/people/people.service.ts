import { eq } from 'drizzle-orm'
import { db } from '../../main.ts'
import { people } from '../db/schema.ts'

export async function getAllPeople() {
  return await db.select({
    name: people.name,
  }).from(people).where(eq(people.visible, true))
}
