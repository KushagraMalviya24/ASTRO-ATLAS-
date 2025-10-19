package com.astroatlas;

public class User {
    private final long id;
    private final String name;
    private final String email;
    private final String passwordHash;

    public User(long id, String name, String email, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
}


