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

/**
 * Controller handling tournament management operations for educators.
 * All endpoints are mapped under the "/api/eduTnt" base URL.
 * This controller allows educators to create, close, and manage tournaments,
 * promote users to moderators, and view their tournaments and tournament details.
 * Access to these endpoints is restricted to authenticated users with the 'EDUCATOR' role.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/eduTnt")
public class TournamentManagementController {

    private final TournamentService tournamentService;

    /**
     * Creates a new tournament. The details for the creation of the tournament, such as name, registration deadline,
     * and visibility, are provided in the TournamentCreationDTO.
     * @param tournamentCreationDTO The DTO containing the tournament creation details.
     * @return A ResponseEntity containing either a success message (with a keyword for private tournaments) or an error message.
     */
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

    /**
     * Closes an existing tournament. The name of the tournament to be closed is provided in the TournamentClosureDTO.
     * @param tournamentClosureDTO The DTO containing the tournament closure details.
     * @return A ResponseEntity indicating the outcome of the tournament closure operation.
     */
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

    /**
     * Promotes a user to the role of a moderator for a specific tournament.
     * The details for the promotion, including the tournament name and the user to be promoted, are provided in the PromoteToModeratorDTO.
     * @param promoteToModeratorDTO The DTO containing the promotion details.
     * @return A ResponseEntity indicating the outcome of the promotion operation.
     */
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

    /**
     * Retrieves a list of tournaments that the currently authenticated educator is involved in.
     * @return A ResponseEntity containing a list of MyTournamentsDTO objects representing the educator's tournaments.
     */
    @PreAuthorize("hasRole('EDUCATOR')")
    @GetMapping("/myTnt")
    public ResponseEntity<?> getMyTournaments(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<MyTournamentsDTO> tournamentList = tournamentService.getTournamentsByEducator((String) authentication.getPrincipal());
        return ResponseEntity.ok(tournamentList);
    }

    /**
     * Fetches detailed information about a specific tournament.
     * @param name The name of the tournament to retrieve information for.
     * @return A ResponseEntity containing the Tournament object if found, or an error message if the tournament doesn't exist.
     */
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

    /**
     * Retrieves a list of upcoming and ongoing tournaments.
     * @return A ResponseEntity containing a list of UpcomingAndOngoingTntDTO objects representing upcoming and ongoing tournaments.
     */
    @PreAuthorize("hasRole('EDUCATOR')")
    @GetMapping("/homePageTnt")
    public ResponseEntity<?> getUpcomingTournaments(){
        List<UpcomingAndOngoingTntDTO> upcomingAndOngoingTournaments = tournamentService.getUpcomingAndOngoingTournaments();
        return ResponseEntity.ok(upcomingAndOngoingTournaments);
    }

}
