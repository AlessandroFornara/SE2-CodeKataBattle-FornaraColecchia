package ingsw2.codekatabattle.Controllers.EducatorController;

import ingsw2.codekatabattle.Entities.Battle;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Model.BattleDTOS.BattleCreationDTO;
import ingsw2.codekatabattle.Model.BattleDTOS.CloseConsolidationStageDTO;
import ingsw2.codekatabattle.Model.BattleDTOS.EvaluateDTO;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.MyBattlesDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Services.BattleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Controller for managing battles, specifically designed for users with the 'EDUCATOR' role.
 * This controller offers functionalities like creating battles, closing the consolidation stage of a battle, evaluating teams,
 * and retrieving battle-related information.
 * All endpoints are mapped under the "/api/eduBattle" base URL and require the user to be authenticated and authorized as an 'EDUCATOR'.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/eduBattle")
public class BattleManagementController {

    private final BattleService battleService;

    /**
     * Creates a new battle.
     * This method allows educators to set up a new battle by providing necessary details through a DTO.
     * @param battleCreationDTO DTO containing battle creation details like name, tournament name, deadlines, code kata, player limits.
     * @return A ResponseEntity containing the server response indicating the outcome of the battle creation operation.
     */
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

    /**
     * Closes the consolidation stage of a battle.
     * This endpoint is used by educators to mark the end of the consolidation phase of a battle.
     * @param closeConsolidationStageDTO DTO containing the name of the battle for which the consolidation stage is to be closed.
     * @return A ResponseEntity with the outcome of the consolidation stage closure operation.
     */
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

    /**
     * Modifies the evaluation of a team in a battle.
     * Educators can use this endpoint to evaluate student submissions, assigning points to each participant.
     * @param evaluateDTO DTO containing details for the evaluation process, including battle name, participant usernames, and points awarded.
     * @return A ResponseEntity indicating whether the evaluation was successful or not.
     */
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

    /**
     * Retrieves the list of battles created by the currently authenticated educator.
     * @return A ResponseEntity containing a list of MyBattlesDTO objects representing the battles created by the educator.
     */
    @PreAuthorize("hasRole('EDUCATOR')")
    @GetMapping("/myBattles")
    public ResponseEntity<?> getMyBattles(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<MyBattlesDTO> myBattles = battleService.getBattlesByEducator((String)authentication.getPrincipal());
        return ResponseEntity.ok(myBattles);
    }

    /**
     * Fetches detailed information about a specific battle.
     * @param name The name of the battle to retrieve information for.
     * @return A ResponseEntity containing the Battle object if found, or an error message if the battle doesn't exist.
     */
    @PreAuthorize("hasRole('EDUCATOR')")
    @GetMapping("/battleInfo")
    public ResponseEntity<?> getBattleInfo(@RequestParam String name){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String grantedAuthority = null;
        for (GrantedAuthority authority : authorities) {
            grantedAuthority = authority.getAuthority();
        }

        Battle battle = battleService.getBattleInfo(name,
                (String) authentication.getPrincipal(),
                grantedAuthority.equals("ROLE_EDUCATOR") ? UserRole.EDUCATOR : UserRole.STUDENT);

        if (battle == null){
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(ServerResponse.BATTLE_DOESNT_EXIST));
        }
        return ResponseEntity.ok(battle);
    }

    /**
     * Retrieves a list of battles associated with a specific tournament.
     * @param tournamentName The name of the tournament to retrieve battles for.
     * @return A ResponseEntity containing a list of battles related to the specified tournament.
     */
    @PreAuthorize("hasRole('EDUCATOR')")
    @GetMapping("/tntBattles")
    public ResponseEntity<?> getTournamentBattles(@RequestParam String tournamentName){
        return ResponseEntity.ok(battleService.getTntBattles(tournamentName));
    }
}
