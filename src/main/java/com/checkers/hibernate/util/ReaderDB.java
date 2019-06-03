package com.checkers.hibernate.util;

import com.checkers.model.Game;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ReaderDB {
    private SessionFactory sessionFactory;


    public ReaderDB() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public Game load(int gameId) {
        Session session = sessionFactory.openSession();
        return session.get(Game.class, gameId);
    }

}
