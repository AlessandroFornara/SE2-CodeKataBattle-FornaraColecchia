package ingsw2.codekatabattle.Controllers.GitHubController;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/GitHub")
@AllArgsConstructor
public class GitHubManager {

    @PostMapping("/automatedEvaluation")
    public void updateScore(){

    }

}
