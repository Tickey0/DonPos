package dev.joguenco.product.repository;

import dev.joguenco.product.domain.Category;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface CategoryRepository extends PageableRepository<Category, String> {}
