package com.checkers.hibernate.util;

import javax.persistence.*;

@Entity
@Table(name= "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name= "whiteUser_id")
    private int whiteUser_id;

    @Column(name= "blackUser_id")
    private int blackUser_id;

    public Game() {
    }

    public Game(int id, int whiteUser_id, int blackUser_id) {
        this.id = id;
        this.whiteUser_id = whiteUser_id;
        this.blackUser_id = blackUser_id;
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
}
