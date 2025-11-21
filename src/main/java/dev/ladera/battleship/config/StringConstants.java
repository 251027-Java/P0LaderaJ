package dev.ladera.battleship.config;

/*
ascii art text
https://patorjk.com/software/taag
 */

public enum StringConstants {
    SIGN_IN("Sign in"),
    CREATE_ACCOUNT("Create account"),
    QUIT("Quit"),
    PLAY("Play"),
    SIGN_OUT("Sign out"),
    STATS("Stats"),
    HISTORY("History"),
    BACK("Back"),
    NEW_GAME("New game"),
    CONTINUE_GAME("Continue game"),
    MAIN_MENU("Main menu"),
    MAKE_MOVE("Make a move"),
    BATTLESHIP(
            """

        █████▄  ▄▄▄ ▄▄▄▄▄▄ ▄▄▄▄▄▄ ▄▄    ▄▄▄▄▄  ▄▄▄▄ ▄▄ ▄▄ ▄▄ ▄▄▄▄
        ██▄▄██ ██▀██  ██     ██   ██    ██▄▄  ███▄▄ ██▄██ ██ ██▄█▀
        ██▄▄█▀ ██▀██  ██     ██   ██▄▄▄ ██▄▄▄ ▄▄██▀ ██ ██ ██ ██

        """),
    GOODBYE(
            """
                                                           ▄▄
         ▄████  ▄████▄ ▄████▄ ████▄  █████▄ ██  ██ ██████  ██
        ██  ▄▄▄ ██  ██ ██  ██ ██  ██ ██▄▄██  ▀██▀  ██▄▄    ██
         ▀███▀  ▀████▀ ▀████▀ ████▀  ██▄▄█▀   ██   ██▄▄▄▄  ▄▄

        """);

    public final String value;

    StringConstants(String value) {
        this.value = value;
    }

    public static StringConstants fromValue(String value) {
        for (var e : values()) {
            if (e.value.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }
}
