package ru.job4j.toDo.store;

import ru.job4j.toDo.model.Item;
import java.util.List;

public interface Store {

    List<Item> allItems();
    Item addItem(String desc);
    Item updateItem(Item item);
    Item getById(int id);
}
