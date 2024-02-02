package ingsw2.codekatabattle.Services;

import ingsw2.codekatabattle.DAO.BattleDAO;
import ingsw2.codekatabattle.DAO.TournamentDAO;
import ingsw2.codekatabattle.Entities.Battle;
import ingsw2.codekatabattle.Entities.CodeKata;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Entities.Team;
import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.MyBattlesDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Utils.KeywordGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Service class for handling battle-related operations.
 * This service includes methods for creating battles, managing teams, updating scores, and other battle-related activities.
 */
@Service
@AllArgsConstructor
@Slf4j
public class BattleService {

    private final BattleDAO battleDAO;
    private final TournamentDAO tournamentDAO;
    private final TaskScheduler taskScheduler;
    private final GitHubService gitHubService;
    private final NotificationService notificationService;

    /**
     * Creates a new battle with specified parameters.
     * The method validates the provided deadlines, creates a new Battle entity, and schedules tasks related to the battle.
     * @param name The name of the new battle.
     * @param tournamentName The name of the tournament associated with this battle.
     * @param registrationDeadline The deadline for registration.
     * @param submissionDeadline The deadline for submitting solutions.
     * @param codeKata The Code Kata associated with the battle.
     * @param creator The username of the battle's creator.
     * @param maxPlayers The maximum number of players allowed in each team.
     * @param minPlayers The minimum number of players required in each team.
     * @return ServerResponse indicating the result of the battle creation process.
     */
    public ServerResponse createBattle(String name, String tournamentName, Date registrationDeadline, Date submissionDeadline, CodeKata codeKata, String creator, int maxPlayers, int minPlayers){
        if (registrationDeadline.before(new Date())) {
            log.error("Registration deadline not valid: " + registrationDeadline);
            return ServerResponse.REGISTRATION_DEADLINE_NOT_VALID;
        }

        if(submissionDeadline.before(registrationDeadline)){
            log.error("Submission deadline not valid: " + submissionDeadline);
            return ServerResponse.SUBMISSION_DEADLINE_NOT_VALID;
        }
        if(maxPlayers < minPlayers || !tournamentDAO.checkIfBattleCanBeCreated(creator, tournamentName, registrationDeadline))
            return ServerResponse.BATTLE_CREATION_ERROR;

        ServerResponse result = battleDAO.saveBattle(new Battle(tournamentName+"-"+name, creator, codeKata, new ArrayList<>(), registrationDeadline, submissionDeadline, maxPlayers, minPlayers));

        HashMap<String, String> files = new HashMap<>();
        IntStream.range(0, codeKata.getInput().size())
                .forEach(i -> files.put("input" + (i + 1) + ".txt", codeKata.getInput().get(i)));

        IntStream.range(0, codeKata.getOutput().size())
                .forEach(i -> files.put("output" + (i + 1) + ".txt", codeKata.getOutput().get(i)));

        files.put("description.txt", codeKata.getDescription());
        files.put("configuration.yaml", codeKata.getConfigurationFile());

        if(result.equals(ServerResponse.BATTLE_SUCCESSFULLY_CREATED)) {
            taskScheduler.schedule(() -> gitHubService.createRepositoryAndUploadFiles(tournamentName + "-" + name, files), registrationDeadline);
            notificationService.notifyNewBattle(tournamentName + "-" + name);
        }
        return result;
    }

    /**
     * Provides detailed information about a specific battle.
     * The method performs checks based on the user's role before providing the information.
     * @param name The name of the battle.
     * @param username The username of the user requesting the information.
     * @param role The role of the user (STUDENT or EDUCATOR).
     * @return The Battle entity if the user is authorized to view it, otherwise null.
     */
    public Battle getBattleInfo(String name, String username, UserRole role) {

        int index = name.indexOf("-");
        if (role.equals(UserRole.STUDENT)) {
            if (!tournamentDAO.checkIfSubscribed(username, name.substring(0, index)))
                return null;
            if(!battleDAO.checkIfSubscribed(username, name))
                return null;
        } else {
            if (!tournamentDAO.checkIfAdminOrModerator(username, name.substring(0, index)))
                return null;
        }
        Battle battle = battleDAO.getBattleInfo(name);
        battle.getTeams().sort(Comparator.comparing(Team::getPoints).reversed());
        return battle;
    }

