package ingsw2.codekatabattle.Entities;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.Date;

@Document(collection = "Battle")
@Getter
public class Battle {

    @MongoId
    //This name is a combination of the tournamentName and the number of the battle in that tournament.
    //Ex. JavaTournament-Calculator
    private String name;
    private String creator;
    private CodeKata codeKata;
    private ArrayList<Team> teams;
    private Date registrationDeadline;
    private Date submitDate;
    private Date endDate;
    private String repositoryLink;
    private int maxPlayers;
    private int minPlayers;

    public Battle(String name, String creator, CodeKata codeKata, ArrayList<Team> teams, Date registrationDeadline, Date submitDate, int maxPlayers, int minPlayers) {
        this.name = name;
        this.creator = creator;
        this.codeKata = codeKata;
        this.teams = teams;
        this.registrationDeadline = registrationDeadline;
        this.submitDate = submitDate;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
    }

}
