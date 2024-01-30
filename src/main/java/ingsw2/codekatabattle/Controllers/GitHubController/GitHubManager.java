package ingsw2.codekatabattle.Controllers.GitHubController;

import ingsw2.codekatabattle.Model.AutomatedEvaluationDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Services.BattleService;
import ingsw2.codekatabattle.Services.GitHubService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/GitHub")
@AllArgsConstructor
public class GitHubManager {

    private final GitHubService gitHubService;
    private final BattleService battleService;

    @PostMapping("/automatedEvaluation")
    public ResponseEntity<?> updateScore(@Valid @RequestBody AutomatedEvaluationDTO automatedEvaluationDTO){

        ServerResponse response = battleService.updateScore(automatedEvaluationDTO.getKeyword(),
                automatedEvaluationDTO.getBattle(),
                automatedEvaluationDTO.getOutputs());

        if (response != ServerResponse.AUTOMATED_EVALUATION_OK){
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(response));
        }
        return ResponseEntity.ok(ServerResponse.toString(response));
    }

}
