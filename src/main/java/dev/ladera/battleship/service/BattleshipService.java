package dev.ladera.battleship.service;

public class BattleshipService implements IBattleshipService {
    private final IGameService gameService;

    public BattleshipService(IGameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void run() {}
}
