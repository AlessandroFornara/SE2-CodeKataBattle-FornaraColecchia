package ingsw2.codekatabattle.Controllers.StudentController;

import ingsw2.codekatabattle.Entities.Battle;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.MyBattlesDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.TeamDTOS.JoinTeamDTO;
import ingsw2.codekatabattle.Model.TeamDTOS.TeamCreationDTO;
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
 * Controller handling battle-related operations for students.
 * All endpoints are mapped under the "/api/studTeam" base URL.
 * It offers functionalities like creating and joining teams, accessing student-specific battle details,
 * and retrieving battle information related to specific tournaments.
 * Access to these endpoints is restricted to authenticated users with the 'STUDENT' role.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/studTeam")
public class StudentBattleController {

    private final BattleService battleService;

    /**
     * Creates a new team for a specific battle. The team's name, the battle's name, and the username of the
     * creating student are provided in the TeamCreationDTO.
     * @param teamCreationDTO The DTO containing the team creation details.
     * @return A ResponseEntity containing either a success message with the team's keyword or an error message.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamCreationDTO teamCreationDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        KeywordResponse result = battleService.createTeam(teamCreationDTO.getName(), teamCreationDTO.getBattleName(), username);
        if(result.getServerResponse().equals(ServerResponse.TEAM_SUCCESSFULLY_CREATED)){
            return ResponseEntity.ok(ServerResponse.toString(result.getServerResponse()) + ". KEYWORD: " + result.getKeyword());
        }else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result.getServerResponse()));
    }

    /**
     * Allows a student to join an existing team in a battle using a unique keyword.
     * @param joinTeamDTO The DTO containing the join team details, including the keyword and battle's name.
     * @return A ResponseEntity indicating the outcome of the join team operation.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/join")
    public ResponseEntity<?> JoinTeam(@Valid @RequestBody JoinTeamDTO joinTeamDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();

        ServerResponse result = battleService.joinTeam(joinTeamDTO.getKeyword(), joinTeamDTO.getBattleName(), username);
        if(result.equals(ServerResponse.JOIN_TEAM_SUCCESS)){
            return ResponseEntity.ok(ServerResponse.toString(result));
        }else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));

    }

    /**
     * Retrieves a list of battles that the currently authenticated student is involved in.
     * @return A ResponseEntity containing a list of MyBattlesDTO objects representing the student's battles.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/myBattles")
    public ResponseEntity<?> getMyBattles(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<MyBattlesDTO> myBattles = battleService.getBattlesByStudent((String)authentication.getPrincipal());
        return ResponseEntity.ok(myBattles);
    }

    /**
     * Fetches detailed information about a specific battle.
     * @param name The name of the battle to retrieve information for.
     * @return A ResponseEntity containing the Battle object if found, or an error message if the battle doesn't exist.
     */
    @PreAuthorize("hasRole('STUDENT')")
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
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/tntBattles")
    public ResponseEntity<?> getTournamentBattles(@RequestParam String tournamentName){
        return ResponseEntity.ok(battleService.getTntBattles(tournamentName));
    }
}
