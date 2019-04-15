package com.checkers.hibernate.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class SaverDB {
    SessionFactory sessionFactory=new Configuration().configure().buildSessionFactory();
    Session session;

    public SaverDB() {
    }
    public void save(GameEntity gameEntity){
        session=sessionFactory.openSession();
        session.beginTransaction();
        session.save(gameEntity);
        session.getTransaction().commit();
    }

}
