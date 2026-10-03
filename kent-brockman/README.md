# Sender message from DonPos to client

Chat application with WebSockets

## Usage
Create .env file in root directory with the following content:
```
PORT=8181
DATABASE_URL=mysql://donpos:d@localhost:3306/donpos
```
## Run the program
```
deno install
```
## Task
```
deno task
```
## Format
```
deno fmt
```
## Project Structure
kent-brockman/
├── deno.json             # Deno configuration and scripts
├── drizzle.config.ts     # Drizzle configuration and shared DB pool
├── main.ts               # App bootstrap and route registration
├── public/               # Static frontend files
│   ├── app.js
│   ├── index.html
│   └── style.css
├── src/
│   ├── chat.server.ts    # WebSocket chat server
│   ├── log.ts            # Logger
│   ├── db/
│   │   └── schema.ts     # Drizzle database schema
│   ├── people/
│   │   ├── people.controller.ts
│   │   ├── people.routes.ts
│   │   └── people.service.ts
│   └── ping/
│       └── ping.routes.ts
├── .env                  # Local environment variables (not committed)
└── README.md             # Project documentation