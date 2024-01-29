package ingsw2.codekatabattle.Model.TeamDTOS;

import ingsw2.codekatabattle.Utils.NoSpecialCharacters;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TeamCreationDTO {

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @NoSpecialCharacters
    private String name;

    @NotNull(message = "BattleName must not be null")
    @NotBlank(message = "BattleName must not be blank")
    private String battleName;


}
