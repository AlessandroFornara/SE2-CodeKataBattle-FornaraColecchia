package ingsw2.codekatabattle.DAO;

import com.mongodb.client.result.UpdateResult;
import ingsw2.codekatabattle.Entities.Battle;
import ingsw2.codekatabattle.Entities.Team;
import ingsw2.codekatabattle.Model.KeywordResponse;
import ingsw2.codekatabattle.Model.ServerResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class BattleDAO {

    private final MongoOperations mongoOperations;
    private final String collectionName = "Battle";

    public ServerResponse saveBattle(Battle battle){

        int index = battle.getName().indexOf("-");
        Query q = new Query();
        String regexPattern = "^" + Pattern.quote(battle.getName().substring(0, index)) + "-.*";
        q.addCriteria(Criteria.where("_id").regex(regexPattern));
        Criteria sequential = Criteria.where("submitDate").gt(battle.getRegistrationDeadline());
        q.addCriteria(sequential);
        if(mongoOperations.exists(q, collectionName)){
            return ServerResponse.BATTLE_CREATION_ERROR;
        }
        try {
            mongoOperations.insert(battle, collectionName);
            return ServerResponse.BATTLE_SUCCESSFULLY_CREATED;
        }catch (Exception e){
            return ServerResponse.BATTLE_ALREADY_EXISTS;
        }
    }

    public boolean checkIfKeywordAlreadyExists(String kw){

        Query q = new Query();
        q.addCriteria(Criteria.where("teams.keyword").is(kw));

        return mongoOperations.exists(q, collectionName);
    }

    public KeywordResponse createTeam(Team team, String battle, String creator){

        Query q = new Query();
        Criteria battleName = Criteria.where("_id").is(battle);
        Criteria teamNameCriteria = Criteria.where("teams.name").nin(team.getName());
        Criteria regDeadlineNotExpired = Criteria.where("registrationDeadline").gt(new Date());
        Criteria creatorNotInAnyTeam = Criteria.where("teams.members." + creator).not().exists(true);
        q.addCriteria(new Criteria().andOperator(battleName, teamNameCriteria, regDeadlineNotExpired, creatorNotInAnyTeam));

        Update u = new Update();
        u.push("teams", team);
        UpdateResult updateResult = mongoOperations.updateFirst(q, u, collectionName);

        if (updateResult.getModifiedCount() == 1) {
            return new KeywordResponse(ServerResponse.TEAM_SUCCESSFULLY_CREATED, team.getKeyword());
        } else {
            return new KeywordResponse(ServerResponse.TEAM_NAME_NOT_AVAILABLE);
        }
    }

    public ServerResponse joinTeam(String keyword, String battle, String username){
        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(battle));
        q.fields().include("maxPlayers");
        q.fields().include("minPlayers");

        List<Battle> maxP = mongoOperations.find(q, Battle.class, collectionName);
        //System.out.println(maxP.get(0).getMaxPlayers());

        q.addCriteria(Criteria.where("registrationDeadline").gt(new Date()));
        q.addCriteria(Criteria.where("teams.members").nin(username));

        Criteria k = Criteria.where("keyword").is(keyword);
        Criteria n = Criteria.where("members").not().size(maxP.get(0).getMaxPlayers());

        q.addCriteria(Criteria.where("teams").elemMatch(new Criteria().andOperator(k,n)));

        Update u = new Update();

        u.filterArray(Criteria.where("t.keyword").is(keyword));
        u.push("teams.$[t].members", username);
        u.push("teams.$[t].scores", 0);

        UpdateResult updateResult = mongoOperations.updateFirst(q, u, collectionName);

        if (updateResult.getModifiedCount() == 1) {
            return ServerResponse.JOIN_TEAM_SUCCESS;
        } else {
            return ServerResponse.JOIN_TEAM_FAILED;
        }
    }

    public boolean updateScore(String keyword, String battle, int points){
        Date date = new Date();
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(battle));
        query.addCriteria(Criteria.where("submitDate").gt(date));
        query.addCriteria(Criteria.where("registrationDeadline").lt(date));
        //si può volendo aggiungere l'errore che non c'è il team con il MatchedCount o va tolta
        query.addCriteria(Criteria.where("teams.keyword").is(keyword));

        Update u = new Update();
        u.filterArray(Criteria.where("t.keyword").is(keyword));
        u.set("teams.$[t].scores.$[]", points);
        u.set("teams.$[t].points", points);

        UpdateResult updateResult = mongoOperations.updateFirst(query, u, Battle.class);

        if (updateResult.getModifiedCount() == 1) {
            return true;
        } else {
            return false;
        }

    }

}