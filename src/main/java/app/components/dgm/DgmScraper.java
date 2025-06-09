package app.components.dgm;

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
public class DgmScraper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DgmScraper.class);

  private final ApplicationProperties applicationProperties;

  @Cacheable(value = "tournaments", key = "#countryCode")
  public Elements getTournaments(String countryCode) {
    LOGGER.info("Getting DGM tournaments for country {} at {}, not using cache", countryCode, Utils.getCurrentTime());
    String url = applicationProperties.getDgmBaseUrl() +
      "/competitions_list_server.php?sort_name=date&sort_order=asc" +
      "&country_code=" + countryCode +
      "&date1=" + Utils.getCurrentDate() +
      "&date2=" + Utils.getSameDateNextYear();

    if (!"EE".equalsIgnoreCase(countryCode)) {
      url += "&type=pdga";
    }

    try {
      Document page = applicationProperties.getIsProduction() ?
        Jsoup.connect(url).get() :
        getTestData(countryCode);
      return page.select("table.table-list.clickable tbody tr");
    } catch (Exception e) {
      LOGGER.error("Failed to retrieve data for country {}: {}", countryCode, e.getMessage());
      return new Elements();
    }
  }

  private Document getTestData(String stateShort) throws IOException {
    return Jsoup.parse(readString(of("src/main/resources/localTestData/test_data_" + stateShort.toLowerCase() + ".html")));
  }

}
