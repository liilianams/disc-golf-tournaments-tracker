package app;

import app.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class App {

  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  private final ApplicationProperties applicationProperties;

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void logProperties() {
    LOGGER.info(applicationProperties.getIsProduction() ?
        "Application is running in production mode." :
        "Application is running in development mode.");
    LOGGER.info(applicationProperties.getIsPasswordCheckEnabled() ?
        "Password check is enabled." :
        "Password check is disabled.");
  }

}
