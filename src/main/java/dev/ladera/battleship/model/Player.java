package dev.ladera.battleship.model;

public class Player {
    private long id;
    private String username;
    private String passphrase;
    private Long originPlayerId;

    public Player(String username, String passphrase, Long originPlayerId) {
        this(-1, username, passphrase, originPlayerId);
    }

    public Player(long id, String username, String passphrase, Long originPlayerId) {
        this.id = id;
        this.username = username;
        this.passphrase = passphrase;
        this.originPlayerId = originPlayerId;
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
