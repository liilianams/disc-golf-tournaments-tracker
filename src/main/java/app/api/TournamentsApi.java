package app.api;

import app.components.Scraper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
class TournamentsApi {

  private final Scraper scraper;

  @GetMapping
  String getAllTournaments() {
    return scraper.getAllTournaments();
  }

  @GetMapping("/my-tournaments")
  String getMyTournaments() {
    return scraper.getMyTournaments();
  }

}
