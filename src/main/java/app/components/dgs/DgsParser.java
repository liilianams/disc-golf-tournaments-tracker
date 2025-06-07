package app.components.dgs;

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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DgsParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(DgsParser.class);

  private final ApplicationProperties applicationProperties;
  private final DgsScraper tournamentsScraper;

  public List<Tournament> getTournaments() {
    return applicationProperties.getCountries().stream().flatMap(country -> getTournaments(country).stream()).toList();
  }

  private List<Tournament> getTournaments(String country) {
    try {
      Elements tournamentsElements = tournamentsScraper.getTournaments(country);
      return mapToTournaments(tournamentsElements);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      return List.of();
    }
  }

  public List<Tournament> mapToTournaments(Elements tournamentsElements) {
    List<Tournament> result = new ArrayList<>();
    for (Element element : tournamentsElements) {
      try {
        String name = element.select("span.name").text();

        ParsedDate parsedDate = getDate(element);
        String dayAndMonth = parsedDate.dayAndMonth();
        String dayOfWeek = parsedDate.dayOfWeek();

        String tier = getTier(element);
        String url = getUrl(element);

        String location = element.select("span.info i.fa-map-marker-alt").first().parent().ownText();
        String course = element.select("span.info i.fa-map").first().parent().ownText();
        String[] cityState = location.split(", ");
        String city = cityState.length > 0 ? cityState[0] : "N/A";
        String country = cityState.length > 1 ? cityState[1] : "N/A";

        boolean isRegistrationOpen = isRegistrationOpen(element);
        int registrants = getRegistrants(element);

        Tournament tournament = new Tournament();
        tournament.setName(name);
        tournament.setDate(Utils.convertToLocalDate(parsedDate.dayAndMonth, Clock.fixed(Instant.now(), ZoneId.of("UTC"))));
        tournament.setDayOfWeek(dayOfWeek);
        tournament.setDayAndMonth(dayAndMonth);
        tournament.setDateString(parsedDate.toString());
        tournament.setRegistrants(registrants);
        tournament.setIsRegistrationOpen(isRegistrationOpen);
        tournament.setTier(tier);
        tournament.setCourse(course);
        tournament.setCity(city);
        tournament.setState("");
        tournament.setCountry(country);
        tournament.setLocation(location);
        tournament.setUrl(url);

        result.add(tournament);
      } catch (Exception e) {
        LOGGER.error(e.getMessage());
      }
    }
    return result;
  }

  private String getUrl(Element element) {
    String url = element.select("a").attr("href");
    return !url.startsWith("http") ? applicationProperties.getDgsBaseUrl() + url : url;
  }

  private int getRegistrants(Element element) {
    try {
      Element regElement = element.select("i.fa-user-group + b").first();
      if (regElement != null) {
        String[] parts = regElement.text().split("/");
        return Integer.parseInt(parts[0].trim());
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    return 0;
  }

  public ParsedDate getDate(Element element) {
    try {
      Element dateContainer = element.select(".list-date-range").first();
      if (dateContainer != null) {
        List<Element> spans = dateContainer.select("span");

        String monthPart = spans.get(0).text().trim();     // "Aug" or "Aug-Sep"
        String dayPart = spans.get(1).text().trim();       // "1" or "1-14"
        String weekDayPart = spans.get(2).text().trim();   // "Mon" or "Mon-Wed"

        String dayAndMonth = getDayAndMonthString(monthPart, dayPart);

        return new ParsedDate(dayAndMonth, weekDayPart);
      }
    } catch (Exception e) {
      LOGGER.error("Failed to parse date", e);
    }
    return new ParsedDate("Invalid date", "");
  }

  private String getDayAndMonthString(String monthPart, String dayPart) {
    String[] months = monthPart.split("-");
    String[] days = dayPart.split("-");

    String dayAndMonth;

    if (months.length == 2 && days.length == 2) {
      dayAndMonth = String.format("%s %s-%s %s", months[0], days[0], months[1], days[1]);
    } else if (months.length == 1 && days.length == 2) {
      dayAndMonth = String.format("%s %s-%s", months[0], days[0], days[1]);
    } else if (months.length == 1 && days.length == 1) {
      dayAndMonth = String.format("%s %s", months[0], days[0]);
    } else if (months.length == 2 && days.length == 1) {
      dayAndMonth = String.format("%s %s-%s %s", months[0], days[0], months[1], days[0]);
    } else {
      // rare case, interpret as: Aug 1-Sep 1
      dayAndMonth = String.format("%s %s-%s %s", months[0], days[0], months[1], days[0]);
    }
    return dayAndMonth;
  }

  private String getTier(Element element) {
    Element tierElement = element.select("span.list-tier").first();
    return tierElement != null ? tierElement.text().trim() : "";
  }

  private boolean isRegistrationOpen(Element tournamentDiv) {
    return tournamentDiv.select("i.fas.fa-play.list-reg-open").first() != null;
  }

  public record ParsedDate(String dayAndMonth, String dayOfWeek) {

    @Override
    public String toString() {
      return dayAndMonth + " " + dayOfWeek;
    }
  }

}