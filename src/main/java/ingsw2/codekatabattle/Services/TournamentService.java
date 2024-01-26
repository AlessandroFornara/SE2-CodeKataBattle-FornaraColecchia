package ingsw2.codekatabattle.Services;

import ingsw2.codekatabattle.DAO.TournamentDAO;
import ingsw2.codekatabattle.DAO.UserDAO;
import ingsw2.codekatabattle.Entities.States.TournamentVisibility;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Entities.Tournament;
import ingsw2.codekatabattle.Entities.User;
import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Utils.KeywordGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class TournamentService {

    private final TournamentDAO tournamentDAO;
    private final UserDAO userDAO;

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
                //notificationService.notifyAllStudents();
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

    public ServerResponse subscribeToTournament(String name, String username, String keyword){

        ServerResponse result = null;
        if(keyword == null) {
            result = tournamentDAO.subscribeToTournament(name, username, TournamentVisibility.PUBLIC);
        }else
            result = tournamentDAO.subscribeToTournament(keyword, username, TournamentVisibility.PRIVATE);

        if (result == ServerResponse.USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT) {
            log.info("The student" + username + " has successfully subscribed to a tournament");
            //notificationService.notifyAllUsers(); //TODO:
        } else
            log.error(ServerResponse.toString(result));

        return result;
    }

    public ServerResponse promoteToModerator(String admin, String name, String moderator){
        User user = userDAO.getUserByUsername(moderator);
        if(user == null || !user.getRole().equals(UserRole.EDUCATOR)){
            return ServerResponse.USER_IS_NOT_REGISTERED;
        }

        ServerResponse result = tournamentDAO.promoteToModerator(admin, name, moderator);

        if(result == ServerResponse.USER_SUCCESSFULLY_PROMOTED_TO_MODERATOR){
            log.info("User " + moderator + " has been promoted to moderator of tournament " + name + " by " + admin);
            //notificationService.notifyNewModerator(); //TODO:
        }else
            log.error(ServerResponse.toString(result));
        return result;
    }

}
