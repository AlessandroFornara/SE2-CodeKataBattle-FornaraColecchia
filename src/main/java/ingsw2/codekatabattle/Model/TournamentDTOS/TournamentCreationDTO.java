package ingsw2.codekatabattle.Model.TournamentDTOS;

import com.fasterxml.jackson.annotation.JsonFormat;
import ingsw2.codekatabattle.Utils.NoSpecialCharacters;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class TournamentCreationDTO {

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @NoSpecialCharacters
    private String name;

    @NotNull(message = "RegistrationDeadline must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date registrationDeadline;

    private boolean publicTournament;

}
