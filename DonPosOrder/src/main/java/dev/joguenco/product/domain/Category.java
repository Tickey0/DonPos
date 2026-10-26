package dev.joguenco.product.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Serdeable
@MappedEntity("categories")
public class Category {

  @Id private String id;

  private String name;

  @MappedProperty("catorder")
  private String catOrder;

  @Relation(value = Relation.Kind.ONE_TO_MANY)
  private Set<Product> products;
}
