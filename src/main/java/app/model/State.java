package app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class State {

  private String path;
  private List<Location> locations;

  @Data
  @AllArgsConstructor
  public static class Location {

    private String name;
    private Integer order;

  }

}
