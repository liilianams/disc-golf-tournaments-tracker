package app.components;

import app.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scraper {

  private final ApplicationProperties applicationProperties;

  @Cacheable(value = "tournaments", key = "#state")
  public Elements getTournaments(String state) {
    System.out.println("Getting tournaments at " + Utils.getCurrentTime() + ", not using cache");
    String url = applicationProperties.getBaseUrl() + "/tournaments/" + state;
    try {
      Document page = Jsoup.connect(url).get();
      return page.select("div.tournaments-listing-all div.tl");
    } catch (Exception e) {
      e.printStackTrace();
      return new Elements();
    }
  }

}
