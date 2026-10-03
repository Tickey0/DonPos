import { boolean, int, mysqlTable, varchar } from 'drizzle-orm/mysql-core'

export const people = mysqlTable('people', {
  id: int().primaryKey(),
  name: varchar('name', { length: 255 }).notNull().unique(),
  visible: boolean('visible').notNull().default(true),
})

export const schema = {
  people,
}
