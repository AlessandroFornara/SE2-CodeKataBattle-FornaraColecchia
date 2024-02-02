package ingsw2.codekatabattle.Entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

/**
 * This class is the representation of a CodeKata.
 */
@AllArgsConstructor
@Getter
public class CodeKata {

    @NotNull(message = "input must not be null")
    @NotEmpty(message = "input must not be empty")
    private ArrayList<String> input;
    @NotNull(message = "output must not be null")
    @NotEmpty(message = "output must not be empty")
    private ArrayList<String> output;
    @NotNull(message = "description must not be null")
    @NotBlank(message = "description must not be blank")
    private String description;
    @NotNull(message = "configurationFile must not be null")
    @NotBlank(message = "configurationFile must not be blank")
    private String configurationFile;

}
