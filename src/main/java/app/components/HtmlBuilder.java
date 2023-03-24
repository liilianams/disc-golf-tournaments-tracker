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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HtmlBuilder {

  private final ApplicationProperties applicationProperties;
  @Autowired
  private final ServletContext servletContext;

  String buildAllTournaments(List<Tournament> tournaments) {
    List<Tournament> sortedTournaments = tournaments.stream().sorted(Tournament.dateComparator).toList();
    return buildHtml("tournaments", sortedTournaments);
  }

  String buildMyTournaments(List<Tournament> tournaments) {
    List<Tournament> result = new ArrayList<>();
    Map<String, List<Tournament>> groupedTournaments = tournaments.stream()
        .sorted(Tournament.dateComparator)
        .collect(Collectors.groupingBy(Tournament::getCustomLocation));

    for (String location : applicationProperties.getLocations()) {
      Optional.ofNullable(groupedTournaments.get(location)).ifPresent(result::addAll);
    }

    return buildHtml("my-tournaments", result);
  }

  private String buildHtml(String templateName, List<Tournament> sortedTournaments) {
    List<String> courses = sortedTournaments.stream().map(Tournament::getCourse).distinct().sorted().toList();
    List<String> cities = sortedTournaments.stream().map(Tournament::getCity).distinct().sorted().toList();
    List<String> states = sortedTournaments.stream().map(Tournament::getState).distinct().sorted().toList();

    // Create Thymeleaf context
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    WebContext ctx = new WebContext(attrs.getRequest(), attrs.getResponse(), servletContext, attrs.getRequest().getLocale());
    ctx.setVariable("tournaments", sortedTournaments);
    ctx.setVariable("locations", applicationProperties.getLocations());
    ctx.setVariable("courses", courses);
    ctx.setVariable("cities", cities);
    ctx.setVariable("states", states);

    // Get the template
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);

    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    return templateEngine.process("templates/" + templateName, ctx);
  }

}
