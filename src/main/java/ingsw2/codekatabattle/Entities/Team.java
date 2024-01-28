package ingsw2.codekatabattle.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
public class Team {

    private String name;
    private ArrayList<String> members;
    private ArrayList<Integer> scores;
    //private HashMap<String, Integer> members;
    private String keyword;

}
