package ingsw2.codekatabattle.Entities;

import ingsw2.codekatabattle.Entities.States.TournamentVisibility;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Document(collection = "Tournament")
@Getter
public class Tournament {

    @MongoId
    private String name;
    private String admin;
    private ArrayList<String> moderators;
    private HashMap<String, Integer> rank;
    private TournamentVisibility visibility;
    private Date registrationDeadline;
    private Date endDate;
    private String keyword;

    public Tournament(String name, String admin, TournamentVisibility visibility, Date registrationDeadline, String keyword) {
        this.name = name;
        this.admin = admin;
        this.moderators = new ArrayList<>();
        this.rank = new HashMap<>();
        this.visibility = visibility;
        this.registrationDeadline = registrationDeadline;
        this.keyword = keyword;
    }
}
