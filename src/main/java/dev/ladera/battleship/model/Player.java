package dev.ladera.battleship.model;

public class Player {
    private long id;
    private String username;

    public Player(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
}
