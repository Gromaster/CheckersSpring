package com.checkers.hibernate.util;

import com.checkers.models.Game;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ReaderDB {
    private SessionFactory sessionFactory=new Configuration().configure().buildSessionFactory();


    public ReaderDB() {
    }

    public Game load(int gameId){
        Session session = sessionFactory.openSession();
        Game game= session.get(Game.class,gameId);
        return game;
    }

}
