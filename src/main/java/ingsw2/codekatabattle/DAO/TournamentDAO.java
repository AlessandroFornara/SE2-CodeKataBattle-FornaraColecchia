package ingsw2.codekatabattle.DAO;


import com.mongodb.client.result.UpdateResult;
import ingsw2.codekatabattle.Entities.States.TournamentVisibility;
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

    public ServerResponse subscribeToTournament(String tntNameOrKeyword, String username, TournamentVisibility tournamentVisibility){

        Query q = new Query();
        Criteria name = Criteria.where("_id").is(tntNameOrKeyword);
        Criteria keyword = Criteria.where("keyword").is(tntNameOrKeyword);
        Criteria regDeadlineNotExpired = Criteria.where("registrationDeadline").gt(new Date());
        Criteria notClosed = Criteria.where("endDate").isNull();
        Criteria notAlreadySubscribed = Criteria.where("rank." + username).isNull();
        Criteria isPublic = Criteria.where("visibility").is("PUBLIC");
        Criteria isPrivate = Criteria.where("visibility").is("PRIVATE");

        Criteria finalCriteria = new Criteria().andOperator(regDeadlineNotExpired, notClosed, notAlreadySubscribed);
        if(tournamentVisibility.equals(TournamentVisibility.PUBLIC)){
            q.addCriteria(new Criteria().andOperator(finalCriteria, name, isPublic));
        }
        else
            q.addCriteria(new Criteria().andOperator(finalCriteria, keyword, isPrivate));

        Update u = new Update();
        u.set("rank." + username, 0);

        UpdateResult updateResult = mongoOperations.updateFirst(q, u, collectionName);

        if (updateResult.getModifiedCount() == 1) {
            return ServerResponse.USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT;
        } else if(updateResult.getMatchedCount() == 1) {
            return ServerResponse.UNSUCCESSFUL_UPDATE;
        }else {
            return ServerResponse.USER_ALREADY_SUBSCRIBED_OR_REG_DEADLINE_EXPIRED;
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

