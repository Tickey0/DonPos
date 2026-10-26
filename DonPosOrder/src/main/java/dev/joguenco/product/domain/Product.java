package dev.joguenco.product.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Serdeable
@MappedEntity("categories")
public class Product {

  @Id private String id;

  private String name;

  @MappedProperty("pricesell")
  private BigDecimal price;

  @MappedProperty("category")
  @Relation(value = Relation.Kind.MANY_TO_ONE)
  private Category category;
}
