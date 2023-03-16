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

@Component
@RequiredArgsConstructor
public class HtmlBuilder {

  private final ApplicationProperties applicationProperties;
  @Autowired
  private final ServletContext servletContext;

  String build(Map<String, List<Tournament>> tournaments) {
    // Sort tournaments by location
    List<Tournament> sortedTournaments = new ArrayList<>();
    for (String location : applicationProperties.getLocations()) {
      sortedTournaments.addAll(tournaments.get(location));
    }

    // Create Thymeleaf context with tournaments data
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    WebContext ctx = new WebContext(attrs.getRequest(), attrs.getResponse(), servletContext, attrs.getRequest().getLocale());
    ctx.setVariable("tournaments", sortedTournaments);
    ctx.setVariable("locations", applicationProperties.getLocations());

    // Get the template
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);

    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    return templateEngine.process("templates/tournaments", ctx);
  }

}
