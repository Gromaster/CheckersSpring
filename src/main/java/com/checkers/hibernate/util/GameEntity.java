package com.checkers.hibernate.util;

import javafx.beans.binding.IntegerBinding;
import org.springframework.cglib.core.GeneratorStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    @Transient
    private String stringToParse;

    public GameEntity() {
    }

    public  GameEntity(String stringToParse){
        this.setStringToParse(stringToParse);

    }

    public GameEntity(int id, int whiteUser_id, int blackUser_id) {
        this.id = id;
        this.whiteUser_id = whiteUser_id;
        this.blackUser_id = blackUser_id;
    }

    public String getStringToParse() {
        return stringToParse;
    }

    public void setStringToParse(String stringToParse) {
        ArrayList<Integer> parsed = Stream.of(stringToParse.split("/")).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
        this.id = parsed.get(0);
        this.whiteUser_id = parsed.get(1);
        this.blackUser_id = parsed.get(2);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWhiteUser_id() {
        return whiteUser_id;
    }

    public void setWhiteUser_id(int whiteUser_id) {
        this.whiteUser_id = whiteUser_id;
    }

    public int getBlackUser_id() {
        return blackUser_id;
    }

    public void setBlackUser_id(int blackUser_id) {
        this.blackUser_id = blackUser_id;
    }

    @Override
    public String toString() {
        return "GameEntity{" +
                "id=" + id +
                ", whiteUser_id=" + whiteUser_id +
                ", blackUser_id=" + blackUser_id +
                '}';
    }
}
