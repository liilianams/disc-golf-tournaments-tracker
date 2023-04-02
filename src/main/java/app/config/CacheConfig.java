package app.config;

import app.components.Utils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;

@EnableCaching
@Configuration
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("tournaments")));
    return cacheManager;
  }

  @CacheEvict(value = "tournaments", allEntries = true)
  @Scheduled(fixedRateString = "${empty-cache-frequency}")
  public void emptyCache() {
    System.out.println("Emptying tournaments cache at " + Utils.getCurrentTime());
  }

}
