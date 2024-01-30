package ingsw2.codekatabattle.Controllers.EducatorController;

import ingsw2.codekatabattle.Model.BattleDTOS.BattleCreationDTO;
import ingsw2.codekatabattle.Model.BattleDTOS.CloseConsolidationStageDTO;
import ingsw2.codekatabattle.Model.BattleDTOS.EvaluateDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Services.BattleService;
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
@RequestMapping("/api/eduBattle")
public class BattleManagementController {

    private final BattleService battleService;

    @PreAuthorize("hasRole('EDUCATOR')")
    @PostMapping("/create")
    public ResponseEntity<?> createBattle(@Valid @RequestBody BattleCreationDTO battleCreationDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ServerResponse result = battleService.createBattle(
                battleCreationDTO.getName(),
                battleCreationDTO.getTournamentName(),
                battleCreationDTO.getRegistrationDeadline(),
                battleCreationDTO.getSubmissionDeadline(),
                battleCreationDTO.getCodeKata(),
                (String) authentication.getPrincipal(),
                battleCreationDTO.getMaxPlayers(),
                battleCreationDTO.getMinPlayers());

        if(result.equals(ServerResponse.BATTLE_SUCCESSFULLY_CREATED)){
            return ResponseEntity.ok(ServerResponse.toString(result));
        }else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
    }


    @PreAuthorize("hasRole('EDUCATOR')")
    @PostMapping("/close")
    public ResponseEntity<?> closeConsolidationStage(@Valid @RequestBody CloseConsolidationStageDTO closeConsolidationStageDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServerResponse result = battleService.closeConsolidationStage((String) authentication.getPrincipal(),
                closeConsolidationStageDTO.getBattleName());
        if(result == ServerResponse.CONS_STAGE_CLOSED_SUCCESSFULLY)
            return ResponseEntity.ok(ServerResponse.toString(result));
        else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
    }

    @PreAuthorize("hasRole('EDUCATOR')")
    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@Valid @RequestBody EvaluateDTO evaluateDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServerResponse result = battleService.evaluate((String) authentication.getPrincipal(),
                evaluateDTO.getBattleName(),
                evaluateDTO.getUsernames(),
                evaluateDTO.getPoints());

        if(result.equals(ServerResponse.EVALUATION_SUCCESSFUL)){
            return ResponseEntity.ok(ServerResponse.toString(result));
        }else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
    }

}
