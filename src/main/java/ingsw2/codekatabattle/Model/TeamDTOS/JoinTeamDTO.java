package ingsw2.codekatabattle.Model.TeamDTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinTeamDTO {

    @NotNull(message = "keyword must not be null")
    @NotBlank(message = "keyword must not be blank")
    private String keyword;

    @NotNull(message = "BattleName must not be null")
    @NotBlank(message = "BattleName must not be blank")
    private String battleName;
}
