import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Location {
  RAPTORS_KNOLL("Raptors Knoll"),
  BURNABY("Burnaby"),
  SQUAMISH("Squamish"),
  KAYAK_POINT("Kayak Point"),
  NOT_SPECIFIED("");

  private final String displayName;

  Location(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Location fromString(String displayName) {
    for (Location location : Location.values()) {
      if (location.getDisplayName().equals(displayName)) {
        return location;
      }
    }
    throw new IllegalArgumentException("No matching Location enum with display name: " + displayName);
  }

  public static List<String> getAllDisplayNames() {
    List<String> result = new ArrayList<>();
    Arrays.stream(Location.values()).forEach(l -> {
      if (l.displayName.isBlank()) {
        result.add("N/A");
      } else {
        result.add(l.displayName);
      }
    });
    return result;
  }

}
