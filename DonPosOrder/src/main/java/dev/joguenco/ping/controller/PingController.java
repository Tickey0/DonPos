package dev.joguenco.ping.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/")
public class PingController {

  @Get("/ping")
  HttpResponse<Ping> ping() {
    return HttpResponse.ok(new Ping("pong"));
  }
}
