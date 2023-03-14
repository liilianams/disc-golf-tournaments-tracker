import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Scraper {

  private static final String BASE_URL = "https://www.discgolfscene.com/";

  Map<Location, List<Tournament>> scrape(List<String> statesProperties) {
    Map<Location, List<Tournament>> result = new HashMap<>();
    statesProperties.forEach(p -> {
      Map<Location, List<Tournament>> tournaments = getTournaments(p);
      tournaments.forEach((location, list) -> {
        result.merge(location, list, (list1, list2) -> {
          list1.addAll(list2);
          return list1;
        });
      });
    });
    return result;
  }

  private Map<Location, List<Tournament>> getTournaments(String state) {
    String url = BASE_URL + "tournaments/" + state;
    try {
      Document page = Jsoup.connect(url).get();
      Elements tournamentsElements = page.select("div.tournaments-listing-all div.tl");
      List<Tournament> tournaments = mapToTournaments(tournamentsElements);

      return tournaments.stream()
          .sorted(Tournament.dateComparator)
          .collect(Collectors.groupingBy(Tournament::getLocation));
    } catch (Exception e) {
      e.printStackTrace();
      return Map.of();
    }
  }

  private List<Tournament> mapToTournaments(Elements tournamentsElements) {
    List<Tournament> result = new ArrayList<>();
    for (Element element : tournamentsElements) {
      String date = getDate(element);
      String competition = getCompetition(element);
      String registrants = element.select("span.info:contains(Registrants)").text().replace("Registrants: ", "");
      String tier = element.select("span.info.ts").text();
      String location = Utils.capitalize(getLocation(element).toLowerCase());
      String url = getUrl(element);

      Tournament tournament = new Tournament();
      tournament.setName(competition);
      tournament.setDate(date.contains("-") ? null : Utils.convertToLocalDate(date));
      tournament.setDateString(date);
      tournament.setRegistrants(registrants.isBlank() ? 0 : Integer.parseInt(registrants));
      tournament.setTier(tier);
      tournament.setLocation(Location.fromString(location));
      tournament.setUrl(url);
      result.add(tournament);
    }
    return result;
  }

  private String getUrl(Element element) {
    Element link = element.select("a").first();
    return BASE_URL + link.attr("href");
  }

  private String getLocation(Element element) {
    String competition = element.text().toLowerCase();
    if (competition.contains("raptors knoll")) {
      return "Raptors Knoll";
    } else if (competition.contains("burnaby")) {
      return "Burnaby";
    } else if (competition.contains("squamish")) {
      return "Squamish";
    } else if (competition.contains("kayak point")) {
      return "Kayak Point";
    } else {
      return "";
    }
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


}
