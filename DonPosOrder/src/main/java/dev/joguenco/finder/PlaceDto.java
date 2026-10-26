package dev.joguenco.finder;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record PlaceDto(
    String id,
    String name,
    String seat,
    String floor,
    String customer,
    String waiter,
    String ticketId,
    int tableMoved,
    int guest) {}
