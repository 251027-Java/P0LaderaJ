package dev.ladera.battleship.model;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Player{" + "id="
                + id + ", username='"
                + username + '\'' + ", passphrase='"
                + passphrase + '\'' + ", originPlayerId="
                + originPlayerId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id)
                && Objects.equals(username, player.username)
                && Objects.equals(passphrase, player.passphrase)
                && Objects.equals(originPlayerId, player.originPlayerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, passphrase, originPlayerId);
    }
}