    /**
     * Creates a new team for a specific battle.
     * This method generates a unique keyword for the team, ensuring the creator is subscribed to the relevant tournament.
     * @param teamName The name of the new team.
     * @param battleName The name of the battle for which the team is being created.
     * @param creator The username of the team's creator.
     * @return KeywordResponse containing the result of the team creation process and the generated keyword.
     */
    public KeywordResponse createTeam(String teamName, String battleName, String creator){

        String keyword;
        int index = battleName.indexOf("-");
        if(!tournamentDAO.checkIfSubscribed(creator, battleName.substring(0, index))){
            System.out.println(battleName.substring(0, index));
            return new KeywordResponse(ServerResponse.USER_IS_NOT_SUBSCRIBED_TO_TOURNAMENT);
        }
        ArrayList<String> members = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();

        members.add(creator);
        scores.add(0);

        do {
            keyword = KeywordGenerator.generateKeyword();
        }while (battleDAO.checkIfKeywordAlreadyExists(keyword));

        Team team = new Team(teamName, members, scores, keyword, 0);

        return battleDAO.createTeam(team, battleName, creator);
    }

    /**
     * Allows a user to join an existing team in a battle.
     * The method validates if the user is subscribed to the tournament associated with the battle before allowing them to join.
     * @param keyword The unique keyword associated with the team.
     * @param battleName The name of the battle.
     * @param username The username of the user who wants to join the team.
     * @return ServerResponse indicating the result of the join team process.
     */
    public ServerResponse joinTeam(String keyword, String battleName, String username){

        int index = battleName.indexOf("-");
        if(!tournamentDAO.checkIfSubscribed(username, battleName.substring(0, index))){
            System.out.println(battleName.substring(0, index));
            return ServerResponse.USER_IS_NOT_SUBSCRIBED_TO_TOURNAMENT;
        }

        return battleDAO.joinTeam(keyword, battleName, username);
    }

    /**
     * Updates the score for a team in a battle based on submitted outputs.
     * The method calculates points based on the submission and registration deadlines, comparing submitted outputs with expected ones.
     * @param keyword The unique keyword of the team.
     * @param battle The name of the battle.
     * @param outputs The list of outputs submitted by the team.
     * @return ServerResponse indicating the result of the score update process.
     */
    public ServerResponse updateScore(String keyword, String battle, List<String> outputs){

        Battle battleInfo = battleDAO.getBattleInfo(battle);
        if(battleInfo == null)
            return ServerResponse.GENERIC_ERROR;

        List<String> educatorOutputs = battleInfo.getCodeKata().getOutput();
        Date submitDate = battleInfo.getSubmitDate();
        Date registrationDeadline = battleInfo.getRegistrationDeadline();

        long currentTime = new Date().getTime();
        long submitTime = submitDate.getTime();
        long registrationTime = registrationDeadline.getTime();

        if(currentTime < registrationTime || currentTime > submitTime){
            return ServerResponse.GENERIC_ERROR;
        }

        int points = gitHubService.computePoints(registrationDeadline, submitDate, educatorOutputs, outputs);

        boolean result = battleDAO.updateScore(keyword, battle, points);
        if(result){
            return ServerResponse.AUTOMATED_EVALUATION_OK;
        }else{
            return ServerResponse.GENERIC_ERROR;
        }
    }

