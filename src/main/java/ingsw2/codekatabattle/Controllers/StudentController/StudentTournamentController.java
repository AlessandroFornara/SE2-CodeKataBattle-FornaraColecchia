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

/**
 * Controller handling tournament-related operations for students.
 * All endpoints are mapped under the "/api/studTnt" base URL.
 * This controller enables students to subscribe to tournaments, view their enrolled tournaments, access detailed tournament
 * information, and view upcoming and ongoing tournaments. Access is restricted to authenticated users with the 'STUDENT' role.
 */
@RestController
@RequestMapping("/api/studTnt")
@AllArgsConstructor
public class StudentTournamentController {

    private final TournamentService tournamentService;

    /**
     * Subscribes a student to a tournament.
     * @param subscribeTntDTO The DTO containing subscription details (tournament name/keyword, public/private status).
     * @return A ResponseEntity containing the server response indicating the outcome of the subscription operation.
     */
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

    /**
     * Retrieves the list of tournaments that the currently authenticated student is subscribed to.
     * @return A ResponseEntity containing a list of MyTournamentsDTO objects representing the student's tournaments.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/myTnt")
    public ResponseEntity<?> getMyTournaments(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<MyTournamentsDTO> tournamentList = tournamentService.getTournamentsByStudent((String) authentication.getPrincipal());
        return ResponseEntity.ok(tournamentList);
    }

    /**
     * Fetches detailed information about a specific tournament.
     * @param name The name of the tournament to retrieve information for.
     * @return A ResponseEntity containing the Tournament object if found, or an error message if the tournament doesn't exist.
     */
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

    /**
     * Retrieves a list of upcoming and ongoing tournaments.
     * @return A ResponseEntity containing a list of UpcomingAndOngoingTntDTO objects representing upcoming and ongoing tournaments.
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/homePageTnt")
    public ResponseEntity<?> getUpcomingAndOngoingTournaments(){
        List<UpcomingAndOngoingTntDTO> upcomingAndOngoingTournaments = tournamentService.getUpcomingAndOngoingTournaments();
        return ResponseEntity.ok(upcomingAndOngoingTournaments);
    }
}
