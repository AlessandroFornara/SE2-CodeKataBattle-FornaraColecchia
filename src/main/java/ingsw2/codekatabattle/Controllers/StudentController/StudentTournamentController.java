package ingsw2.codekatabattle.Controllers.StudentController;

import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.TournamentDTOS.SubscribeTntDTO;
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
@RequestMapping("/api/studTnt")
@AllArgsConstructor
public class StudentTournamentController {

    private final TournamentService tournamentService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody SubscribeTntDTO subscribeTntDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();
        ServerResponse result;
        if(subscribeTntDTO.isPublic())
            result = tournamentService.subscribeToTournament(subscribeTntDTO.getNameOrKeyword(), username, null);
        else
            result = tournamentService.subscribeToTournament(null, username, subscribeTntDTO.getNameOrKeyword());

        if(result == ServerResponse.USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT)
            return ResponseEntity.ok(ServerResponse.toString(result));
        else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
    }

}
