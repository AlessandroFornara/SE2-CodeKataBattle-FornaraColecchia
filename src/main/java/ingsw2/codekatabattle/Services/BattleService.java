package ingsw2.codekatabattle.Services;

import ingsw2.codekatabattle.DAO.BattleDAO;
import ingsw2.codekatabattle.DAO.TournamentDAO;
import ingsw2.codekatabattle.Entities.Battle;
import ingsw2.codekatabattle.Entities.CodeKata;
import ingsw2.codekatabattle.Entities.Team;
import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Utils.KeywordGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Slf4j
public class BattleService {

    private final BattleDAO battleDAO;
    private final TournamentDAO tournamentDAO;
    private final TaskScheduler taskScheduler;
    private final GitHubService gitHubService;

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
                .forEach(i -> files.put("input" + (i + 1), codeKata.getInput().get(i)));

        IntStream.range(0, codeKata.getOutput().size())
                .forEach(i -> files.put("output" + (i + 1), codeKata.getOutput().get(i)));

        files.put("description", codeKata.getDescription());

        if(result.equals(ServerResponse.BATTLE_SUCCESSFULLY_CREATED)) {
            taskScheduler.schedule(() -> gitHubService.createRepositoryAndUploadFiles(tournamentName + "-" + name, files), registrationDeadline);
            //notificationService.notifyNewBattle();
        }
        return result;
    }

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

    public ServerResponse joinTeam(String keyword, String battleName, String username){

        int index = battleName.indexOf("-");
        if(!tournamentDAO.checkIfSubscribed(username, battleName.substring(0, index))){
            System.out.println(battleName.substring(0, index));
            return ServerResponse.USER_IS_NOT_SUBSCRIBED_TO_TOURNAMENT;
        }

        return battleDAO.joinTeam(keyword, battleName, username);
    }

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

    public ServerResponse closeConsolidationStage(String admin, String battleName){
        ServerResponse result = updateConsolidationStage(admin, battleName);
        //notificationService.notifyCloseConsStage();
        return result;
    }

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

}
