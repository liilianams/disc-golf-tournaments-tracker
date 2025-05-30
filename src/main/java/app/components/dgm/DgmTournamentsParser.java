package app.components.dgm;

import app.components.Utils;
import app.config.ApplicationProperties;
import app.model.Tournament;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class DgmTournamentsParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(DgmTournamentsParser.class);

  private final ApplicationProperties applicationProperties;
  private final DgmScraper dgmTournamentsScraper;

  public List<Tournament> getTournaments() {
    return applicationProperties.getStatesShort().stream().flatMap(state -> getTournaments(state).stream()).toList();
  }

  private List<Tournament> getTournaments(String state) {
    try {
      Elements tournamentsElements = dgmTournamentsScraper.getTournaments(state);
      return parseTournaments(tournamentsElements);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return List.of();
    }
  }

  private List<Tournament> parseTournaments(Elements elements) {
    List<Tournament> tournaments = new ArrayList<>();

    for (Element element : elements) {
      Tournament tournament = mapToTournament(element);
      if (tournament != null) {
        tournaments.add(tournament);
      }
    }

    return tournaments;
  }

  private Tournament mapToTournament(Element element) {
    Elements cells = element.select("td");
    if (cells.size() < 8) return null;

    String url = getUrl(element);
    String name = cells.get(0).select("b").text().trim();

    String locationLine = cells.get(0).ownText().trim();
    String[] locationParts = locationLine.split(",");
    String state = locationParts.length > 0 ? locationParts[0].trim() : "";
    String city = locationParts.length > 1 ? locationParts[1].trim() : "";

    String dateString = cells.get(1).text().trim();
    ParsedDate parsedDate = getParsedDate(dateString);
    String dayAndMonth = parsedDate.dayAndMonth();
    String dayOfWeek = parsedDate.dayOfWeek();

    String tier = cells.get(2).text().trim();
    String course = cells.get(3).text().trim();

    Integer registrants = getRegistrants(cells.get(4).text().trim());
    boolean isRegistrationOpen = isRegistrationOpen(cells.get(7).text().toLowerCase());

    Tournament tournament = new Tournament();
    tournament.setName(name);
    tournament.setUrl(url);
    tournament.setDate(Utils.convertToLocalDate(parsedDate.dayAndMonth, Clock.fixed(Instant.now(), ZoneId.of("UTC"))));
    tournament.setDateString(dateString);
    tournament.setDayAndMonth(dayAndMonth);
    tournament.setDayOfWeek(dayOfWeek);
    tournament.setTier(tier);
    tournament.setCourse(course);
    tournament.setCity(city);
    tournament.setState(state);
    tournament.setRegistrants(registrants);
    tournament.setIsRegistrationOpen(isRegistrationOpen);

    return tournament;
  }

  public ParsedDate getParsedDate(String dateString) {
    try {
      DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("MM/dd/yy");
      DateTimeFormatter outputDayAndMonthFormat = DateTimeFormatter.ofPattern("MMM d");
      DateTimeFormatter outputDayOfWeekFormat = DateTimeFormatter.ofPattern("EEE");

      String[] parts = dateString.split("-");

      LocalDate start = LocalDate.parse(parts[0].trim().split(" ")[0], inputFormat);
      LocalDate end = (parts.length > 1)
        ? LocalDate.parse(parts[1].trim().split(" ")[0], inputFormat)
        : start;

      String dayAndMonth = outputDayAndMonthFormat.format(start);
      String dayOfWeek = outputDayOfWeekFormat.format(start);

      if (!start.equals(end)) {
        dayAndMonth += "-" + outputDayAndMonthFormat.format(end);
        dayOfWeek += "-" + outputDayOfWeekFormat.format(end);
      }

      return new ParsedDate(dayAndMonth, dayOfWeek);

    } catch (Exception e) {
      LOGGER.error("{}; {}", e.getMessage(), dateString);
      return new ParsedDate("Invalid date", "");
    }
  }

  private boolean isRegistrationOpen(String registrationText) {
    return registrationText.contains("start") || registrationText.contains("open");
  }

  private String getUrl(Element row) {
    String onclickAttr = row.attr("onclick");
    String url = null;
    Matcher matcher = Pattern.compile("'(.*?)'").matcher(onclickAttr);
    if (matcher.find()) {
      url = applicationProperties.getDgmBaseUrl() + matcher.group(1);
    }
    return url;
  }

  private Integer getRegistrants(String registrantsString) {
    Integer registrants = null;
    try {
      registrants = Integer.parseInt(registrantsString);
    } catch (NumberFormatException e) {
      LOGGER.error(e.getMessage());
    }
    return registrants;
  }

  public record ParsedDate(String dayAndMonth, String dayOfWeek) {

    @Override
    public String toString() {
      return dayAndMonth + " " + dayOfWeek;
    }
  }

}
