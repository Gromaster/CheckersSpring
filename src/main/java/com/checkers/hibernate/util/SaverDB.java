package com.checkers.hibernate.util;

import com.checkers.models.Game;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class SaverDB {
    private SessionFactory sessionFactory;
    private Session session;

    public SaverDB() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public void save(Game game) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(game);
        session.getTransaction().commit();
    }

}
