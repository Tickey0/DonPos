# Sender message from DonPos to client

Shows the customer data entered by the cashier in DonPos on the customer's tablet, using WebSockets.

## Requirements
* Node.js 24 LTS
* pnpm

## Usage
Create .env file in root directory with the following content:
```
PORT=8181
DATABASE_URL=mysql://donpos:d@localhost:3306/donpos
API_KEY=<secret key>
```
Generate the API key once and paste it in `.env`, in each tablet and in DonPos (Maintenance > Services > Kent, method `X-API-KEY`):
```
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```
The server does not start without `API_KEY`.
## Install
```
pnpm install
```
## Run the program
```
pnpm dev
```
## Type check
```
pnpm check
```
## Endpoints
| Method | Path | Used by | Description |
|---|---|---|---|
| GET | /ping | anyone | Health check |
| GET | /people | tablet | Cashiers (people with Employee role) |
| WS | /start_web_socket?cashier=Pepe&apiKey=... | tablet | Connects as "Pepe cliente" |
| POST | /send_message | DonPos | `{ "cashier": "Pepe", "message": {...} }` from "Pepe cajero" to "Pepe cliente" |
| POST | /clear_message | DonPos | `{ "cashier": "Pepe" }` clears the tablet of "Pepe cliente" |

Everything except `/ping` and the web page needs the API key: in the `X-API-KEY` header, or in the
`apiKey` parameter for the WebSocket (browsers can't send headers there). A wrong key gets `401`,
or the WebSocket is closed with code `4001`.

## Project Structure
kent-brockman/
├── package.json          # Node.js dependencies and scripts
├── tsconfig.json         # TypeScript type check configuration
├── main.ts               # App bootstrap and route registration
├── public/               # Static frontend files (web version of kent)
├── src/
│   ├── auth.ts           # API key check
│   ├── chat.routes.ts    # Routes used by DonPos
│   ├── chat.server.ts    # WebSocket chat server
│   ├── log.ts            # Logger
│   ├── db/
│   │   ├── connection.ts # Shared DB pool
│   │   └── schema.ts     # Drizzle database schema
│   ├── people/
│   │   ├── people.controller.ts
│   │   ├── people.routes.ts
│   │   └── people.service.ts
│   └── ping/
│       └── ping.routes.ts
├── .env                  # Local environment variables (not committed)
└── README.md             # Project documentation
