package com.astroatlas;

import static spark.Spark.*;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class App {
    private static final Gson GSON = new Gson();

    public static void main(String[] args) {
        port(8080);

        // CORS
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type");
        });
        options("*", (req, res) -> {
            res.status(204);
            return "";
        });

        Db.initialize();

        get("/health", (req, res) -> {
            res.type("text/plain");
            return "OK";
        });

        post("/api/register", (req, res) -> {
            res.type("application/json");
            Map<String, Object> body = GSON.fromJson(req.body(), Map.class);
            String name = (String) body.getOrDefault("name", "");
            String email = (String) body.getOrDefault("email", "");
            String password = (String) body.getOrDefault("password", "");

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                res.status(400);
                return GSON.toJson(error("All fields are required"));
            }

            UserDao dao = new UserDao();
            if (dao.existsByEmail(email)) {
                res.status(400);
                return GSON.toJson(error("User already exists"));
            }

            String hashed = PasswordUtil.hashPassword(password);
            dao.createUser(name, email, hashed);
            return GSON.toJson(success("User registered"));
        });

        post("/api/login", (req, res) -> {
            res.type("application/json");
            Map<String, Object> body = GSON.fromJson(req.body(), Map.class);
            String email = (String) body.getOrDefault("email", "");
            String password = (String) body.getOrDefault("password", "");

            UserDao dao = new UserDao();
            User user = dao.findByEmail(email);
            if (user == null) {
                res.status(400);
                return GSON.toJson(error("Invalid email"));
            }
            boolean valid = PasswordUtil.checkPassword(password, user.getPasswordHash());
            if (!valid) {
                res.status(400);
                return GSON.toJson(error("Incorrect password"));
            }
            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("message", "Login successful");
            data.put("name", user.getName());
            return GSON.toJson(data);
        });
    }

    private static Map<String, Object> error(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", false);
        m.put("error", msg);
        return m;
    }

    private static Map<String, Object> success(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("success", true);
        m.put("message", msg);
        return m;
    }
}


