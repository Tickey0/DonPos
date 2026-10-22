import { and, eq } from 'drizzle-orm'
import { db } from '../../main.ts'
import { roles, users } from '../db/schema.ts'

export async function getAllPeople() {
  return await db.select({
    name: users.name,
  }).from(users)
    .innerJoin(roles, eq(users.role, roles.id))
    .where(and(eq(users.visible, true), eq(roles.name, 'Employee role')))
}
