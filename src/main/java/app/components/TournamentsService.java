package app.components;

import app.components.dgm.DgmParser;
import app.components.dgs.DgsParser;
import app.model.Tournament;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TournamentsService {

  private final HtmlBuilder htmlBuilder;
  private final DgsParser dgsTournamentsService;
  private final DgmParser dgmTournamentsParser;

  public String getAllTournaments() {
    return htmlBuilder.buildAllTournaments(getTournaments());
  }

  public String getFavoriteTournaments() {
    return htmlBuilder.buildFavoriteTournaments(getTournaments());
  }

  private List<Tournament> getTournaments() {
    List<Tournament> dgsTournaments = dgsTournamentsService.getTournaments();
    List<Tournament> dgmTournaments = dgmTournamentsParser.getTournaments();

    List<Tournament> allTournaments = new ArrayList<>();
    allTournaments.addAll(dgsTournaments);
    allTournaments.addAll(dgmTournaments);
    return allTournaments;
  }

}
