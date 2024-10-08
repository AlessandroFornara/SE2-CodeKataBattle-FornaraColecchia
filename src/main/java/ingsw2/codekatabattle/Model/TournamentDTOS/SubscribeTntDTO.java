package ingsw2.codekatabattle.Model.TournamentDTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SubscribeTntDTO {

    @NotNull(message = "nameOrKeyword must not be null")
    @NotBlank(message = "nameOrKeyword must not be blank")
    private String nameOrKeyword;

    private boolean tournamentPublic;
}
