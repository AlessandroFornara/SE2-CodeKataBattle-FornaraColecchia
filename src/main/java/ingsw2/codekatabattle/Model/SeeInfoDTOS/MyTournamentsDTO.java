package ingsw2.codekatabattle.Model.SeeInfoDTOS;

import ingsw2.codekatabattle.Entities.States.TournamentVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class MyTournamentsDTO {

    private String _id;
    private TournamentVisibility visibility;
    private Date endDate;

}
