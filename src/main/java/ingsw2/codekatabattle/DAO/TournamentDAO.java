package ingsw2.codekatabattle.DAO;


import com.mongodb.client.result.UpdateResult;
import ingsw2.codekatabattle.Entities.Tournament;
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

    @Transactional(transactionManager = "primaryTransactionManager", rollbackFor = {Exception.class})
    public boolean saveTournament(Tournament tnt){

        Query q = new Query();
        q.addCriteria(Criteria.where("name").is(tnt.getName()));

        if(mongoOperations.exists(q,collectionName)){
            return false;
        } else {
            mongoOperations.insert(tnt,collectionName);
            return true;
        }
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

