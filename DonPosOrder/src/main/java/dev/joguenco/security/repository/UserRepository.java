package dev.joguenco.security.repository;

import dev.joguenco.security.domain.User;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;
import java.util.Optional;
import lombok.NonNull;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface UserRepository extends GenericRepository<User, String> {

  @Query("SELECT * from v_users where username = :username")
  Optional<User> findByUsername(@NonNull String username);
}
