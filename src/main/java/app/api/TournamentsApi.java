package app.api;

import app.components.Scraper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TournamentsApi {

  private final Scraper scraper;

  @GetMapping("/")
  public String getAllTournaments(HttpSession session, HttpServletResponse response) throws IOException {
    if (session.getAttribute("passwordChecked") == null) {
      response.sendRedirect("/login?redirect=/");
      return null;
    }
    return scraper.getAllTournaments();
  }

  @GetMapping("/my-tournaments")
  public String getMyTournaments(HttpSession session, HttpServletResponse response) throws IOException {
    if (session.getAttribute("passwordChecked") == null) {
      response.sendRedirect("/login?redirect=/my-tournaments");
      return null;
    }
    return scraper.getMyTournaments();
  }

}

