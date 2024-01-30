package ingsw2.codekatabattle.DAO;


import com.mongodb.client.result.UpdateResult;
import ingsw2.codekatabattle.Entities.States.TournamentVisibility;
import ingsw2.codekatabattle.Entities.Team;
import ingsw2.codekatabattle.Entities.Tournament;
import ingsw2.codekatabattle.Model.ServerResponse;
import lombok.AllArgsConstructor;
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

    public ServerResponse saveTournament(Tournament tnt) {

        try {
            mongoOperations.insert(tnt, collectionName);
            return ServerResponse.TOURNAMENT_SUCCESSFULLY_SAVED;
        } catch (Exception e) {
            return ServerResponse.TOURNAMENT_ALREADY_EXISTS;
        }
    }


    public boolean checkIfKeywordAlreadyExists(String kw) {

        Query q = new Query();
        q.addCriteria(Criteria.where("keyword").is(kw));

        return mongoOperations.exists(q, collectionName);
    }

    public ServerResponse subscribeToTournament(String tntNameOrKeyword, String username, TournamentVisibility tournamentVisibility) {

        Query q = new Query();
        Criteria name = Criteria.where("_id").is(tntNameOrKeyword);
        Criteria keyword = Criteria.where("keyword").is(tntNameOrKeyword);
        Criteria regDeadlineNotExpired = Criteria.where("registrationDeadline").gt(new Date());
        Criteria notClosed = Criteria.where("endDate").isNull();
        Criteria notAlreadySubscribed = Criteria.where("rank." + username).isNull();
        Criteria isPublic = Criteria.where("visibility").is("PUBLIC");
        Criteria isPrivate = Criteria.where("visibility").is("PRIVATE");

        Criteria finalCriteria = new Criteria().andOperator(regDeadlineNotExpired, notClosed, notAlreadySubscribed);
        if (tournamentVisibility.equals(TournamentVisibility.PUBLIC)) {
            q.addCriteria(new Criteria().andOperator(finalCriteria, name, isPublic));
        } else
            q.addCriteria(new Criteria().andOperator(finalCriteria, keyword, isPrivate));

        Update u = new Update();
        u.set("rank." + username, 0);

        UpdateResult updateResult = mongoOperations.updateFirst(q, u, collectionName);

        if (updateResult.getModifiedCount() == 1) {
            return ServerResponse.USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT;
        } else if (updateResult.getMatchedCount() == 1) {
            return ServerResponse.UNSUCCESSFUL_UPDATE;
        } else {
            return ServerResponse.USER_ALREADY_SUBSCRIBED_OR_REG_DEADLINE_EXPIRED;
        }
    }

    public ServerResponse promoteToModerator(String admin, String name, String moderator) {

        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(name));
        List<Tournament> result = mongoOperations.find(q, Tournament.class, collectionName);
        if (result.isEmpty()) {
            return ServerResponse.TOURNAMENT_DOESNT_EXIST;
        } else {
            Tournament t = result.get(0);

            if (!t.getAdmin().equals(admin)) {
                return ServerResponse.USER_IS_NOT_ADMIN;
            } else if (t.getAdmin().equals(moderator)) {
                return ServerResponse.USER_IS_ALREADY_ADMIN;
            } else if (t.getModerators().contains(moderator)) {
                return ServerResponse.USER_IS_ALREADY_MODERATOR;
            } else if (t.getEndDate() != null) {
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

    public boolean checkIfBattleCanBeCreated(String username, String name, Date battleRegistrationDeadline) {
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


    public boolean checkIfSubscribed(String username, String name) {
        Query q = new Query();
        Criteria tournamentName = Criteria.where("_id").is(name);
        Criteria isSubscribed = Criteria.where("rank." + username).exists(true);

        q.addCriteria(new Criteria().andOperator(tournamentName, isSubscribed));

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

        UpdateResult result = mongoOperations.updateMulti(q,u,collectionName);

        if (result.getModifiedCount() == 1) {
            return true;
        } else {
            return false;
        }
    }

}
