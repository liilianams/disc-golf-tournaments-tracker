package app.components;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Map;

@UtilityClass
public class Utils {

  private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

  private static final Map<String, Month> MONTH_ABBREVIATIONS = Map.ofEntries(
    Map.entry("JAN", Month.JANUARY),
    Map.entry("FEB", Month.FEBRUARY),
    Map.entry("MAR", Month.MARCH),
    Map.entry("APR", Month.APRIL),
    Map.entry("MAY", Month.MAY),
    Map.entry("JUN", Month.JUNE),
    Map.entry("JUL", Month.JULY),
    Map.entry("AUG", Month.AUGUST),
    Map.entry("SEP", Month.SEPTEMBER),
    Map.entry("OCT", Month.OCTOBER),
    Map.entry("NOV", Month.NOVEMBER),
    Map.entry("DEC", Month.DECEMBER)
  );

  /**
   * Converts a date string in the specified format to a {@link LocalDate} object
   * using the provided {@link Clock} to calculate the appropriate year.
   *
   * @param dateString a date string in the format "{Month} {Day}" or
   *                   "{Month} {Day}-{Day}" or "{Month} {Day}-{Month} {Day}"
   * @param clock      the {@link Clock} instance to use to calculate the year
   * @return the parsed date as a {@link LocalDate} object (in case of a date range returns the end date)
   * @throws IllegalArgumentException if the input date string does not adhere to the expected format
   */
  static LocalDate convertToLocalDate(String dateString, Clock clock) {
    Month month;
    int day;

    try {
      if (isDateRange(dateString)) {
        month = getParseMonthFromDateRange(dateString);
        day = getParseDayFromDateRange(dateString);
      } else {
        String[] dateParts = dateString.split(" ");
        month = MONTH_ABBREVIATIONS.get(dateParts[0].toUpperCase());
        day = Integer.parseInt(dateParts[1]);
      }

      LocalDate currentDate = LocalDate.now(clock);
      LocalDate parsedDate = LocalDate.of(currentDate.getYear(), month, day);

      if (parsedDate.isBefore(currentDate)) {
        parsedDate = parsedDate.plusYears(1);
      }

      return parsedDate;
    } catch (IllegalArgumentException e) {
      LOGGER.error(e.getMessage());
      return null;
    }
  }

  private Month getParseMonthFromDateRange(String dateString) throws IllegalArgumentException {
    String[] dates = dateString.split("-");
    String[] startDateParts = dates[0].trim().split(" ");
    String[] endDateParts = dates[1].trim().split(" ");

    String monthStr = endDateParts.length < 2 ? startDateParts[0].trim() : endDateParts[0].trim();

    Month month = MONTH_ABBREVIATIONS.get(monthStr.toUpperCase());
    if (month == null) {
      throw new IllegalArgumentException("Invalid month abbreviation: " + monthStr);
    }

    return month;
  }

  private int getParseDayFromDateRange(String dateString) throws IllegalArgumentException {
    String[] dates = dateString.split("-");

    if (dates.length < 2) {
      throw new IllegalArgumentException("Invalid date string: " + dateString);
    }

    String[] startDateParts = dates[0].trim().split(" ");
    String[] endDateParts = dates[1].trim().split(" ");

    String dayStr = endDateParts.length == 0 ?
      startDateParts[1].trim() :
      endDateParts.length < 2 ? endDateParts[0] : endDateParts[1];

    return Integer.parseInt(dayStr);
  }

  /**
   * Capitalizes the first letter of each word in the input string and returns the result.
   * If the input string is blank, it is returned as is.
   *
   * @param input the input string to be capitalized
   * @return the input string with the first letter of each word capitalized
   */
  static String capitalize(String input) {
    if (input.isBlank()) {
      return input;
    }
    String[] words = input.split("\\s+");
    StringBuilder result = new StringBuilder();
    for (String word : words) {
      String capitalizedWord = capitalizeFirstLetter(word);
      result.append(capitalizedWord).append(" ");
    }
    return result.toString().trim();
  }

  /**
   * Retrieves the current local date and time, truncated to the nearest minute.
   *
   * @return the current local date and time truncated to minutes
   */
  public static LocalDateTime getCurrentTime() {
    return LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
  }

  private static String capitalizeFirstLetter(String string) {
    return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
  }

  private static boolean isDateRange(String date) {
    return date.contains("-");
  }
}
