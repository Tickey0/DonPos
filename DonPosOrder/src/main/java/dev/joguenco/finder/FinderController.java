package dev.joguenco.finder;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/donpos/order/v1/finder")
public class FinderController {

  @Post("/")
  public PlaceDto createPlace(@Body PlaceDto place) {

    return place;
  }
}
