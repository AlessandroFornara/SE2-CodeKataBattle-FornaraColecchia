package ingsw2.codekatabattle.Controllers.StudentController;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/studTnt")
@AllArgsConstructor
public class StudentTournamentController {

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(){

        return null;
    }

}
