package com.checkers.hibernate.util;

import javafx.beans.binding.IntegerBinding;
import org.springframework.cglib.core.GeneratorStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name= "game")
public class GameEntity {

    @Id
    @Column(name = "game_id")
    private int id;

    @Column(name = "whiteUser")
    private int whiteUser_id;

    @Column(name = "blackUser")
    private int blackUser_id;

    @Column(name = "boardState")
    private String boardState;

    @Column(name = "currentPlayerId")
    private int currentPlayerId;

    @Transient
    private String stringToParse;

    public GameEntity() {
    }

    public  GameEntity(String stringToParse){
        this.setStringToParse(stringToParse);
    }

    private void setStringToParse(String stringToParse) {
    }

    public GameEntity(int id, int whiteUser_id, int blackUser_id) {
        this.id = id;
        this.whiteUser_id = whiteUser_id;
        this.blackUser_id = blackUser_id;
    }

    public String getStringToParse() {
        return stringToParse;
    }

}
