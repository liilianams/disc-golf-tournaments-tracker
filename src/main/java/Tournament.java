import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;

@Data
@NoArgsConstructor
class Tournament {

  private String name;
  private LocalDate date;
  private String dateString;
  private Integer registrants;
  private String tier;
  private String description;
  private Location location;
  private String url;

  static Comparator<Tournament> dateComparator = Comparator.comparing((Tournament t) -> {
    String[] parts = t.getDateString().split(" ");
    String month = parts[0];
    int monthValue = Month.valueOf(month.toUpperCase()).getValue();
    int day = parts[1].contains("-") ? Integer.parseInt(parts[1].split("-")[0]) : Integer.parseInt(parts[1]);
    return LocalDate.of(LocalDate.now().getYear(), monthValue, day);
  });

}