package ingsw2.codekatabattle.Controllers.EducatorController;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/eduTnt")
public class TournamentManagementController {


    @PostMapping ("/create")
    public ResponseEntity<?> createTournament() {

        return null;
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
