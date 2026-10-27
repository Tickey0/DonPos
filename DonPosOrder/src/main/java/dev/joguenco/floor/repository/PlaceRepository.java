package dev.joguenco.floor.repository;

import dev.joguenco.floor.model.Place;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface PlaceRepository extends GenericRepository<Place, String> {

  @Query("select p.* from places p where p.floor = :floorId")
  List<Place> findAllByFloor(@NonNull String floorId);
}
