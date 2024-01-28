package ingsw2.codekatabattle.Controllers.EducatorController;

import ingsw2.codekatabattle.Model.BattleDTOS.BattleCreationDTO;
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

    @PostMapping("/close")
    public ResponseEntity<?> closeConsolidationStage(){
        return null;
    }


    @PostMapping("/evaluate")
    public String evaluate(){
        return "";
    }

}
