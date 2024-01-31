package ingsw2.codekatabattle.Model.SeeInfoDTOS;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class MyBattlesDTO {

    private String _id;
    private Date registrationDeadline;
    private Date submitDate;
}
