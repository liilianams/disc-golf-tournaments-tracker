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

  private String loginPassword;
  private String dgsBaseUrl;
  private String dgmBaseUrl;
  private Boolean isProduction;
  private Boolean isPasswordCheckEnabled;
  private List<String> states;
  private List<String> statesShort;
  private List<String> favoriteLocations;

}