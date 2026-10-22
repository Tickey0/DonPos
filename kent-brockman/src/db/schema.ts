import { boolean, mysqlTable, varchar } from 'drizzle-orm/mysql-core'

export const users = mysqlTable('people', {
  id: varchar('id', { length: 255 }).primaryKey(),
  name: varchar('name', { length: 255 }).notNull().unique(),
  role: varchar('role', { length: 255 }).notNull(),
  visible: boolean('visible').notNull().default(true),
})

export const roles = mysqlTable('roles', {
  id: varchar('id', { length: 255 }).primaryKey(),
  name: varchar('name', { length: 255 }).notNull().unique(),
})

export const schema = {
  users,
  roles,
}
