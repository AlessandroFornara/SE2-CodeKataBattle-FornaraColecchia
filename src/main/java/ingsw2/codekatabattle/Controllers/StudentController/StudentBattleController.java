package ingsw2.codekatabattle.Controllers.StudentController;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/studTeam")
public class StudentBattleController {

    @PostMapping("/create")
    public ResponseEntity<?> createTeam() {
        return null;
    }

    @PostMapping("/join")
    public ResponseEntity<?> JoinTeam() {
        return null;
    }
}
