package ingsw2.codekatabattle.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

@AllArgsConstructor
@Getter
public class Team {

    private String name;
    private HashMap<String, Integer> members;
    private String keyword;

}
