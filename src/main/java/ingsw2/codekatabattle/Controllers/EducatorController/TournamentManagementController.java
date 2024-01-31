package ingsw2.codekatabattle.Controllers.EducatorController;

import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Entities.Tournament;
import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.MyTournamentsDTO;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.UpcomingAndOngoingTntDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.TournamentDTOS.PromoteToModeratorDTO;
import ingsw2.codekatabattle.Model.TournamentDTOS.TournamentClosureDTO;
import ingsw2.codekatabattle.Model.TournamentDTOS.TournamentCreationDTO;
import ingsw2.codekatabattle.Services.TournamentService;
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
@RequestMapping("/api/eduTnt")
public class TournamentManagementController {

    private final TournamentService tournamentService;

    @PreAuthorize("hasRole('EDUCATOR')")
    @PostMapping ("/create")
    public ResponseEntity<?> createTournament(@Valid @RequestBody TournamentCreationDTO tournamentCreationDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        KeywordResponse result = tournamentService.createTournament(tournamentCreationDTO.getName(),
                (String) authentication.getPrincipal(),
                tournamentCreationDTO.getRegistrationDeadline(),
                tournamentCreationDTO.isPublicTournament());

        if(result.getServerResponse() == ServerResponse.PRIVATE_TOURNAMENT_CREATED)
            return ResponseEntity.ok(ServerResponse.toString(result.getServerResponse()) + ". KEYWORD: " + result.getKeyword());
        else if(result.getServerResponse() == ServerResponse.PUBLIC_TOURNAMENT_CREATED)
            return ResponseEntity.ok(ServerResponse.toString(result.getServerResponse()));
        else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result.getServerResponse()));
    }

    @PreAuthorize("hasRole('EDUCATOR')")
    @PostMapping("/close")
    public ResponseEntity<?> closeTournament(@Valid @RequestBody TournamentClosureDTO tournamentClosureDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServerResponse result = tournamentService.closeTournament(tournamentClosureDTO.getName(),(String) authentication.getPrincipal());
        if(result == ServerResponse.TOURNAMENT_CLOSED_OK)
            return ResponseEntity.ok(ServerResponse.toString(result));
        else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
    }

    @PreAuthorize("hasRole('EDUCATOR')")
    @PostMapping("/promote")
    public ResponseEntity<?> promoteToModerator(@Valid @RequestBody PromoteToModeratorDTO promoteToModeratorDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServerResponse result = tournamentService.promoteToModerator((String) authentication.getPrincipal(),
                promoteToModeratorDTO.getName(),
                promoteToModeratorDTO.getModerator());

        if(result == ServerResponse.USER_SUCCESSFULLY_PROMOTED_TO_MODERATOR)
            return ResponseEntity.ok(ServerResponse.toString(result));
        else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
    }

    @PreAuthorize("hasRole('EDUCATOR')")
    @GetMapping("/myTnt")
    public ResponseEntity<?> getMyTournaments(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<MyTournamentsDTO> tournamentList = tournamentService.getTournamentsByEducator((String) authentication.getPrincipal());
        return ResponseEntity.ok(tournamentList);
    }

    @PreAuthorize("hasRole('EDUCATOR')")
    @GetMapping("/tntInfo")
    public ResponseEntity<?> getTournamentInfo(@RequestParam String name){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String grantedAuthority = null;
        for (GrantedAuthority authority : authorities) {
            grantedAuthority = authority.getAuthority();
        }

        Tournament tournament = tournamentService.getTournamentInfo(name,
                (String) authentication.getPrincipal(),
                grantedAuthority.equals("ROLE_EDUCATOR") ? UserRole.EDUCATOR : UserRole.STUDENT);
        if (tournament == null){
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(ServerResponse.TOURNAMENT_DOESNT_EXIST));
        }
        return ResponseEntity.ok(tournament);
    }

    @PreAuthorize("hasRole('EDUCATOR')")
    @GetMapping("/homePageTnt")
    public ResponseEntity<?> getUpcomingTournaments(){
        List<UpcomingAndOngoingTntDTO> upcomingAndOngoingTournaments = tournamentService.getUpcomingAndOngoingTournaments();
        return ResponseEntity.ok(upcomingAndOngoingTournaments);
    }

}
