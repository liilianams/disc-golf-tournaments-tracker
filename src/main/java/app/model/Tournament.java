package app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Tournament {

  private UUID id = UUID.randomUUID();
  private String name;
  private LocalDate date;
  private String dateString;
  private String dayAndMonth;
  private String dayOfWeek;
  private Integer registrants;
  private Boolean isRegistrationOpen;
  private String tier;
  private String course;
  private String city;
  private String state;
  private String customLocation;
  private String hostedBy;
  private String url;

}