package app.components;

import app.config.ApplicationProperties;
import app.model.Tournament;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HtmlBuilder {

  private final ApplicationProperties applicationProperties;
  @Autowired
  private final ServletContext servletContext;

  String buildAllTournaments(List<Tournament> tournaments, Boolean isMobile) {
    List<Tournament> sortedTournaments = tournaments.stream().sorted(Tournament.DATE_COMPARATOR).toList();
    return buildTournamentsHtml("tournaments", sortedTournaments, isMobile);
  }

  String buildMyTournaments(List<Tournament> tournaments, Boolean isMobile) {
    List<String> myLocations = applicationProperties.getLocations();
    List<Tournament> myTournaments = tournaments.stream()
        .sorted(Tournament.DATE_COMPARATOR)
        .filter(a -> myLocations.contains(a.getCustomLocation()))
        .toList();
    return buildTournamentsHtml("my-tournaments", myTournaments, isMobile);
  }

  public String buildLogin() {
    return buildHtml("login", Map.of());
  }

  private String buildTournamentsHtml(String templateName, List<Tournament> sortedTournaments, Boolean isMobile) {
    List<String> courses = sortedTournaments.stream().map(Tournament::getCourse).distinct().sorted().toList();
    List<String> cities = sortedTournaments.stream().map(Tournament::getCity).distinct().sorted().toList();
    List<String> states = sortedTournaments.stream().map(Tournament::getState).distinct().sorted().toList();
    Map<String, Object> contextParams = Map.of(
        "tournaments", sortedTournaments,
        "locations", applicationProperties.getLocations(),
        "courses", courses,
        "cities", cities,
        "states", states,
        "isMobile", isMobile
    );
    return buildHtml(templateName, contextParams);
  }

  private String buildHtml(String templateName, Map<String, Object> contextParams) {
    // Create Thymeleaf context
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    WebContext ctx = new WebContext(attrs.getRequest(), attrs.getResponse(), servletContext, attrs.getRequest().getLocale());
    ctx.setVariables(contextParams);

    // Get the template
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);

    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    return templateEngine.process("templates/" + templateName, ctx);
  }

}
