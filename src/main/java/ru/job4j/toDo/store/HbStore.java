package ru.job4j.toDo.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.toDo.model.Item;
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
        return this.tx(session -> session.createQuery(
                "FROM ru.job4j.toDo.model.Item ORDER BY id").list());
    }

    @Override
    public Item addItem(String desc) {
        return this.tx(session -> {
            Item item = new Item();
            item.setDescription(desc);
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
    public Item getById(int id) {
        return this.tx(session -> session.get(Item.class, id));
    }
}