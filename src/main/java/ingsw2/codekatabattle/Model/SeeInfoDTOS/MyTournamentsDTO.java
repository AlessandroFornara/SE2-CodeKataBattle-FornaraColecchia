package ingsw2.codekatabattle.Model.SeeInfoDTOS;

import ingsw2.codekatabattle.Entities.States.TournamentVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MyTournamentsDTO {

    private String _id;
    private TournamentVisibility visibility;

}
