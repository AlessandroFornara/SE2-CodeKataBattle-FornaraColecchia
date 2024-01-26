package ingsw2.codekatabattle.Controllers.EducatorController;

import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.TournamentDTOS.PromoteToModeratorDTO;
import ingsw2.codekatabattle.Model.TournamentDTOS.TournamentCreationDTO;
import ingsw2.codekatabattle.Services.TournamentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/eduTnt")
public class TournamentManagementController {

    private final TournamentService tournamentService;

    @PreAuthorize("hasRole('EDUCATOR')")
    @PostMapping ("/create")
    public ResponseEntity<?> createTournament(@Valid @RequestBody TournamentCreationDTO tournamentCreationDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KeywordResponse result = tournamentService.createTournament(tournamentCreationDTO.getName(),
                (String) authentication.getPrincipal(),
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

    @PreAuthorize("hasRole('EDUCATOR')")
    @PostMapping("/promote")
    public ResponseEntity<?> promoteToModerator(@Valid @RequestBody PromoteToModeratorDTO promoteToModeratorDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServerResponse result = tournamentService.promoteToModerator((String) authentication.getPrincipal(),
                promoteToModeratorDTO.getName(),
                promoteToModeratorDTO.getModerator());

        if(result == ServerResponse.USER_SUCCESSFULLY_PROMOTED_TO_MODERATOR)
            return ResponseEntity.ok(ServerResponse.toString(result));
        else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
    }

}
