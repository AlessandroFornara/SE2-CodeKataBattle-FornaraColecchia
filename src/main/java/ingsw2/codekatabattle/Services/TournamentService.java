package ingsw2.codekatabattle.Services;

import ingsw2.codekatabattle.DAO.BattleDAO;
import ingsw2.codekatabattle.DAO.TournamentDAO;
import ingsw2.codekatabattle.DAO.UserDAO;
import ingsw2.codekatabattle.Entities.States.TournamentVisibility;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Entities.Tournament;
import ingsw2.codekatabattle.Entities.User;
import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.MyTournamentsDTO;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.UpcomingAndOngoingTntDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Utils.KeywordGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for handling tournament-related operations in the Code Kata Battle application.
 * This service includes methods for creating and managing tournaments, subscribing users to tournaments, and retrieving tournament-related information.
 */
@Service
@AllArgsConstructor
@Slf4j
public class TournamentService {

    private final TournamentDAO tournamentDAO;
    private final BattleDAO battleDAO;
    private final UserDAO userDAO;
    private final NotificationService notificationService;

    /**
     * Creates a new tournament with specified parameters.
     * The method validates the registration deadline, generates a keyword for private tournaments, and saves the tournament information.
     * @param name The name of the new tournament.
     * @param admin The username of the tournament's creator/admin.
     * @param registrationDeadline The deadline for registering to the tournament.
     * @param isPublic Boolean indicating whether the tournament is public or private.
     * @return KeywordResponse containing the result of the tournament creation process and the generated keyword for private tournaments.
     */
    public KeywordResponse createTournament(String name, String admin, Date registrationDeadline, boolean isPublic) {

        Tournament tnt;

        //First check is the registration deadline is valid
        if (registrationDeadline.before(new Date())) {
            log.error("Registration deadline not valid: " + registrationDeadline);
            return new KeywordResponse(ServerResponse.REGISTRATION_DEADLINE_NOT_VALID);
        }

        if(!isPublic) {
            //Generate a unique keyword and create a tournament object
            String keyword;
            do {
                keyword = KeywordGenerator.generateKeyword();
            } while (tournamentDAO.checkIfKeywordAlreadyExists(keyword));

            //Create a tournament object
            tnt = new Tournament(name, admin, TournamentVisibility.PRIVATE, registrationDeadline, keyword);
        }else
            tnt = new Tournament(name, admin, TournamentVisibility.PUBLIC, registrationDeadline, null);
        //Save the tournament in the database
        ServerResponse result = tournamentDAO.saveTournament(tnt);

        //If the DAO was successful in saving the tournament
        if(result == ServerResponse.TOURNAMENT_SUCCESSFULLY_SAVED){
            if(isPublic){
                log.info("A PUBLIC tournament has been created: " + tnt.getName());
                notificationService.notifyAllStudents(tnt.getName());
                return new KeywordResponse(ServerResponse.PUBLIC_TOURNAMENT_CREATED);
            }else{
                log.info("A PRIVATE tournament has been created: " + tnt.getName());
                return new KeywordResponse(ServerResponse.PRIVATE_TOURNAMENT_CREATED, tnt.getKeyword());
            }
        } else {
            log.error(ServerResponse.toString(result));
            return new KeywordResponse(result);
        }
    }

    /**
     * Closes a tournament.
     * The method checks if all battles in the tournament are closed before proceeding to close the tournament.
     * @param name The name of the tournament to be closed.
     * @param admin The username of the admin attempting to close the tournament.
     * @return ServerResponse indicating the result of the tournament closure process.
     */
    public ServerResponse closeTournament(String name, String admin) {

        //Check if all battles in the tournament are closed
        boolean allBattlesClosed = battleDAO.checkIfAllBattlesClosed(name);
        if(!allBattlesClosed){
            log.error(ServerResponse.toString(ServerResponse.TOURNAMENT_BATTLES_IN_PROGRESS));
            return ServerResponse.TOURNAMENT_BATTLES_IN_PROGRESS;
        }

        //DAO tries to close the tournament
        ServerResponse result = tournamentDAO.addEndDate(admin, name, new Date());

        //Check if the tournament has been successfully closed
        if (result == ServerResponse.TOURNAMENT_CLOSED_OK) {
            log.info("The tournament" + name + " has been successfully closed");
            notificationService.notifySubscribedStudents(name);
        } else
            log.error(ServerResponse.toString(result));

        return result;
    }

