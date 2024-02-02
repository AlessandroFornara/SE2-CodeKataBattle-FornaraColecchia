package ingsw2.codekatabattle.DAO;


import com.mongodb.client.result.UpdateResult;
import ingsw2.codekatabattle.Entities.States.TournamentVisibility;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Entities.Team;
import ingsw2.codekatabattle.Entities.Tournament;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.MyTournamentsDTO;
import ingsw2.codekatabattle.Model.SeeInfoDTOS.UpcomingAndOngoingTntDTO;
import ingsw2.codekatabattle.Model.ServerResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    public ServerResponse addEndDate(String username, String tntName, Date end){

        Query q = new Query();
        Criteria name = Criteria.where("_id").is(tntName);
        Criteria notClosed = Criteria.where("endDate").isNull();
        Criteria isAdmin = Criteria.where("admin").is(username);

        q.addCriteria(new Criteria().andOperator(name, notClosed, isAdmin));

        Update u = new Update();
        u.set("endDate", end);
        UpdateResult updateResult = mongoOperations.updateFirst(q, u, collectionName);

        if(updateResult.getModifiedCount()==1)
            return ServerResponse.TOURNAMENT_CLOSED_OK;
        else if(updateResult.getMatchedCount() == 1)
            return ServerResponse.UNSUCCESSFUL_UPDATE;
        else
            return ServerResponse.TOURNAMENT_IS_ALREADY_CLOSED_OR_USER_NOT_ADMIN;
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

    public ServerResponse promoteToModerator(String admin, String name, String moderator){

        Query q =new Query();
        q.addCriteria(Criteria.where("_id").is(name));
        List<Tournament> result = mongoOperations.find(q, Tournament.class, collectionName);
        if(result.isEmpty()) {
            return ServerResponse.TOURNAMENT_DOESNT_EXIST;
        } else {
            Tournament t = result.get(0);

            if(!t.getAdmin().equals(admin)){
                return ServerResponse.USER_IS_NOT_ADMIN;
            }
            else if(t.getAdmin().equals(moderator)){
                return ServerResponse.USER_IS_ALREADY_ADMIN;
            }
            else if(t.getModerators().contains(moderator)){
                return ServerResponse.USER_IS_ALREADY_MODERATOR;
            }
            else if(t.getEndDate() != null){
                return ServerResponse.TOURNAMENT_ALREADY_CLOSED;
            }

            Update u = new Update();
            u.push("moderators", moderator);
            UpdateResult updateResult = mongoOperations.updateFirst(q, u, collectionName);

            if (updateResult.getModifiedCount() == 1) {
                return ServerResponse.USER_SUCCESSFULLY_PROMOTED_TO_MODERATOR;
            } else {
                return ServerResponse.UNSUCCESSFUL_UPDATE;
            }
        }
    }

    public List<MyTournamentsDTO> getTournamentsByEducator(String username){

        Query q =new Query();
        Criteria isAdmin = Criteria.where("admin").is(username);
        Criteria isModerator = Criteria.where("moderators").is(username);
        //Criteria notClosed = Criteria.where("endDate").isNull();

        Criteria isAdminOrModerator = new Criteria().orOperator(isAdmin, isModerator);
        q.addCriteria(isAdminOrModerator);
        q.fields().include("_id");
        q.fields().include("visibility");
        q.fields().include("endDate");

        q.with(Sort.by(Sort.Direction.ASC, "_id"));

        return mongoOperations.find(q, MyTournamentsDTO.class, collectionName);
    }

    public List<MyTournamentsDTO> getTournamentsByStudent(String username){

        Query q =new Query();
        Criteria isSubscribed = Criteria.where("rank." + username).exists(true);
        Criteria notClosed = Criteria.where("endDate").isNull();

        q.addCriteria(new Criteria().andOperator(isSubscribed, notClosed));
        q.fields().include("_id");
        q.fields().include("visibility");
        q.fields().include("endDate");

        return mongoOperations.find(q, MyTournamentsDTO.class, collectionName);
    }

    public Tournament getTournamentInfo(String name, String username, UserRole role){
        Query q = new Query();
        Criteria tournamentName = Criteria.where("_id").is(name);

        Criteria isAdmin = Criteria.where("admin").is(username);
        Criteria isModerator = Criteria.where("moderators").is(username);
        Criteria isAdminOrModerator = new Criteria().orOperator(isAdmin, isModerator);
        //Criteria notClosed = Criteria.where("endDate").isNull();

        Criteria isSubscribed = Criteria.where("rank." + username).exists(true);
        Criteria isPublic = Criteria.where("visibility").is("PUBLIC");
        Criteria privateAndAdminOrModerator = new Criteria().orOperator(isPublic, isAdminOrModerator);
        Criteria privateAndSubscribed = new Criteria().orOperator(isPublic, isSubscribed);

        q.addCriteria(tournamentName);
        //q.addCriteria(notClosed);
        q.fields().exclude("keyword");

        if(role.equals(UserRole.EDUCATOR)){
            q.addCriteria(privateAndAdminOrModerator.is(true));
        }else{
            q.addCriteria(privateAndSubscribed.is(true));
        }

        return mongoOperations.findOne(q, Tournament.class, collectionName);
    }

    public List<UpcomingAndOngoingTntDTO> getUpcomingAndOngoingTournaments(){
        Query q = new Query();

        Criteria notClosed = Criteria.where("endDate").isNull();
        Criteria isPublic = Criteria.where("visibility").is("PUBLIC");
        q.addCriteria(notClosed);
        q.addCriteria(isPublic);
        q.fields().include("_id");
        q.fields().include("admin");
        q.fields().include("registrationDeadline");

        q.with(Sort.by(Sort.Direction.ASC, "registrationDeadline"));

        return mongoOperations.find(q, UpcomingAndOngoingTntDTO.class, collectionName);
    }

    public boolean checkIfBattleCanBeCreated(String username, String name, Date battleRegistrationDeadline){
        Query q = new Query();
        Criteria tournamentName = Criteria.where("_id").is(name);

        Criteria isAdmin = Criteria.where("admin").is(username);
        Criteria isModerator = Criteria.where("moderators").is(username);
        Criteria isAdminOrModerator = new Criteria().orOperator(isAdmin, isModerator);

        Criteria notClosed = Criteria.where("endDate").isNull();
        Criteria regDeadlineValid = Criteria.where("registrationDeadline").lt(battleRegistrationDeadline);

        q.addCriteria(new Criteria().andOperator(tournamentName, isAdminOrModerator, notClosed, regDeadlineValid));

        return mongoOperations.exists(q, collectionName);

    }



    public boolean checkIfSubscribed(String username, String name){
        Query q = new Query();
        Criteria tournamentName = Criteria.where("_id").is(name);
        Criteria isSubscribed = Criteria.where("rank." + username).exists(true);

        q.addCriteria(new Criteria().andOperator(tournamentName, isSubscribed));

        return mongoOperations.exists(q, collectionName);
    }

    public boolean checkIfAdminOrModerator(String username, String name){
        Query q = new Query();
        Criteria tournamentName = Criteria.where("_id").is(name);

        Criteria isAdmin = Criteria.where("admin").is(username);
        Criteria isModerator = Criteria.where("moderators").is(username);
        Criteria isAdminOrModerator = new Criteria().orOperator(isAdmin, isModerator);

        q.addCriteria(new Criteria().andOperator(tournamentName, isAdminOrModerator));

        return mongoOperations.exists(q, collectionName);
    }

    public boolean checkIfAdmin(String username, String name){
        Query q = new Query();

        Criteria tournamentName = Criteria.where("_id").is(name);
        Criteria isAdmin = Criteria.where("admin").is(username);

        q.addCriteria(new Criteria().andOperator(tournamentName, isAdmin));

        return mongoOperations.exists(q, collectionName);
    }

    public boolean updateRank(String tntName, List<Team> teams){

        Query q = new Query().addCriteria(Criteria.where("_id").is(tntName));
        Update u = new Update();

        for (Team t : teams) {
            for (String student : t.getMembers()){
                u.inc("rank."+student, t.getScores().get(t.getMembers().indexOf(student)));
            }
        }

        UpdateResult result = mongoOperations.updateFirst(q,u,collectionName);

        if (result.getModifiedCount() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public List<Tournament> getTntByNameOrKeyword(String nameOrKeyword, TournamentVisibility visibility){

        Query query = new Query();
        if(visibility == TournamentVisibility.PUBLIC)
            query.addCriteria(Criteria.where("_id").is(nameOrKeyword));
        else query.addCriteria(Criteria.where("keyword").is(nameOrKeyword));

        return mongoOperations.find(query, Tournament.class, collectionName);
    }
}
