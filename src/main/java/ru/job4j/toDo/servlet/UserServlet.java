package ru.job4j.toDo.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.job4j.toDo.model.User;
import ru.job4j.toDo.store.Store;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Map;

public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Store store = (Store) getServletContext().getAttribute("store");
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        if ("user".equals(request.getParameter("get"))) {
            try {
                PrintWriter pw = response.getWriter();
                User user = (User) request.getSession().getAttribute("user");
                if (user == null) {
                    user = new User();
                }
                pw.write(new JSONObject(user).toString());
                pw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JSONArray users = new JSONArray(store.allUsers());
            try {
                PrintWriter pw = response.getWriter();
                pw.write(users.toString());
                pw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Store store = (Store) getServletContext().getAttribute("store");
        User user = null;
        try {
            BufferedReader reader = request.getReader();
            String line;
            JSONObject jo = null;
            while ((line = reader.readLine()) != null) {
                jo = new JSONObject(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            if (jo != null && !jo.isEmpty()) {
                user = mapper.readValue(jo.toString(), new TypeReference<>(){});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user.getId() == -500) {
            user = store.addUser(user);
        } else if(user.getId() == 0) {
            user = store.getUser(user);
        }
        if (user.getId() > 0) {
            request.getSession().setAttribute("user", user);
        }
        try {
            PrintWriter pw = response.getWriter();
            pw.write(new JSONObject(user).toString());
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
