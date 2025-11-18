package dev.ladera.battleship.config;

public enum StringConstants {
    SIGN_IN("Sign in"),
    CREATE_ACCOUNT("Create account"),
    QUIT("Quit"),
    ;

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
