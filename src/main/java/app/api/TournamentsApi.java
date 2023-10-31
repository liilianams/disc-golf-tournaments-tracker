package app.api;

import app.components.TournamentsService;
import app.config.ApplicationProperties;
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

  private final TournamentsService tournamentsService;
  private final ApplicationProperties applicationProperties;

  @GetMapping("/")
  public String getAllTournaments() {
    return tournamentsService.getAllTournaments();
  }

  @GetMapping("/favorites")
  public String getFavoriteTournaments(
      HttpSession session,
      HttpServletResponse response
  ) throws IOException {
    if (applicationProperties.getIsPasswordCheckEnabled() && session.getAttribute("passwordChecked") == null) {
      response.sendRedirect("/login?redirect=/favorites");
      return null;
    }
    return tournamentsService.getFavoriteTournaments();
  }

}

