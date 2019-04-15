package com.checkers.hibernate.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ReaderDB {
    private SessionFactory sessionFactory=new Configuration().configure().buildSessionFactory();
    private Session session;


    public ReaderDB() {
    }

    public GameEntity load(int gameId){
        session=sessionFactory.openSession();
        GameEntity gameEntity=session.get(GameEntity.class,gameId);
        return gameEntity;
    }

}
