package dev.joguenco.security;

import dev.joguenco.security.repository.UserRepository;
import dev.joguenco.util.Hashcypher;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;

@Singleton
class AuthenticationProviderUserPassword<B> implements HttpRequestAuthenticationProvider<B> {

  protected final UserRepository userRepository;

  AuthenticationProviderUserPassword(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public AuthenticationResponse authenticate(
      @Nullable HttpRequest<B> httpRequest,
      @NonNull AuthenticationRequest<String, String> authenticationRequest) {
    final var username = authenticationRequest.getIdentity();
    final var password = authenticationRequest.getSecret();

    final var user = userRepository.findByUsername(username).orElse(null);

    if (user == null) {
      return AuthenticationResponse.failure(AuthenticationFailureReason.USER_NOT_FOUND);
    }
    if (!user.status()) {
      return AuthenticationResponse.failure(AuthenticationFailureReason.ACCOUNT_LOCKED);
    }

    if (Hashcypher.authenticate(password, user.password())) {
      return AuthenticationResponse.success(username);
    }

    return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
  }
}
