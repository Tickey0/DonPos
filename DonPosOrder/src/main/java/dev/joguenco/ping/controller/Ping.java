package dev.joguenco.ping.controller;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
record Ping(String message) {
  public Ping {
    message = "pong";
  }
}
