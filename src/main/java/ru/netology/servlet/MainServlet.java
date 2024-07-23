package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;
    static private final String GET_METHOD = "GET";
    static private final String POST_METHOD = "POST";
    static private final String DELETE_METHOD = "DELETE";
    private final String API_PORTS = "/api/posts";
    private final String API_PORTS_D = "/api/posts/\\d+";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if (method.equals(GET_METHOD) && path.equals(API_PORTS)) {
                controller.all(resp);
                return;
            }
            if (method.equals(GET_METHOD) && path.matches(API_PORTS_D)) {
                // easy way
                controller.getById(receivedId(path), resp);
                return;
            }
            if (method.equals(POST_METHOD) && path.equals(API_PORTS)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals(DELETE_METHOD) && path.matches(API_PORTS_D)) {
                // easy way
                controller.removeById(receivedId(path), resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public long receivedId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}