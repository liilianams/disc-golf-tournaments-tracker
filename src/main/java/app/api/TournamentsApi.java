package app.api;

import app.components.Scraper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class TournamentsApi {

  private final Scraper scraper;

  @GetMapping
  String getHtml() {
    return scraper.scrape();
  }

}
