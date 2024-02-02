package ingsw2.codekatabattle.Controllers.GitHubController;

import ingsw2.codekatabattle.DAO.BattleDAO;
import ingsw2.codekatabattle.Model.AutomatedEvaluationDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Services.BattleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling GitHub-related operations.
 * All endpoints are mapped under the "/api/GitHub" base URL.
 */
@RestController
@RequestMapping("/api/GitHub")
@AllArgsConstructor
public class GitHubManager {

    private final BattleService battleService;

    private final BattleDAO battleDAO;

    /**
     * Updates the score of a participant in a battle based on automated evaluation results.
     * The details of the code to be evaluated, including the team keyword, battle name and code outputs, are provided in the AutomatedEvaluationDTO.
     * @param automatedEvaluationDTO The DTO containing details of the code to be evaluated and the team who sent it.
     * @return A ResponseEntity indicating the outcome of the score update operation, either success or an error message.
     */
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
