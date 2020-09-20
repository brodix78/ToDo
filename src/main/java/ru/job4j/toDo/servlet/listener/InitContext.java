package ru.job4j.toDo.servlet.listener;

import ru.job4j.toDo.store.HbStore;
import ru.job4j.toDo.store.Store;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitContext implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Store store = new HbStore();
        sce.getServletContext().setAttribute("store", store);
    }
}
