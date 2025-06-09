package app.components;

import app.components.dgs.DgsParser;
import app.components.dgs.DgsScraper;
import app.config.ApplicationProperties;
import app.model.Tournament;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;

import java.util.List;

import static app.components.dgs.DgsParser.ParsedDate;
import static org.assertj.core.api.Assertions.assertThat;

class TournamentsServiceTest {

  @Mock
  private DgsScraper tournamentsScraper;

  private DgsParser tournamentsService;

  @BeforeEach
  void setUp() {
    ApplicationProperties props = new ApplicationProperties();
    props.setFavoriteLocations(List.of());
    tournamentsService = new DgsParser(props, tournamentsScraper);
  }

  @ParameterizedTest(name = "Given date string {0}, then local date is {1}")
  @CsvSource({
    "Sep, 1, Wed, Sep 1 Wed",
    "Sep-Oct, 29-1, Sat-Sun, Sep 29-Oct 1 Sat-Sun",
    "Oct, 1-4, Sun-Wed, Oct 1-4 Sun-Wed",
    "Sep-Oct, 1, Sun-Sun, Sep 1-Oct 1 Sun-Sun"
  })
  void getDate(String givenMonthString, String givenDayString, String givenDayOfWeekString, String expectedDate) {
    // Given
    String html = String.format(
      """
      <div id="tournaments-list">
        <div class="tournament-list list-record ">
          <div class="d-none d-lg-block list-date-range flex-shrink-0">
            <div class="d-flex flex-column text-center">
              <span class="text-uppercase text-muted">%s</span>
              <span>%s</span>
              <span class="text-uppercase text-muted">%s</span>
            </div>
          </div>
        </div>
      </div>
      """, givenMonthString, givenDayString, givenDayOfWeekString
    );

    Document page = Jsoup.parse(html);
    Element element = page.select("div#tournaments-list div.tournament-list").get(0);

    // When
    ParsedDate actualDate = tournamentsService.getDate(element);

    // Then
    assertThat(actualDate.toString()).isEqualTo(expectedDate);
  }

