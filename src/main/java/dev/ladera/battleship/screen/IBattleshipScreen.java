package dev.ladera.battleship.screen;

public interface IBattleshipScreen {
    void run();

    ScreenType startUp();

    ScreenType signIn();

    ScreenType createAccount();

    ScreenType mainMenu();

    ScreenType newGameInit();

    ScreenType newGameCpu();

    ScreenType play();

    ScreenType gameSelection();

    void quit();
}
