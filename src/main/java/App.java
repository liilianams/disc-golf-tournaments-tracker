import java.awt.*;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class App {

  public static void main(String[] args) throws IOException {
    List<String> props = getProperties();

    Scraper scraper = new Scraper();
    Map<Location, List<Tournament>> tournaments = scraper.scrape(props);

    // Generate HTML
    String html = HtmlBuilder.build(tournaments);

    // Write the HTML string to a file
    String filename = "index.html";
    Path path = Paths.get(filename);
    try (FileWriter writer = new FileWriter(filename)) {
      writer.write(html);
    }

    // Open the file in the default browser
    URI uri = path.toUri();
    Desktop.getDesktop().browse(uri);
  }

  private static List<String> getProperties() {
    try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
      Properties prop = new Properties();
      prop.load(input);
      return Arrays.asList(prop.getProperty("states").split(","));
    } catch (IOException ex) {
      ex.printStackTrace();
      return List.of();
    }
  }

}
