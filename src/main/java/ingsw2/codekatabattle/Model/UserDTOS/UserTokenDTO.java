package ingsw2.codekatabattle.Model.UserDTOS;

import ingsw2.codekatabattle.Entities.States.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserTokenDTO {

    private String username;
    private UserRole role;
    private String token;

}
