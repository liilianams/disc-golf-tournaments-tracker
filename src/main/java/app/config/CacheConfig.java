package app.config;

import app.components.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@EnableCaching
@Configuration
public class CacheConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfig.class);

  @Autowired
  private CacheManager cacheManager;

  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(List.of(new ConcurrentMapCache("tournaments")));
    return cacheManager;
  }

  @CacheEvict(value = "tournaments", allEntries = true)
  @Scheduled(fixedRateString = "${empty-cache-frequency}")
  public void emptyCache() {
    LOGGER.info("Emptying tournaments cache at {}", Utils.getCurrentTime());
  }

}
