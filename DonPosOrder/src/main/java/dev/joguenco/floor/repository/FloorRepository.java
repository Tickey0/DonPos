package dev.joguenco.floor.repository;

import dev.joguenco.floor.domain.Floor;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface FloorRepository extends PageableRepository<Floor, String> {}
