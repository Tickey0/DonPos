import { defineConfig } from 'drizzle-kit'
import mysql from 'mysql2/promise'

export const databaseUrl = Deno.env.get('DATABASE_URL')

if (!databaseUrl) {
  throw new Error('DATABASE_URL is not configured')
}

export const poolConnection = mysql.createPool(databaseUrl)

export default defineConfig({
  dialect: 'mysql',
  schema: './src/db/schema.ts',
  out: './drizzle',
  dbCredentials: {
    url: databaseUrl,
  },
})

