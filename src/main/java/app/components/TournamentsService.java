package app.components;

import app.config.ApplicationProperties;
import app.model.Tournament;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TournamentsService {

  private final ApplicationProperties applicationProperties;
  private final HtmlBuilder htmlBuilder;
  private final Scraper tournamentsScraper;

  public String getAllTournaments() {
    return htmlBuilder.buildAllTournaments(getTournaments());
  }

  public String getFavoriteTournaments() {
    return htmlBuilder.buildFavoriteTournaments(getTournaments());
  }

  private List<Tournament> getTournaments() {
    return applicationProperties.getStates().stream().flatMap(state -> getTournaments(state).stream()).toList();
  }

  private List<Tournament> getTournaments(String state) {
    try {
      Elements tournamentsElements = tournamentsScraper.getTournaments(state);
      return mapToTournaments(tournamentsElements);
    } catch (Exception e) {
      e.printStackTrace();
      return List.of();
    }
  }

  public List<Tournament> mapToTournaments(Elements tournamentsElements) {
    List<Tournament> result = new ArrayList<>();
    for (Element element : tournamentsElements) {
      try {
        String name = element.select("span.name").text();
        String date = getDate(element);
        String dayOfWeek = getDayOfWeek(date);
        String dayAndMonth = getMonthAndDay(date);

        String tier = getTier(element);
        String url = element.select("a").attr("href");
        if (!url.startsWith("http")) {
          url = applicationProperties.getDgsBaseUrl() + url;
        }

        String location = element.select("span.info i.fa-map-marker-alt").first().parent().ownText();
        String course = element.select("span.info i.fa-map").first().parent().ownText();
        String[] cityState = location.split(", ");
        String city = cityState.length > 0 ? cityState[0] : "N/A";
        String state = cityState.length > 1 ? cityState[1] : "N/A";

        String hostedBy = "N/A";
        boolean isRegistrationOpen = true;

        int registrants = getRegistrants(element);

        Tournament tournament = new Tournament();
        tournament.setName(name);
        tournament.setDate(Utils.convertToLocalDate(date, Clock.fixed(Instant.now(), ZoneId.of("UTC"))));
        tournament.setDayOfWeek(dayOfWeek);
        tournament.setDayAndMonth(dayAndMonth);
        tournament.setDateString(date);
        tournament.setRegistrants(registrants);
        tournament.setIsRegistrationOpen(isRegistrationOpen);
        tournament.setTier(tier);
        tournament.setCourse(course);
        tournament.setCity(city);
        tournament.setState(state);
        tournament.setHostedBy(hostedBy);
        tournament.setCustomLocation(getCustomLocation(name));
        tournament.setUrl(url);

        result.add(tournament);
      } catch (Exception e) {
        e.printStackTrace(); // Ignore broken entries
      }
    }
    return result;
  }

  private int getRegistrants(Element element) {
    try {
      Element regElement = element.select("i.fa-user-group + b").first();
      if (regElement != null) {
        String[] parts = regElement.text().split("/");
        return Integer.parseInt(parts[0].trim());
      }
    } catch (Exception e) {
      // log if needed
    }
    return 0;
  }

  public String getDate(Element element) {
    try {
      Element dateContainer = element.select(".list-date-range").first();
      if (dateContainer != null) {
        List<Element> spans = dateContainer.select("span");
        String monthPart = spans.get(0).text().trim();     // "Aug" or "Aug-Sep"
        String dayPart = spans.get(1).text().trim();       // "1" or "1-14"

        String[] months = monthPart.split("-");
        String[] days = dayPart.split("-");

        if (months.length == 2 && days.length == 2) {
          return String.format("%s %s-%s %s", months[0], days[0], months[1], days[1]);
        } else if (months.length == 1 && days.length == 2) {
          return String.format("%s %s-%s", months[0], days[0], days[1]);
        } else if (months.length == 1 && days.length == 1) {
          return String.format("%s %s", months[0], days[0]);
        } else if (months.length == 2 && days.length == 1) {
          // rare case, interpret as: Aug 1 - Sep 1
          return String.format("%s %s-%s %s", months[0], days[0], months[1], days[0]);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "Unknown";
  }

  private String getDayOfWeek(String date) {
    return ""; // Too variable in new format to reliably extract
  }

  private String getMonthAndDay(String date) {
    return date; // Already in suitable format
  }

  private String getTier(Element element) {
    Element tierElement = element.select("span.list-tier").first();
    return tierElement != null ? tierElement.text().trim() : "";
  }

  private String getCustomLocation(String competition) {
    for (String searchString : applicationProperties.getFavoriteLocations()) {
      if (competition.toLowerCase().contains(searchString.toLowerCase())) {
        return Utils.capitalize(searchString);
      }
    }
    return "N/A";
  }
}