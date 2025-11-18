package dev.ladera.battleship.service;

public interface IBattleshipService {
    void run();

    void toStartup();

    void toSignIn();

    void toAccountCreation();

    void toMainMenu();

    void toNewGameInit();

    void toNewGameCpu();
}
