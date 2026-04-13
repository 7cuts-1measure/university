package ru.nsu.ccfit.gerasimov2.a.game.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResult {
    public final String name;
    public final int score;
  
    @JsonCreator
    public UserResult(
        @JsonProperty("name") String name,
        @JsonProperty("score") int score
) {
        this.name = name;
        this.score = score;
    }
    
}
