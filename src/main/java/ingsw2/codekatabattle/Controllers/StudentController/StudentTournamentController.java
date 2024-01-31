package ingsw2.codekatabattle.Controllers.StudentController;

import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Entities.Tournament;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.MyTournamentsDTO;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.UpcomingAndOngoingTntDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.TournamentDTOS.SubscribeTntDTO;
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
@RequestMapping("/api/studTnt")
@AllArgsConstructor
public class StudentTournamentController {

    private final TournamentService tournamentService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody SubscribeTntDTO subscribeTntDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();
        ServerResponse result;
        if(subscribeTntDTO.isTournamentPublic())
            result = tournamentService.subscribeToTournament(subscribeTntDTO.getNameOrKeyword(), username, null);
        else
            result = tournamentService.subscribeToTournament(null, username, subscribeTntDTO.getNameOrKeyword());

        if(result == ServerResponse.USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT)
            return ResponseEntity.ok(ServerResponse.toString(result));
        else
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/myTnt")
    public ResponseEntity<?> getMyTournaments(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<MyTournamentsDTO> tournamentList = tournamentService.getTournamentsByStudent((String) authentication.getPrincipal());
        return ResponseEntity.ok(tournamentList);
    }

    @PreAuthorize("hasRole('STUDENT')")
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

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/homePageTnt")
    public ResponseEntity<?> getUpcomingAndOngoingTournaments(){
        List<UpcomingAndOngoingTntDTO> upcomingAndOngoingTournaments = tournamentService.getUpcomingAndOngoingTournaments();
        return ResponseEntity.ok(upcomingAndOngoingTournaments);
    }
}
