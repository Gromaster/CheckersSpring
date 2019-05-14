package com.checkers.hibernate.util;

import com.checkers.models.Game;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class SaverDB {
    SessionFactory sessionFactory=new Configuration().configure().buildSessionFactory();
    Session session;

    public SaverDB() {
    }
    public void save(Game game){
        session=sessionFactory.openSession();
        session.beginTransaction();
        session.save(game);
        session.getTransaction().commit();
    }

}
