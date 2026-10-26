package dev.joguenco.floor;

import dev.joguenco.floor.domain.Floor;
import dev.joguenco.floor.domain.Place;
import dev.joguenco.floor.repository.FloorRepository;
import dev.joguenco.floor.repository.PlaceRepository;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/donpos/order/v1/floors")
public class FloorController {

  protected final FloorRepository floorRepository;
  protected final PlaceRepository placeRepository;

  public FloorController(FloorRepository floorRepository, PlaceRepository placeRepository) {
    this.floorRepository = floorRepository;
    this.placeRepository = placeRepository;
  }

  @Get("/")
  public List<Floor> listFloor() {
    return floorRepository.findAll();
  }

  @Get("/places/{floorId}")
  public List<Place> listPlace(String floorId) {
    return placeRepository.findAllByFloor(floorId);
  }
}
