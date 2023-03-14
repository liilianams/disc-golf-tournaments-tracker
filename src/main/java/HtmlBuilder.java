import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HtmlBuilder {

  static String build(Map<Location, List<Tournament>> tournaments) {
    // Sort tournaments by location
    List<Tournament> sortedTournaments = new ArrayList<>();
    sortedTournaments.addAll(tournaments.get(Location.RAPTORS_KNOLL));
    sortedTournaments.addAll(tournaments.get(Location.BURNABY));
    sortedTournaments.addAll(tournaments.get(Location.SQUAMISH));
    sortedTournaments.addAll(tournaments.get(Location.KAYAK_POINT));
    sortedTournaments.addAll(tournaments.get(Location.NOT_SPECIFIED));

    // Create Thymeleaf context with tournaments data
    Context ctx = new Context();
    ctx.setVariable("tournaments", sortedTournaments);
    ctx.setVariable("locations", Location.values());

    // Get the template
    FileTemplateResolver templateResolver = new FileTemplateResolver();
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);

    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    return templateEngine.process("src/main/templates/tournaments", ctx);
  }

}
