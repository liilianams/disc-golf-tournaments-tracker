package app.components.dgs;

import app.components.Utils;
import app.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.nio.file.Files.readString;
import static java.nio.file.Path.of;

@Component
@RequiredArgsConstructor
public class DgsScraper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DgsScraper.class);

  private final ApplicationProperties applicationProperties;

  @Cacheable(value = "tournaments", key = "#country")
  public Elements getTournaments(String country) {
    LOGGER.info(String.format("Getting DGS tournaments for country %s at %s, not using cache", country, Utils.getCurrentTime()));
    String url = applicationProperties.getDgsBaseUrl() + "/tournaments/" + country;
    try {
      Document page = applicationProperties.getIsProduction() ?
          Jsoup.connect(url).get() :
          getTestData(country);
      return page.select("div#tournaments-list div.tournament-list");
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return new Elements();
    }
  }

  private Document getTestData(String state) throws IOException {
    return Jsoup.parse(readString(of("src/main/resources/localTestData/test_data_" + state.toLowerCase() + ".html")));
  }

}
