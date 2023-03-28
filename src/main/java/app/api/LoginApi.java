package app.api;

import app.components.HtmlBuilder;
import app.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class LoginApi {

  private final HtmlBuilder htmlBuilder;
  private final ApplicationProperties applicationProperties;

  @GetMapping("/login")
  public String showLogin(
      @RequestParam(value = "redirect", required = false) String redirect,
      HttpSession session,
      HttpServletResponse response,
      HttpServletRequest request
  ) throws IOException {
    if (session.getAttribute("passwordChecked") != null) {
      response.sendRedirect(Objects.requireNonNullElse(redirect, "/"));
      return "";
    }
    request.removeAttribute("error");
    return htmlBuilder.buildLogin();
  }

  @PostMapping("/login")
  public String login(
      @RequestParam("password") String password,
      @RequestParam(value = "redirect", required = false) String redirect,
      HttpSession session,
      HttpServletResponse response,
      HttpServletRequest request
  ) throws IOException {
    if (applicationProperties.getLogin().getPassword().equals(password)) {
      session.setAttribute("passwordChecked", true);
      response.sendRedirect(Objects.requireNonNullElse(redirect, "/"));
      return "";
    } else {
      request.setAttribute("error", "Invalid password");
      return htmlBuilder.buildLogin();
    }
  }
}
