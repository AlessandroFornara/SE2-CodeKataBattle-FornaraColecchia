package ingsw2.codekatabattle.Model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
public class AutomatedEvaluationDTO {

    @NotNull(message = "keyword must not be null")
    @NotBlank(message = "keyword must not be blank")
    private String keyword;

    @NotNull(message = "battle must not be null")
    @NotBlank(message = "battle must not be blank")
    private String battle;
    @NotNull(message = "outputs must not be null")
    @NotEmpty(message = "outputs must not be empty")
    private ArrayList<String> outputs;
}
