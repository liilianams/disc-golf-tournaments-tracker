package app.components;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

class UtilsTest {

  private static final LocalDateTime NOW = LocalDateTime.of(2023, 9, 10, 0, 0);
  private static final ZonedDateTime NOW_ZONED = ZonedDateTime.of(NOW, ZoneOffset.UTC);
  private static final Clock CLOCK = Clock.fixed(NOW_ZONED.toInstant(), ZoneOffset.UTC);

  @ParameterizedTest(name = "Given date string {0}, then local date is {1}")
  @CsvSource({
      "Sep 1,         2024-09-01",  // If day of month < current day of month, then date is next year
      "Sep 10,        2023-09-10",  // date == current date
      "Sep 29-Oct 1,  2023-09-29",  // If date range, then use start date
      "Oct 1-4,       2023-10-01"   // If date range, then use start date
  })
  void convertToLocalDate(String givenDateString, String expectedDateString) {
    // Given when
    LocalDate expectedDate = LocalDate.parse(expectedDateString);
    LocalDate actualDate = Utils.convertToLocalDate(givenDateString, CLOCK);

    // Then
    assertThat(actualDate).isEqualTo(expectedDate);
  }
}