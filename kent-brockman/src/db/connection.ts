import { drizzle } from 'drizzle-orm/mysql2'
import mysql from 'mysql2/promise'

const databaseUrl = process.env.DATABASE_URL

if (!databaseUrl) {
  throw new Error('DATABASE_URL is not configured')
}

export const db = drizzle({ client: mysql.createPool(databaseUrl) })
