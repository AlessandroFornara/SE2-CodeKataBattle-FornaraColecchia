package ingsw2.codekatabattle.Model.UserDTOS;

import ingsw2.codekatabattle.Entities.States.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegisterDTO {

    @NotNull(message = "Email must not be null")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Surname must not be null")
    @NotBlank(message = "Surname must not be blank")
    private String surname;

    @NotNull(message = "Username must not be null")
    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotNull(message = "Password must not be null")
    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotNull(message = "Role must not be null")
    private UserRole role;

}
