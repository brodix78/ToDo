package ru.job4j.toDo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.toDo.model.Item;

import java.util.ArrayList;
import java.util.List;

public class HbStore implements Store, AutoCloseable{

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @Override
    public List<Item> allItems() {
        Session session = sf.openSession();
        List allItems = new ArrayList<>();
        try {
            session.beginTransaction();
            allItems = session.createQuery("FROM ru.job4j.toDo.model.Item ORDER BY id").list();
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return allItems;
    }

    @Override
    public Item addItem(String desc) {
        Session session = sf.openSession();
        Item item = new Item();
        try {
            session.beginTransaction();
            item.setDescription(desc);
            item.setDate(System.currentTimeMillis());
            session.save(item);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Session session = sf.openSession();
        try {
            session.beginTransaction();
            session.update(item);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return item;
    }

    @Override
    public Item getById(int id) {
        Session session = sf.openSession();
        Item item = new Item();
        try {
            session.beginTransaction();
            item = session.get(Item.class, id);
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return item;
    }
}