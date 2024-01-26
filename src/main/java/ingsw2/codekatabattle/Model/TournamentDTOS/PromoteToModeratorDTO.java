package ingsw2.codekatabattle.Model.TournamentDTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PromoteToModeratorDTO {

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Moderator must not be null")
    @NotBlank(message = "Moderator must not be blank")
    private String moderator;
}
