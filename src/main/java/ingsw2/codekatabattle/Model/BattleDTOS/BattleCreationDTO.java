package ingsw2.codekatabattle.Model.BattleDTOS;

import com.fasterxml.jackson.annotation.JsonFormat;
import ingsw2.codekatabattle.Entities.CodeKata;
import ingsw2.codekatabattle.Utils.NoSpecialCharacters;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class BattleCreationDTO {

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @NoSpecialCharacters
    private String name;

    @NotNull(message = "TournamentName must not be null")
    @NotBlank(message = "TournamentName must not be blank")
    private String tournamentName;

    @NotNull(message = "RegistrationDeadline must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date registrationDeadline;

    @NotNull(message = "SubmissionDeadline must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date submissionDeadline;

    @NotNull(message = "CodeKata must not be null")
    @Valid private CodeKata codeKata;

    @Positive(message = "MaxPlayers must be a positive integer number")
    private int maxPlayers;

    @Positive(message = "MinPlayers must be a positive integer number")
    private int minPlayers;
}