  @Test
  void mapToTournaments() {
    // Given
    String elements = "<div id=\"tournaments-list\">\n" +
      "  <div class=\"tournament-list list-record \">\n" +
      "    <a href=\"https://www.discgolfscene.com/tournaments/Monday_Qualifier_for_the_Coolbet_presents_European_Disc_Golf_Festival_2025_PDGA_Major\"\n" +
      "       class=\"d-lg-flex align-items-center\">\n" +
      "      <div class=\"d-none d-lg-block list-date-range flex-shrink-0\">\n" +
      "        <div class=\"d-flex flex-column text-center\">\n" +
      "          <span class=\"text-uppercase text-muted\">Jul</span>\n" +
      "          <span>14</span>\n" +
      "          <span class=\"text-uppercase text-muted\"> Mon  </span>\n" +
      "        </div>\n" +
      "      </div>\n" +
      "      <em class=\"logo\">\n" +
      "        <img src=\"https://m.discgolfscene.com/logos/clubs/15028/team-discgolf-estonia-mtu-2cee0e4d8de0.jpg\">\n" +
      "      </em>\n" +
      "      <span class=\"detail\">\n" +
      "        <span class=\"name\">Monday Qualifier for the Coolbet presents European Disc Golf Festival 2025 | PDGA Major</span>\n" +
      "        <i class=\"fas fa-play list-reg-open\"></i>" +
      "        <span class=\"info\">Mon, Jul 14, 2025</span>\n" +
      "        <span class=\"info\">\n" +
      "          <span class=\"d-none d-lg-inline-block mr-2\"><i class=\"far fa-map count\"></i>Tallinn Song Festival Grounds</span>\n" +
      "          <span class=\"mr-2\"><i class=\"far fa-map-marker-alt count\"></i>Tallinn, Estonia</span>\n" +
      "          <i class=\"far fa-flying-disc count\"></i>\n" +
      "          <b>1</b>\n" +
      "        </span>\n" +
      "        <span class=\"info\">\n" +
      "          <i class=\"fa fa-play small font-weight-bold\"></i>Mon, Jun 9 at 12:00am      BST\n" +
      "        </span>\n" +
      "      </span>\n" +
      "    </a>\n" +
      "  </div>\n" +
      "  <div class=\"tournament-list list-record \">\n" +
      "    <a href=\"https://www.discgolfscene.com/tournaments/European_Disc_Golf_Festival_2025\"\n" +
      "       class=\"d-lg-flex align-items-center\">\n" +
      "      <div class=\"d-none d-lg-block list-date-range flex-shrink-0\">\n" +
      "        <div class=\"d-flex flex-column text-center\">\n" +
      "          <span class=\"text-uppercase text-muted\">Jul</span>\n" +
      "          <span>17-20</span>\n" +
      "          <span class=\"text-uppercase text-muted\">Thu-Sun</span>\n" +
      "        </div>\n" +
      "      </div>\n" +
      "      <em class=\"logo\">\n" +
      "        <img src=\"https://m.discgolfscene.com/logos/tournaments/92332/European_Disc_Golf_Festival_20251745479890.jpg\">\n" +
      "      </em>\n" +
      "      <span class=\"detail\">\n" +
      "        <span class=\"name\">Coolbet presents: European Disc Golf Festival 2025</span>\n" +
      "        <span class=\"info\">PDGA Major · Thu-Sun, Jul 17-20, 2025</span>\n" +
      "        <span class=\"info\">\n" +
      "          <span class=\"d-none d-lg-inline-block mr-2\"><i class=\"far fa-map count\"></i>Tallinn Song Festival Grounds</span>\n" +
      "          <span class=\"mr-2\"><i class=\"far fa-map-marker-alt count\"></i>Tallinn, Estonia</span>\n" +
      "          <i class=\"far fa-user-group count\"></i><b>156 / 156</b> &nbsp;\n" +
      "          <i class=\"far fa-flying-disc count\"></i><b>4</b>\n" +
      "        </span>\n" +
      "      </span>\n" +
      "      <span class=\"info d-none d-lg-inline-block list-tier\">\n" +
      "        <img src=\"/app/images/global/pdga-wordmark.svg\">\n" +
      "        <br>\n" +
      "        Major\n" +
      "      </span>\n" +
      "    </a>\n" +
      "  </div>\n" +
      "</div>\n";

    Document page = Jsoup.parse(elements);
    Elements tournamentsElements = page.select("div#tournaments-list div.tournament-list");

    // When
    List<Tournament> tournaments = tournamentsService.parseTournaments(tournamentsElements);

    // Then
    assertThat(tournaments).hasSize(2);

    Tournament tournament = tournaments.get(0);
    assertThat(tournament.getName()).isEqualTo(
      "Monday Qualifier for the Coolbet presents European Disc Golf Festival 2025 | PDGA Major");
    assertThat(tournament.getTier()).isEqualTo("");
    assertThat(tournament.getDate().getMonthValue()).isEqualTo(7);
    assertThat(tournament.getDate().getDayOfMonth()).isEqualTo(14);
    assertThat(tournament.getDateString()).isEqualTo("Jul 14 Mon");
    assertThat(tournament.getDayAndMonth()).isEqualTo("Jul 14");
    assertThat(tournament.getDayOfWeek()).isEqualTo("Mon");
    assertThat(tournament.getRegistrants()).isZero();
    assertThat(tournament.getIsRegistrationOpen()).isTrue();
    assertThat(tournament.getCourse()).isEqualTo("Tallinn Song Festival Grounds");
    assertThat(tournament.getCity()).isEqualTo("Tallinn");
    assertThat(tournament.getState()).isEqualTo("");
    assertThat(tournament.getCountry()).isEqualTo("Estonia");
    assertThat(tournament.getUrl()).isEqualTo("https://www.discgolfscene.com/tournaments/Monday_Qualifier_for_the_Coolbet_presents_European_Disc_Golf_Festival_2025_PDGA_Major");
    assertThat(tournament.getRegistrationUrl()).isEqualTo("https://www.discgolfscene.com/tournaments/Monday_Qualifier_for_the_Coolbet_presents_European_Disc_Golf_Festival_2025_PDGA_Major/registration");

    Tournament tournament2 = tournaments.get(1);
    assertThat(tournament2.getName()).isEqualTo("Coolbet presents: European Disc Golf Festival 2025");
    assertThat(tournament2.getTier()).isEqualTo("Major");
    assertThat(tournament2.getDate().getMonthValue()).isEqualTo(7);
    assertThat(tournament2.getDate().getDayOfMonth()).isEqualTo(17);
    assertThat(tournament2.getDateString()).isEqualTo("Jul 17-20 Thu-Sun");
    assertThat(tournament2.getDayAndMonth()).isEqualTo("Jul 17-20");
    assertThat(tournament2.getDayOfWeek()).isEqualTo("Thu-Sun");
    assertThat(tournament2.getIsRegistrationOpen()).isFalse();
    assertThat(tournament2.getRegistrants()).isEqualTo(156);
    assertThat(tournament2.getCourse()).isEqualTo("Tallinn Song Festival Grounds");
    assertThat(tournament2.getCity()).isEqualTo("Tallinn");
    assertThat(tournament.getState()).isEqualTo("");
    assertThat(tournament.getCountry()).isEqualTo("Estonia");
    assertThat(tournament2.getUrl()).isEqualTo("https://www.discgolfscene.com/tournaments/European_Disc_Golf_Festival_2025");
    assertThat(tournament2.getRegistrationUrl()).isEqualTo("https://www.discgolfscene.com/tournaments/European_Disc_Golf_Festival_2025/registration");
  }
}
