package app.components.dgs;

import app.components.Utils;
import app.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.nio.file.Files.readString;
import static java.nio.file.Path.of;

@Component
@RequiredArgsConstructor
public class DgsScraper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DgsScraper.class);

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final ApplicationProperties applicationProperties;
  private final CacheManager cacheManager;

  @Cacheable(value = "tournaments", key = "#country")
  public Elements getTournaments(String country) {
    LOGGER.info("Getting DGS tournaments for country {} at {}, not using cache", country, Utils.getCurrentTime());

    try {
      return fetchTournaments(country);
    } catch (Exception e) {
      LOGGER.error("Failed to retrieve data for country {}: {}", country, e.getMessage());
      retryInFiveMinutes(country);
      return new Elements();
    }
  }

  private Document getTestData(String state) throws IOException {
    return Jsoup.parse(readString(of("src/main/resources/localTestData/test_data_" + state.toLowerCase() + ".html")));
  }

  private void retryInFiveMinutes(String country) {
    LOGGER.warn("Scheduling a retry for country {} in 5 minutes.", country);

    scheduler.schedule(
      () -> {
        try {
          LOGGER.info("Retrying tournament fetch for country {}", country);
          Elements tournaments = fetchTournaments(country);

          // Add to cache
          Cache cache = cacheManager.getCache("tournaments");
          if (cache != null) cache.put(country, tournaments);

          LOGGER.info("Retry successful for country {}", country);
        } catch (Exception e) {
          LOGGER.error("Retry failed for country {}: {}", country, e.getMessage());
        }
      }, 5, TimeUnit.MINUTES
    );
  }

  private Elements fetchTournaments(String country) throws IOException {
    String url = applicationProperties.getDgsBaseUrl() + "/tournaments/" + country;
    Document page = applicationProperties.getIsProduction() ? Jsoup.connect(url).get() : getTestData(country);
    return page.select("div#tournaments-list div.tournament-list");
  }

}