    /**
     * Manually evaluates the participants of a battle.
     * This method allows educators to assign or adjust scores for participants in a battle.
     * @param user The username of the educator performing the evaluation.
     * @param battle The name of the battle being evaluated.
     * @param usernames Array of usernames of the participants being evaluated.
     * @param newScores Array of scores corresponding to the participants.
     * @return ServerResponse indicating the success or failure of the evaluation process.
     */
    public ServerResponse evaluate(String user, String battle, String[] usernames, int[] newScores){

        if(usernames.length != newScores.length)
            return ServerResponse.EVALUATION_FAILED;

        for(int i = 0; i < newScores.length; i++){
            if(newScores[i] < 0 || newScores[i] > 100)
                return ServerResponse.EVALUATION_FAILED;
        }

        int index = battle.indexOf("-");
        if(!tournamentDAO.checkIfAdminOrModerator(user, battle.substring(0, index))){
            return ServerResponse.EVALUATION_FAILED;
        }

        boolean result = battleDAO.evaluate(battle, usernames, newScores);
        if(result)
            return ServerResponse.EVALUATION_SUCCESSFUL;
        else return ServerResponse.EVALUATION_FAILED;
    }

    /**
     * Closes the consolidation stage of a battle.
     * This method updates the consolidation stage status of a battle and sends out notifications accordingly.
     * @param admin The username of the admin initiating the closure.
     * @param battleName The name of the battle whose consolidation stage is being closed.
     * @return ServerResponse indicating the result of the closure process.
     */
    public ServerResponse closeConsolidationStage(String admin, String battleName){
        ServerResponse result = updateConsolidationStage(admin, battleName);
        notificationService.notifyCloseConsStage(battleName);
        return result;
    }

    /**
     * Updates the consolidation stage status of a battle.
     * This transactional method checks if the user requesting the update is an admin for the tournament associated with the battle.
     * If the user is authorized, the method proceeds to update the consolidation stage status and may update the tournament rankings based on the battle results.
     * @param admin The username of the admin attempting to update the consolidation stage.
     * @param battleName The name of the battle whose consolidation stage is being updated.
     * @return ServerResponse indicating the result of the update process, such as success, failure, or lack of admin privileges.
     */
    @Transactional(transactionManager = "primaryTransactionManager", rollbackFor = {Exception.class})
    public ServerResponse updateConsolidationStage(String admin, String battleName){
        int index = battleName.indexOf("-");
        String tournamentName = battleName.substring(0, index);
        if(!tournamentDAO.checkIfAdmin(admin, tournamentName))
            return ServerResponse.USER_IS_NOT_ADMIN;

        Battle battle = battleDAO.checkIfCanBeClosed(battleName);

        if(battle == null){
            return ServerResponse.CONS_STAGE_CLOSED_FAILED;
        }
        boolean result;
        if(battle.getTeams() != null) {
            result = tournamentDAO.updateRank(tournamentName, battle.getTeams());
            if(result)
                return ServerResponse.CONS_STAGE_CLOSED_SUCCESSFULLY;
        }
        return ServerResponse.CONS_STAGE_CLOSED_SUCCESSFULLY;
    }

    /**
     * Retrieves a list of battles created by an educator.
     * @param username The username of the educator.
     * @return A list of MyBattlesDTO representing the battles created by the educator.
     */
    public List<MyBattlesDTO> getBattlesByEducator(String username){
        return battleDAO.getBattlesByEducator(username);
    }

    /**
     * Retrieves a list of battles a student is enrolled in.
     * @param username The username of the student.
     * @return A list of MyBattlesDTO representing the battles the student is enrolled in.
     */
    public List<MyBattlesDTO> getBattlesByStudent(String username){
        return battleDAO.getBattlesByStudent(username);
    }

    /**
     * Retrieves a list of all battles associated with a specific tournament.
     * @param tournamentName The name of the tournament for which battles are being queried.
     * @return A list of Battle entities associated with the given tournament.
     */
    public List<Battle> getTntBattles(String tournamentName){
        return battleDAO.getTntBattles(tournamentName);
    }
}
