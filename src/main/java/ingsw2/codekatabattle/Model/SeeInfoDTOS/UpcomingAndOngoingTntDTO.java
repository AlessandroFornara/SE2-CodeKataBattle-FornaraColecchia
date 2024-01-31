package ingsw2.codekatabattle.Model.SeeInfoDTOS;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class UpcomingAndOngoingTntDTO {

    private String _id;
    private String admin;
    private Date registrationDeadline;

}
