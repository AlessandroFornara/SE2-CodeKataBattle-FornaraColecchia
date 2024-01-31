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

@RestController
@AllArgsConstructor
@RequestMapping("/api/studTeam")
public class StudentBattleController {

    private final BattleService battleService;

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

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/myBattles")
    public ResponseEntity<?> getMyBattles(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<MyBattlesDTO> myBattles = battleService.getBattlesByStudent((String)authentication.getPrincipal());
        return ResponseEntity.ok(myBattles);
    }

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

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/tntBattles")
    public ResponseEntity<?> getTournamentBattles(@RequestParam String tournamentName){
        return ResponseEntity.ok(battleService.getTntBattles(tournamentName));
    }
}
