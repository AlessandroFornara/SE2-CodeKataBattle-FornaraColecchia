package ingsw2.codekatabattle.Controllers.StudentController;

import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.TournamentDTOS.SubscribeTntDTO;
import ingsw2.codekatabattle.Services.TournamentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/studTnt")
@AllArgsConstructor
public class StudentTournamentController {

    private final TournamentService tournamentService;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribePublic(@Valid @RequestBody SubscribeTntDTO subscribeTntDTO){

        ServerResponse result;
        if(subscribeTntDTO.isPublic())
            result = tournamentService.subscribeToTournament(subscribeTntDTO.getNameOrKeyword(), "test", null);
        else
            result = tournamentService.subscribeToTournament(null, "test", subscribeTntDTO.getNameOrKeyword());

        if(result == ServerResponse.USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT)
            return ResponseEntity.ok(ServerResponse.toString(result));
        else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
    }

}
