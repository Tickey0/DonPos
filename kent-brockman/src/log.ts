import { ConsoleTransport, LogLayer } from 'loglayer'
import { LogFileRotationTransport } from '@loglayer/transport-log-file-rotation'
import { serializeError } from 'serialize-error'

export const log = new LogLayer({
  errorSerializer: serializeError,
  transport: [
    new ConsoleTransport({
      logger: console as any,
    }),
    new LogFileRotationTransport({
      filename: './logs/kent.log',
    }),
  ],
})
