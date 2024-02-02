package ingsw2.codekatabattle.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

/**
 * This class is the representation of a Team.
 */
@AllArgsConstructor
@Getter
public class Team {

    private String name;
    private ArrayList<String> members;
    private ArrayList<Integer> scores;
    //private HashMap<String, Integer> members;
    private String keyword;
    private int points;
}
