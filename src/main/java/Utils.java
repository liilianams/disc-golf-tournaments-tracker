import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class Utils {

  static LocalDate convertToLocalDate(String dateString) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d EEEE uuuu");
    return LocalDate.parse(dateString + " " + LocalDate.now().getYear(), formatter);
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

  private static String capitalizeFirstLetter(String string){
    return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
  }

}
