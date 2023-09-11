package app.components;

import app.config.ApplicationProperties;
import app.model.Tournament;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
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

  public String getAllTournaments(Boolean isMobile) {
    return htmlBuilder.buildAllTournaments(getTournaments(), isMobile);
  }

  public String getMyTournaments(Boolean isMobile) {
    return htmlBuilder.buildMyTournaments(getTournaments(), isMobile);
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

  private List<Tournament> mapToTournaments(Elements tournamentsElements) {
    List<Tournament> result = new ArrayList<>();
    for (Element element : tournamentsElements) {
      String date = getDate(element);
      String dayOfWeek = getDayOfWeek(date);
      String dayAndMonth = getDayAndMonth(date);
      String competition = getCompetition(element);
      String registrants = element.select("span.info:contains(Registrants)").text().replace("Registrants: ", "");
      String tier = element.select("span.info.ts").text();
      String customLocation = getCustomLocation(element);
      String url = getUrl(element);
      String hostedBy = getHostedBy(element);

      String[] locationParts = getLocationParts(element);
      String course = locationParts[0];
      String[] cityAndState = locationParts[1].split(", ");
      String city = cityAndState[0];
      String state = cityAndState[1];

      Tournament tournament = new Tournament();
      tournament.setName(competition);
      tournament.setDate(Utils.convertToLocalDate(date, Clock.fixed(Instant.now(), ZoneId.of("UTC"))));
      tournament.setDayOfWeek(dayOfWeek);
      tournament.setDayAndMonth(dayAndMonth);
      tournament.setDateString(date);
      tournament.setRegistrants(registrants.isBlank() ? 0 : Integer.parseInt(registrants));
      tournament.setTier(tier);
      tournament.setCourse(course);
      tournament.setCity(city);
      tournament.setState(state);
      tournament.setHostedBy(hostedBy);
      tournament.setCustomLocation(customLocation);
      tournament.setUrl(url);
      result.add(tournament);
    }
    return result;
  }

  private String getDayAndMonth(String date) {
    String[] dateParts = date.split(" ");
    return dateParts[0] + " " + dateParts[1];
  }

  private String getDayOfWeek(String date) {
    return date.split(" ")[2];
  }

  private String getUrl(Element element) {
    Element link = element.select("a").first();
    return applicationProperties.getBaseUrl() + link.attr("href");
  }

  private String getCustomLocation(Element element) {
    String competition = element.text().toLowerCase();
    for (String searchString : applicationProperties.getLocations()) {
      if (competition.contains(searchString.toLowerCase())) {
        return Utils.capitalize(searchString);
      }
    }
    return "N/A";
  }

  private String getCompetition(Element tournament) {
    Elements em = tournament.select("em");
    return em.isEmpty() ? em.select("trego").text() : em.text();
  }

  private String getDate(Element tournament) {
    StringBuilder result = new StringBuilder();
    List<Node> elements = tournament.select("span.t-date").first().childNodes();
    elements.forEach(e -> {
      String whitespace = "\\s+";
      if (e.hasAttr("text")) {
        result.append(e.attr("text").replaceAll(whitespace, "")).append(" ");
      } else {
        result.append(e.childNodes().get(0).attr("text").replaceAll(whitespace, "")).append(" ");
      }
    });
    return result.toString().trim();
  }

  private String getHostedBy(Element element) {
    String hostedByString = element.select("span:contains(hosted by)").text();
    return hostedByString.replace("hosted by ", "").trim();
  }

  private String[] getLocationParts(Element element) {
    String locationString = element.select("span:not(:containsOwn(Sat)):not(:containsOwn(sat))")
        .select(":matchesOwn(\\bat\\b)").text();
    return locationString.replaceFirst("at ", "").split(" · ");
  }

}
