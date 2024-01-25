package ingsw2.codekatabattle.Model.UserDTOS;

import ingsw2.codekatabattle.Entities.States.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDTO {

    private String email;
    private String name;
    private String surname;
    private String username;
    private UserRole role;

}
