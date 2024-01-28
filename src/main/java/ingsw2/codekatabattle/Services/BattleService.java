package ingsw2.codekatabattle.Services;

import ingsw2.codekatabattle.DAO.BattleDAO;
import ingsw2.codekatabattle.DAO.TournamentDAO;
import ingsw2.codekatabattle.Entities.Battle;
import ingsw2.codekatabattle.Entities.CodeKata;
import ingsw2.codekatabattle.Model.ServerResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Slf4j
public class BattleService {

    private final BattleDAO battleDAO;
    private final TournamentDAO tournamentDAO;
    private final TaskScheduler taskScheduler;
    private final GitHubService gitHubService;

    public ServerResponse createBattle(String name, String tournamentName, Date registrationDeadline, Date submissionDeadline, CodeKata codeKata, String creator, int maxPlayers, int minPlayers) {
        if (registrationDeadline.before(new Date())) {
            log.error("Registration deadline not valid: " + registrationDeadline);
            return ServerResponse.REGISTRATION_DEADLINE_NOT_VALID;
        }

        if (submissionDeadline.before(registrationDeadline)) {
            log.error("Submission deadline not valid: " + submissionDeadline);
            return ServerResponse.SUBMISSION_DEADLINE_NOT_VALID;
        }
        if (maxPlayers < minPlayers || !tournamentDAO.checkIfBattleCanBeCreated(creator, tournamentName, registrationDeadline))
            return ServerResponse.BATTLE_CREATION_ERROR;

        ServerResponse result = battleDAO.saveBattle(new Battle(tournamentName + "-" + name, creator, codeKata, new ArrayList<>(), registrationDeadline, submissionDeadline, maxPlayers, minPlayers));

        HashMap<String, String> files = new HashMap<>();
        IntStream.range(0, codeKata.getInput().size())
                .forEach(i -> files.put("input" + (i + 1), codeKata.getInput().get(i)));

        IntStream.range(0, codeKata.getOutput().size())
                .forEach(i -> files.put("output" + (i + 1), codeKata.getOutput().get(i)));

        files.put("description", codeKata.getDescription());

        if (result.equals(ServerResponse.BATTLE_SUCCESSFULLY_CREATED)) {
            taskScheduler.schedule(() -> gitHubService.createRepositoryAndUploadFiles(tournamentName + "-" + name, files), registrationDeadline);
            //notificationService.notifyNewBattle();
        }
        return result;
    }
}