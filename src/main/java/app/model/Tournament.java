package app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Tournament {

  public static final Comparator<Tournament> DATE_COMPARATOR = Comparator.comparing((Tournament t) -> {
    String[] parts = t.getDateString().split(" ");
    String month = parts[0];
    int monthValue = Month.valueOf(month.toUpperCase()).getValue();
    int day = parts[1].contains("-") ? Integer.parseInt(parts[1].split("-")[0]) : Integer.parseInt(parts[1]);
    return LocalDate.of(LocalDate.now().getYear(), monthValue, day);
  });

  private UUID id = UUID.randomUUID();
  private String name;
  private LocalDate date;
  private String dateString;
  private String dayAndMonth;
  private String dayOfWeek;
  private Integer registrants;
  private String tier;
  private String course;
  private String city;
  private String state;
  private String customLocation;
  private String hostedBy;
  private String url;

}