    /**
     * Subscribes a user to a tournament.
     * The method handles subscriptions to both public and private tournaments.
     * @param name The name of the tournament (for public tournaments).
     * @param username The username of the user subscribing to the tournament.
     * @param keyword The keyword for private tournaments.
     * @return ServerResponse indicating the result of the subscription process.
     */
    public ServerResponse subscribeToTournament(String name, String username, String keyword){

        ServerResponse result = null;
        if(keyword == null) {
            result = tournamentDAO.subscribeToTournament(name, username, TournamentVisibility.PUBLIC);
        }else
            result = tournamentDAO.subscribeToTournament(keyword, username, TournamentVisibility.PRIVATE);

        if (result == ServerResponse.USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT) {
            log.info("The student" + username + " has successfully subscribed to a tournament");
            if(keyword == null)
                notificationService.notifyUpcomingBattles(username, name, TournamentVisibility.PUBLIC);
            else notificationService.notifyUpcomingBattles(username, keyword, TournamentVisibility.PRIVATE);
        } else
            log.error(ServerResponse.toString(result));

        return result;
    }

    /**
     * Promotes a user to the role of moderator for a specific tournament.
     * The method checks if the user exists and is an educator before promoting them.
     * @param admin The username of the admin performing the promotion.
     * @param name The name of the tournament.
     * @param moderator The username of the user being promoted.
     * @return ServerResponse indicating the result of the promotion process.
     */
    public ServerResponse promoteToModerator(String admin, String name, String moderator){
        User user = userDAO.getUserByUsername(moderator);
        if(user == null || !user.getRole().equals(UserRole.EDUCATOR)){
            return ServerResponse.USER_IS_NOT_REGISTERED;
        }

        ServerResponse result = tournamentDAO.promoteToModerator(admin, name, moderator);

        if(result == ServerResponse.USER_SUCCESSFULLY_PROMOTED_TO_MODERATOR){
            log.info("User " + moderator + " has been promoted to moderator of tournament " + name + " by " + admin);
            notificationService.notifyNewModerator(moderator, name);
        }else
            log.error(ServerResponse.toString(result));
        return result;
    }

    /**
     * Retrieves a list of tournaments created by an educator.
     * @param username The username of the educator.
     * @return A list of MyTournamentsDTO representing the tournaments created by the educator.
     */
    public List<MyTournamentsDTO> getTournamentsByEducator(String username){
        return tournamentDAO.getTournamentsByEducator(username);
    }

    /**
     * Retrieves a list of tournaments a student is enrolled in.
     * @param username The username of the student.
     * @return A list of MyTournamentsDTO representing the tournaments the student is enrolled in.
     */
    public List<MyTournamentsDTO> getTournamentsByStudent(String username){
        return tournamentDAO.getTournamentsByStudent(username);
    }

    /**
     * Provides detailed information about a specific tournament.
     * The method performs checks based on the user's role before providing the information.
     * @param name The name of the tournament.
     * @param username The username of the user requesting the information.
     * @param role The role of the user (STUDENT or EDUCATOR).
     * @return The Tournament entity if the user is authorized to view it, otherwise null.
     */
    public Tournament getTournamentInfo(String name, String username, UserRole role){
        Tournament tournament = tournamentDAO.getTournamentInfo(name, username, role);
        if(tournament != null && tournament.getRank() != null && !tournament.getRank().keySet().isEmpty()) {
            HashMap<String, Integer> sortedMap = tournament.getRank().entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(
                            LinkedHashMap::new,
                            (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                            LinkedHashMap::putAll
                    );
            tournament.setRank(sortedMap);
        }
        return tournament;
    }


    /**
     * Retrieves a list of upcoming and ongoing tournaments.
     * @return A list of UpcomingAndOngoingTntDTO representing upcoming and ongoing tournaments.
     */
    public List<UpcomingAndOngoingTntDTO> getUpcomingAndOngoingTournaments(){
        return tournamentDAO.getUpcomingAndOngoingTournaments();
    }



}
