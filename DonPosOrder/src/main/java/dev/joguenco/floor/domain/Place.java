package dev.joguenco.floor.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Serdeable
@MappedEntity("places")
public class Place {

  @Id private String id;

  private String name;

  @MappedProperty("seats")
  private String seat;

  private String floor;

  private String customer;

  private String waiter;

  @MappedProperty("ticketid")
  private String ticketId;

  @MappedProperty("tablemoved")
  private int tableMoved;

  @MappedProperty("guests")
  private int guest;

  private LocalDateTime occupied;
}
