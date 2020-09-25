package ru.job4j.toDo.store;

import ru.job4j.toDo.model.Item;
import ru.job4j.toDo.model.User;

import java.util.List;

public interface Store {

    List<Item> allItems();
    Item addItem(Item in);
    Item updateItem(Item item);
    Item getItemById(int id);
    List<User> allUsers();
    User addUser(User user);
    User getUser(User user);

}
