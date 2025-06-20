package app.components;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.Map;

import static java.time.temporal.ChronoUnit.MINUTES;

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
  public static LocalDate convertToLocalDate(String dateString, Clock clock) {
    Month month;
    int day;

    try {
      if (isDateRange(dateString)) {
        month = parseStartMonthFromDateRange(dateString);
        day = parseStartDayFromDateRange(dateString);
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
      LOGGER.error("Failed to convert dateString {} to localDate: {} ", dateString, e.getMessage());
      return null;
    }
  }

  private Month parseStartMonthFromDateRange(String dateString) throws IllegalArgumentException {
    String[] dates = dateString.split("-");

    if (dates.length < 2) {
      throw new IllegalArgumentException("Invalid date string format: " + dateString);
    }

    String[] startDateParts = dates[0].trim().split(" ");
    Month month = MONTH_ABBREVIATIONS.get(startDateParts[0].trim().toUpperCase());

    if (month == null) {
      throw new IllegalArgumentException("Invalid month abbreviation: " + startDateParts[0].trim());
    }

    return month;
  }

  private int parseStartDayFromDateRange(String dateString) throws IllegalArgumentException {
    String[] dates = dateString.split("-");

    if (dates.length < 2) {
      throw new IllegalArgumentException("Invalid date string format: " + dateString);
    }

    String[] startDateParts = dates[0].trim().split(" ");
    String dayString = startDateParts[1].trim();

    return Integer.parseInt(dayString);
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
    return LocalDateTime.now(ZoneId.of("UTC")).truncatedTo(MINUTES);
  }

  /**
   * Returns the current date in UTC.
   *
   * @return the current date in UTC
   */
  public static LocalDate getCurrentDate() {
    return LocalDate.now(ZoneId.of("UTC"));
  }

  /**
   * Returns the same date but next year in UTC.
   *
   * @return the same date next year in UTC
   */
  public static LocalDate getSameDateNextYear() {
    return getCurrentDate().plusYears(1);
  }

  private static String capitalizeFirstLetter(String string) {
    return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
  }

  private static boolean isDateRange(String date) {
    return date.contains("-");
  }
}
