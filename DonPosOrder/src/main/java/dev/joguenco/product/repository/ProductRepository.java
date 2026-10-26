package dev.joguenco.product.repository;

import dev.joguenco.product.domain.Product;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface ProductRepository extends GenericRepository<Product, String> {

  @Query("select p.* from products p where p.category = :categoryId")
  List<Product> findAllByCategory(@NonNull String categoryId);
}
