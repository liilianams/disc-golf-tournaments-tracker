package app.components;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

@UtilityClass
public class Utils {

  static LocalDate convertToLocalDate(String dateString) {
    String[] parts = dateString.split(" ");
    String month = parts[0];
    int monthValue = Month.valueOf(month.toUpperCase()).getValue();
    int day = parts[1].contains("-") ? Integer.parseInt(parts[1].split("-")[1]) : Integer.parseInt(parts[1]);

    LocalDate currentDate = LocalDate.now();
    LocalDate parsedDate = LocalDate.of(currentDate.getYear(), monthValue, day);

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

}
