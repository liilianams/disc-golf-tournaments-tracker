package app.components;

import app.config.ApplicationProperties;
import app.model.Tournament;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TournamentsServiceTest {

  @Mock private HtmlBuilder htmlBuilder;
  @Mock private Scraper tournamentsScraper;

  private TournamentsService tournamentsService;

  @BeforeEach
  void setUp() {
    ApplicationProperties props = new ApplicationProperties();
    props.setFavoriteLocations(List.of());
    tournamentsService = new TournamentsService(props, htmlBuilder, tournamentsScraper);
  }

  @Test
  void mapToTournaments() {
    // Given
    String elements = "<div class=\"tournaments-listing-all\">\n" +
      "  <div class=\"tournament-B \">\n" +
      "    <div class=\"tl Sep\">\n" +
      "      <a href=\"/tournaments/Placeholder_Clearwater_Open_2023\">\n" +
      "        <span class=\"t-date\">September <span>29-1</span> Fri - Sun</span>\n" +
      "        <em class=\"\">Harvest Huck (BC Provincial Championships) presented by Best Western Plus &amp; Quality Inn - Sponsored by Discraft</em>\n" +
      "        <span class=\"info ts\">B-tier</span>\n" +
      "        <span class=\"info cash\">Added cash: C$15,000</span>\n" +
      "        <span class=\"info\">Registrants: 0</span>\n" +
      "        <span>at Clearwater Ski Hill · Clearwater, BC</span>\n" +
      "        <span>hosted by Doomsday Discovery Tour</span>\n" +
      "        <b></b>\n" +
      "      </a>\n" +
      "    </div>\n" +
      "  </div>\n" +
      "  <div class=\"tournament-U \">\n" +
      "    <div class=\"tl Oct\">\n" +
      "      <a href=\"/tournaments/Doomsday_Disc_Golf_Series_Dick_Hart_2023\">\n" +
      "        <span class=\"t-date\">October <span>15</span> Sunday</span>\n" +
      "        <em class=\"\">Doomsday Disc Golf Series - Dick Hart</em>\n" +
      "        <span class=\"info ts\"></span>\n" +
      "        <span>at Dick Hart Memorial Park &middot; Kamloops, BC</span>\n" +
      "        <span>hosted by Doomsday Discovery Tour</span>\n" +
      "        <b></b>\n" +
      "      </a>\n" +
      "    </div>\n" +
      "  </div>\n" +
      "</div>";

    Document page = Jsoup.parse(elements);
    Elements tournamentsElements = page.select("div.tournaments-listing-all div.tl");

    // When
    List<Tournament> tournaments = tournamentsService.mapToTournaments(tournamentsElements);

    // Then
    assertThat(tournaments).hasSize(2);

    Tournament tournament = tournaments.get(0);
    assertThat(tournament.getName()).isEqualTo("Harvest Huck (BC Provincial Championships) presented by Best Western Plus & Quality Inn - Sponsored by Discraft");
    assertThat(tournament.getTier()).isEqualTo("B-tier");
    assertThat(tournament.getDate().getMonthValue()).isEqualTo(10);
    assertThat(tournament.getDate().getDayOfMonth()).isEqualTo(1);
    assertThat(tournament.getDateString()).isEqualTo("September 29-1 Fri-Sun");
    assertThat(tournament.getDayAndMonth()).isEqualTo("Sep 29-1");
    assertThat(tournament.getDayOfWeek()).isEqualTo("Fri-Sun");
    assertThat(tournament.getRegistrants()).isZero();
    assertThat(tournament.getIsRegistrationOpen()).isNotNull();
    assertThat(tournament.getCourse()).isEqualTo("Clearwater Ski Hill");
    assertThat(tournament.getCity()).isEqualTo("Clearwater");
    assertThat(tournament.getState()).isEqualTo("BC");
    assertThat(tournament.getCustomLocation()).isNotBlank();
    assertThat(tournament.getHostedBy()).isEqualTo("Doomsday Discovery Tour");
    assertThat(tournament.getUrl()).isEqualTo("null/tournaments/Placeholder_Clearwater_Open_2023");

    Tournament tournament2 = tournaments.get(1);
    assertThat(tournament2.getName()).isEqualTo("Doomsday Disc Golf Series - Dick Hart");
    assertThat(tournament2.getTier()).isEmpty();
    assertThat(tournament2.getDate().getMonthValue()).isEqualTo(10);
    assertThat(tournament2.getDate().getDayOfMonth()).isEqualTo(15);
    assertThat(tournament2.getDateString()).isEqualTo("October 15 Sunday");
    assertThat(tournament2.getDayAndMonth()).isEqualTo("Oct 15");
    assertThat(tournament2.getDayOfWeek()).isEqualTo("Sun");
    assertThat(tournament2.getIsRegistrationOpen()).isNotNull();
    assertThat(tournament2.getCourse()).isEqualTo("Dick Hart Memorial Park");
    assertThat(tournament2.getCity()).isEqualTo("Kamloops");
    assertThat(tournament2.getState()).isEqualTo("BC");
    assertThat(tournament2.getCustomLocation()).isNotBlank();
    assertThat(tournament2.getHostedBy()).isEqualTo("Doomsday Discovery Tour");
    assertThat(tournament2.getUrl()).isEqualTo("null/tournaments/Doomsday_Disc_Golf_Series_Dick_Hart_2023");
  }
}
