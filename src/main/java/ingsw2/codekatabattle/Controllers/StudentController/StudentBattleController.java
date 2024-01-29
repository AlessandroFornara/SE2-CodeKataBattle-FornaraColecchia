package ingsw2.codekatabattle.Controllers.StudentController;

import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.TeamDTOS.JoinTeamDTO;
import ingsw2.codekatabattle.Model.TeamDTOS.TeamCreationDTO;
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
}
