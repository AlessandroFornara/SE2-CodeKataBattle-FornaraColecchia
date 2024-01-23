package ingsw2.codekatabattle.Controllers.EducatorController;

import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.TournamentDTOS.TournamentCreationDTO;
import ingsw2.codekatabattle.Services.TournamentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/eduTnt")
public class TournamentManagementController {

    private final TournamentService tournamentService;

    @PostMapping ("/create")
    public ResponseEntity<?> createTournament(@Valid @RequestBody TournamentCreationDTO tournamentCreationDTO) {
        KeywordResponse result = tournamentService.createTournament(tournamentCreationDTO.getName(),
                "TEST",
                tournamentCreationDTO.getRegistrationDeadline(),
                tournamentCreationDTO.isPublic());

        if(result.getServerResponse() == ServerResponse.PRIVATE_TOURNAMENT_CREATED)
            return ResponseEntity.ok(ServerResponse.toString(result.getServerResponse()) + ". KEYWORD: " + result.getKeyword());
        else if(result.getServerResponse() == ServerResponse.PUBLIC_TOURNAMENT_CREATED)
            return ResponseEntity.ok(ServerResponse.toString(result.getServerResponse()));
        else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result.getServerResponse()));
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeTournament(){

        return null;
    }

    @PostMapping("/promote")
    public ResponseEntity<?> promoteToModerator(){

        return null;
    }

}
