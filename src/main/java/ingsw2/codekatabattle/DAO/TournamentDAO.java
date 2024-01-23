package ingsw2.codekatabattle.DAO;


import com.mongodb.client.result.UpdateResult;
import ingsw2.codekatabattle.Entities.Tournament;
import ingsw2.codekatabattle.Model.ServerResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@AllArgsConstructor
public class TournamentDAO {

    private final MongoOperations mongoOperations;
    private final String collectionName = "Tournament";

    public ServerResponse saveTournament(Tournament tnt){

        try {
            mongoOperations.insert(tnt, collectionName);
            return ServerResponse.TOURNAMENT_SUCCESSFULLY_SAVED;
        }catch (Exception e){
            return ServerResponse.TOURNAMENT_ALREADY_EXISTS;
        }
    }

    public boolean checkIfKeywordAlreadyExists(String kw){

        Query q = new Query();
        q.addCriteria(Criteria.where("keyword").is(kw));

        return mongoOperations.exists(q, collectionName);
    }

    @Transactional(transactionManager = "primaryTransactionManager", rollbackFor = {Exception.class})
    public boolean addEndDate(String tntName, Date end){

        Query q = new Query();
        q.addCriteria(Criteria.where("name").is(tntName));

        Update u = new Update();
        u.set("endDate", end);

        UpdateResult result = mongoOperations.updateFirst(q, u, "Tournament");

        if(result.getModifiedCount()==1){
            return true;
        } else {
            return false;
        }
    }

}

