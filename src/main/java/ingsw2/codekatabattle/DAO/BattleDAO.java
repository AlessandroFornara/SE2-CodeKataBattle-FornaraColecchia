package ingsw2.codekatabattle.DAO;

import ingsw2.codekatabattle.Entities.Battle;
import ingsw2.codekatabattle.Model.ServerResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class BattleDAO {

    private final MongoOperations mongoOperations;
    private final String collectionName = "Battle";

    public ServerResponse saveBattle(Battle battle) {

        int index = battle.getName().indexOf("-");
        Query q = new Query();
        String regexPattern = "^" + Pattern.quote(battle.getName().substring(0, index)) + "-.*";
        q.addCriteria(Criteria.where("_id").regex(regexPattern));
        Criteria sequential = Criteria.where("submitDate").gt(battle.getRegistrationDeadline());
        q.addCriteria(sequential);
        if (mongoOperations.exists(q, collectionName)) {
            return ServerResponse.BATTLE_CREATION_ERROR;
        }
        try {
            mongoOperations.insert(battle, collectionName);
            return ServerResponse.BATTLE_SUCCESSFULLY_CREATED;
        } catch (Exception e) {
            return ServerResponse.BATTLE_ALREADY_EXISTS;
        }
    }
}