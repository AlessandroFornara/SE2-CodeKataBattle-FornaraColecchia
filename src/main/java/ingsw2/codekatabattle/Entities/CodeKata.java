package ingsw2.codekatabattle.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
public class CodeKata {

    private ArrayList<String> input;
    private ArrayList<String> output;
    private String description;

}
