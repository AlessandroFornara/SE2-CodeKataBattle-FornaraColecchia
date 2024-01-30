package ingsw2.codekatabattle.Model.BattleDTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CloseConsolidationStageDTO {

    @NotNull(message = "battleName must not be null")
    @NotBlank(message = "battleName must not be blank")
    private String battleName;

    private boolean t;

}
