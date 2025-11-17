package dev.ladera.battleship.model;

public class Player {
    private Long id;
    private String username;
    private String passphrase;
    private Long originPlayerId;

    public Player(String username, String passphrase, Long originPlayerId) {
        this(null, username, passphrase, originPlayerId);
    }

    public Player(Long id, String username, String passphrase, Long originPlayerId) {
        this.id = id;
        this.username = username;
        this.passphrase = passphrase;
        this.originPlayerId = originPlayerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public Long getOriginPlayerId() {
        return originPlayerId;
    }
}
