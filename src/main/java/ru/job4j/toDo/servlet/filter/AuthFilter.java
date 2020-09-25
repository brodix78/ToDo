package ru.job4j.toDo.servlet.filter;

import ru.job4j.toDo.model.User;
import ru.job4j.toDo.store.Store;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if(req.getMethod().equalsIgnoreCase("POST")) {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                try {
                    req.getRequestDispatcher("auth.html").forward(req, resp);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
