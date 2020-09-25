package ru.job4j.toDo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.toDo.model.Item;
import ru.job4j.toDo.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HbStore implements Store, AutoCloseable{

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @Override
    public List<Item> allItems() {
        return this.tx(session -> session.createQuery("FROM ru.job4j.toDo.model.Item ORDER BY id").list());
    }

    @Override
    public Item addItem(Item in) {
        return this.tx(session -> {
            Item item = new Item();
            item.setDescription(in.getDescription());
            item.setUser(in.getUser());
            item.setDate(System.currentTimeMillis());
            session.save(item);
            return item;
        });
    }

    @Override
    public Item updateItem(Item item) {
        return this.tx(session -> {
            session.update(item);
            return item;
        });
    }

    @Override
    public Item getItemById(int id) {
        return this.tx(session -> session.get(Item.class, id));
    }

    @Override
    public List<User> allUsers() {
        return this.tx(session -> {
            ArrayList<User> users = new ArrayList<>();
            List original = session.createQuery("SELECT id, name FROM ru.job4j.toDo.model.User").getResultList();
            for (Object user : original) {
                Object[] temp = (Object[]) user;
                Integer id = (Integer) temp[0];
                String name = (String) temp[1];
                users.add(new User(id, name));
            }
            return users;
        });
    }

    @Override
    public User addUser(User user) {
        return this.tx(session -> {
            session.save(user);
            return user;
        });
    }

    @Override
    public User getUser(User user) {
        return this.tx(session -> {
            List idList = session.createQuery("SELECT id FROM ru.job4j.toDo.model.User WHERE name = ?1 AND password = ?2").
                    setParameter(1, user.getName()).
                    setParameter(2, user.getPassword()).list();
            if (idList.size() == 1) {
                for (Object id : idList) {
                    user.setId((int) id);
                }
            } else {
                user.setId(0);
            }
            user.setPassword("");
            return user;
        });
    }
}