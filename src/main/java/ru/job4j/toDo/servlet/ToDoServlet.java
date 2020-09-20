package ru.job4j.toDo.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.job4j.toDo.model.Item;
import ru.job4j.toDo.store.Store;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ToDoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Store store = (Store) getServletContext().getAttribute("store");
        Item item = new Item();
        try {
            BufferedReader reader = request.getReader();
            String line;
            JSONObject jo = null;
            while ((line = reader.readLine()) != null) {
                jo = new JSONObject(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            if (jo != null && !jo.isEmpty()) {
                item = mapper.readValue(jo.toString(), new TypeReference<Item>(){});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            if (item.getId() != 0) {
                store.updateItem(item);
            } else {
                store.addItem(item.getDescription());
            }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Store store = (Store) getServletContext().getAttribute("store");
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        JSONArray items = new JSONArray(store.allItems());
        try {
            PrintWriter pw = response.getWriter();
            pw.write(items.toString());
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
