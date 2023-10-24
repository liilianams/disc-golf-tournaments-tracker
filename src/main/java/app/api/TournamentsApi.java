package app.api;

import app.components.TournamentsService;
import app.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
  public String getAllTournaments(HttpServletRequest request) {
    return tournamentsService.getAllTournaments(isMobile(request));
  }

  @GetMapping("/favorites")
  public String getFavoriteTournaments(
      HttpSession session,
      HttpServletResponse response,
      HttpServletRequest request
  ) throws IOException {
    if (applicationProperties.getIsPasswordCheckEnabled() && session.getAttribute("passwordChecked") == null) {
      response.sendRedirect("/login?redirect=/favorites");
      return null;
    }
    return tournamentsService.getFavoriteTournaments(isMobile(request));
  }

  private boolean isMobile(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    return userAgent != null && userAgent.toLowerCase().contains("mobile");
  }

}

