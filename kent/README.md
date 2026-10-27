# Expo React Native Customer Display App

Shows on the customer's tablet the customer data entered by the cashier in DonPos.
It needs the kent-brockman server running.

## Install
```sh
pnpm install
```
## Development
Web version in the browser:
```sh
pnpm expo start --web
```
On the phone, scan the QR code with the Expo Go app (same WiFi as the PC).

## Configure the tablet
The first time, write the server address (for example `192.168.1.8:8181`) and the API key of the server.
They are saved in SecureStore on the phone (in localStorage on the web). To change them later, keep
pressed the black header for 6 seconds; leave the API key empty to keep the current one.

Every time the app is opened it asks for the cashier. On the web, reloading the page keeps the cashier;
closing the tab forgets it. The customer data is cleared after 1 minute if DonPos never clears it.

## Web version for the server
Builds the web version into `kent-brockman/public`, the server shows it at `http://<server ip>:8181`.
```sh
pnpm export:web
```
