package dev.ladera.battleship.ui;

public interface IBattleshipUI {
    Screen screenStartup();

    Screen screenSignIn();

    Screen screenCreateAccount();

    Screen screenMainMenu();

    Screen screenNewGameInit();

    Screen screenNewGameCpu();
}
