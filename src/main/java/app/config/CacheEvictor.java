package app.config;

import app.components.Utils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CacheEvictor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheEvictor.class);

  private final CacheManager cacheManager;

  @Scheduled(fixedRateString = "${empty-cache-frequency}")
  public void emptyCache() {
    Cache cache = cacheManager.getCache("tournaments");
    if (cache == null) {
      LOGGER.warn("Tournaments cache not found");
      return;
    }

    // Print all keys before eviction
    Object nativeCache = cache.getNativeCache();
    LOGGER.info("Current tournaments cache keys before eviction: {}", getCacheKeys(nativeCache));

    // Evict all entries
    cache.clear();
    LOGGER.info("Emptied tournaments cache at {}", Utils.getCurrentTime());

    // Print all keys after eviction
    nativeCache = cache.getNativeCache();
    LOGGER.info("Current tournaments cache keys after eviction: {}", getCacheKeys(nativeCache));
  }

  private String getCacheKeys(Object nativeCache) {
    if (nativeCache instanceof Map<?, ?>) {
      return ((Map<?, ?>) nativeCache).keySet().toString();
    }

    return "Unable to return tournaments cache keys";
  }


}
