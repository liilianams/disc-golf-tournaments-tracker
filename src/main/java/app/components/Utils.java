package app.components;

import lombok.experimental.UtilityClass;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

@UtilityClass
public class Utils {

  /**
   * Converts a date string in the specified format to a {@link LocalDate} object
   * using the provided {@link Clock} to calculate the appropriate year.
   *
   * @param  dateString a date string in the format "{Month} {Day} {DayOfWeek}" or
   *                    "{Month} {Day}-{Day} {DayOfWeek}-{DayOfWeek}"
   * @param  clock      the {@link Clock} instance to use to calculate the year
   * @return            the parsed date as a {@link LocalDate} object
   * @throws IllegalArgumentException if the input date string does not adhere to the expected format
  */
  static LocalDate convertToLocalDate(String dateString, Clock clock) {
    String[] parts = dateString.split(" ");

    String monthPart = parts[0];
    int monthValue = Month.valueOf(monthPart.toUpperCase()).getValue();

    String datePart = parts[1];
    int dateValue = 0;

    // Handle if dateString is a date range instead of a single date
    if (isDateRange(datePart)) {
      String[] dateRange = parts[1].split("-");
      validateDateRange(dateRange);
      dateValue = Integer.parseInt(dateRange[1]);

      if (doesDateRangeSpanAcrossMultipleMonths(dateRange)) {
        monthValue += 1;
      }
    } else {
      dateValue = Integer.parseInt(datePart);
    }

    LocalDate currentDate = LocalDate.now(clock);
    LocalDate parsedDate = LocalDate.of(currentDate.getYear(), monthValue, dateValue);

    if (parsedDate.isBefore(currentDate)) {
      parsedDate = parsedDate.plusYears(1);
    }

    return parsedDate;
  }

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

  public static LocalDateTime getCurrentTime() {
    return LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
  }

  private static String capitalizeFirstLetter(String string){
    return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
  }

  private static void validateDateRange(String[] dateRange) {
    if (dateRange.length > 2) {
      throw new IllegalArgumentException("Invalid format for date range");
    }
  }

  private static boolean isDateRange(String date) {
    return date.contains("-");
  }

  private static boolean doesDateRangeSpanAcrossMultipleMonths(String[] dateRange) {
    return Integer.parseInt(dateRange[0]) >= Integer.parseInt(dateRange[1]);
  }

}
