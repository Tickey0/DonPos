package dev.joguenco.security.repository;

import dev.joguenco.security.domain.RefreshToken;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.MYSQL)
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

  @Transactional
  RefreshToken save(
      @NonNull @NotBlank String username,
      @NonNull @NotBlank String refreshToken,
      @NonNull @NotNull Boolean revoked); // <3>

  Optional<RefreshToken> findByRefreshToken(@NonNull @NotBlank String refreshToken);

  long updateByUsername(@NonNull @NotBlank String username, boolean revoked);
}
