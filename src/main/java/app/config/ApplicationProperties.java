package app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties
public class ApplicationProperties {

  private List<String> states;
  private List<String> locations;
  private String baseUrl;
  private Login login;

  @Getter
  @Setter
  public static class Login {

    private String password;

  }

}