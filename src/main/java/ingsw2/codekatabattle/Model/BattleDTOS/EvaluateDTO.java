package ingsw2.codekatabattle.Model.BattleDTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EvaluateDTO {

    @NotNull(message = "battleName must not be null")
    @NotBlank(message = "battleName must not be blank")
    private String battleName;

    @NotNull(message = "usernames must not be null")
    private String[] usernames;

    @NotNull(message = "points must not be null")
    private int[] points;
}
