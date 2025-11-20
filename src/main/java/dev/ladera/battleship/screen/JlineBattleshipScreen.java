package dev.ladera.battleship.screen;

import dev.ladera.battleship.config.StringConstants;
import dev.ladera.battleship.dto.GameDto;
import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.exception.InvalidPassphraseException;
import dev.ladera.battleship.exception.InvalidUsernameException;
import dev.ladera.battleship.exception.UsernameExistsException;
import dev.ladera.battleship.model.Game;
import dev.ladera.battleship.model.Player;
import dev.ladera.battleship.model.Ship;
import dev.ladera.battleship.service.IGameService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JlineBattleshipScreen implements IBattleshipScreen {
    private static final Logger LOGGER = LoggerFactory.getLogger(JlineBattleshipScreen.class);

    private IGameService gameService;
    private Terminal terminal;
    private ConsolePrompt prompt;
    private ScreenType currentScreen;

    private Player player;
    private Game currentGame;

    public JlineBattleshipScreen(IGameService gameService) throws IOException {
        this.gameService = gameService;
        terminal = TerminalBuilder.builder().build();
        prompt = new ConsolePrompt(terminal);
        currentScreen = ScreenType.STARTUP;
    }

    private ScreenType processScreen(ScreenType screenType) {
        switch (screenType) {
            case ScreenType.STARTUP -> {
                return startUp();
            }
            case ScreenType.SIGN_IN -> {
                return signIn();
            }
            case ScreenType.CREATE_ACCOUNT -> {
                return createAccount();
            }
            case ScreenType.MAIN_MENU -> {
                return mainMenu();
            }
            case ScreenType.PLAY -> {
                return play();
            }
            case ScreenType.GAMEPLAY -> {
                return gamePlay();
            }
            // case ScreenType.GAME_SELECTION -> {
            //     return gameSelection();
            // }
            case ScreenType.NEW_GAME_INIT -> {
                return newGameInit();
            }
            default -> {
                return null;
            }
        }
    }

    private void clearScreen(boolean clearScrollBackBuffer) {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();

        if (clearScrollBackBuffer) {
            terminal.writer().print("\033[3J");
            terminal.flush();
        }
    }

    @Override
    public void run() {
        while (currentScreen != null) {
            currentScreen = processScreen(currentScreen);
        }

        quit();

        try {
            terminal.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ScreenType startUp() {
        clearScreen(false);

        terminal.writer().print(StringConstants.BATTLESHIP.value);

        var builder = prompt.getPromptBuilder()
                .createListPrompt()
                .name("action")
                .message("Actions")
                .newItem(StringConstants.SIGN_IN.value)
                .add()
                .newItem(StringConstants.CREATE_ACCOUNT.value)
                .add()
                .newItem(StringConstants.QUIT.value)
                .add()
                .addPrompt();

        try {
            var res = prompt.prompt(builder.build());
            resetPrompt();

            String action = res.get("action").getResult();

            switch (StringConstants.fromValue(action)) {
                case StringConstants.SIGN_IN -> {
                    return ScreenType.SIGN_IN;
                }
                case StringConstants.CREATE_ACCOUNT -> {
                    return ScreenType.CREATE_ACCOUNT;
                }
                case StringConstants.QUIT -> {
                    return ScreenType.QUIT;
                }
                case null, default -> {}
            }

        } catch (IOException e) {
            LOGGER.error("start up", e);
        }

        return null;
    }

    @Override
    public ScreenType signIn() {
        clearScreen(false);

        var builder = prompt.getPromptBuilder();
        builder.createInputPrompt()
                .name("username")
                .message("Username")
                .defaultValue("")
                .addPrompt();
        builder.createInputPrompt()
                .name("passphrase")
                .message("Passphrase")
                .mask('*')
                .defaultValue("")
                .addPrompt();

        while (true) {
            try {
                placeCursor(1, 0);
                var res = prompt.prompt(builder.build());
                resetPrompt();

                String username = res.get("username").getResult();
                String passphrase = res.get("passphrase").getResult();

                player = gameService.findPlayerByUsername(username);

                if (player == null || !Objects.equals(passphrase, player.getPassphrase())) {
                    throw new RuntimeException("Invalid username or passphrase");
                }

                return ScreenType.MAIN_MENU;
            } catch (IOException | SQLException e) {
                LOGGER.error("sign in", e);
                return null;
            } catch (RuntimeException e) {
                clearLines(0, 2);
                displayError(0, 0, e.getMessage());
            }
        }
    }

    private void resetPrompt() throws IOException {
        prompt.prompt(List.of());
    }

    private void clearLine(int row) {
        var cursor = terminal.getCursorPosition(null);

        placeCursor(row, 0);
        terminal.puts(InfoCmp.Capability.clr_eol);

        placeCursor(cursor);
    }

    private void clearLines(int start, int end) {
        for (int i = start; i <= end; i++) {
            clearLine(i);
        }
    }

    private void placeCursor(Cursor cursor) {
        placeCursor(cursor.getY(), cursor.getX());
    }

    private void placeCursor(int row, int col) {
        terminal.puts(InfoCmp.Capability.cursor_address, row, col);
    }

    private void logCursor(String message) {
        LOGGER.info("{}: {}", message, terminal.getCursorPosition(null));
    }

    private void placeNextPromptAt(Cursor cursor) throws IOException {
        placeNextPromptAt(cursor.getY(), cursor.getX());
    }

    private void placeNextPromptAt(int row, int col) throws IOException {
        placeCursor(row, col);
        terminal.puts(InfoCmp.Capability.newline);
        resetPrompt();
    }

    private void remakePromptAt(Cursor cursor) throws IOException {
        remakePromptAt(cursor.getY(), cursor.getX());
    }

    private void remakePromptAt(int row, int col) throws IOException {
        resetPrompt();
        placeCursor(row, col);
        terminal.puts(InfoCmp.Capability.clr_eol);
    }

    private void displayError(int row, int col, String message) {
        clearLine(row);
        placeCursor(row, col);

        var str = new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.background(AttributedStyle.RED))
                .append(" ERROR ")
                .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
                .append(" ")
                .append(message);

        str.print(terminal);
    }

    private String promptUsernameCreation() throws IOException, SQLException {
        boolean done = false;
        String username = null;

        var cursor = terminal.getCursorPosition(null);
        var builder = prompt.getPromptBuilder()
                .createInputPrompt()
                .name("username")
                .message("Username")
                .defaultValue("")
                .addPrompt();

        while (!done) {
            var res = prompt.prompt(builder.build());
            username = res.get("username").getResult();

            done = gameService.findPlayerByUsername(username) == null;

            if (!done) {
                resetPrompt();
                displayError(0, 0, "Username already exists");
                placeCursor(cursor);
            }
        }

        clearLine(0);
        placeNextPromptAt(cursor.getY() + 1, cursor.getX());

        return username;
    }

    private String promptPassphraseCreation() throws IOException {
        boolean done = false;
        String p1 = null;

        var cursor = terminal.getCursorPosition(null);

        while (!done) {
            var builder = prompt.getPromptBuilder()
                    .createInputPrompt()
                    .name("passphrase")
                    .message("Passphrase:")
                    .mask('*')
                    .defaultValue("")
                    .addPrompt()
                    .createInputPrompt()
                    .name("re-passphrase")
                    .message("Re-enter your passphrase:")
                    .mask('*')
                    .defaultValue("")
                    .addPrompt();

            var res = prompt.prompt(builder.build());

            p1 = res.get("passphrase").getResult();
            String p2 = res.get("re-passphrase").getResult();

            done = p1.equals(p2);

            if (!done) {
                resetPrompt();
                displayError(0, 0, "Passphrases did not match");
                placeCursor(cursor);
            }
        }

        clearLine(0);
        placeNextPromptAt(cursor.getY() + 2, cursor.getX());

        return p1;
    }

    @Override
    public ScreenType createAccount() {
        clearScreen(false);

        while (true) {
            try {
                placeCursor(1, 0);
                String username = promptUsernameCreation();
                String passphrase = promptPassphraseCreation();

                this.player = gameService.createPlayer(new PlayerDto(username, passphrase, null));

                return ScreenType.MAIN_MENU;
            } catch (IOException | SQLException e) {
                LOGGER.error("create account", e);
                return null;
            } catch (InvalidUsernameException | UsernameExistsException | InvalidPassphraseException e) {
                clearLines(0, 2);
                displayError(0, 0, e.getMessage());
            }
        }
    }

    private void displaySignedInContent() {
        placeCursor(1, 0);
        var str = new AttributedStringBuilder()
                .style(AttributedStyle.DEFAULT.background(AttributedStyle.BLUE))
                .append(" ")
                .append(player.getUsername())
                .append(" ");
        str.println(terminal);
    }

    @Override
    public ScreenType mainMenu() {
        clearScreen(false);
        displaySignedInContent();

        var builder = prompt.getPromptBuilder()
                .createListPrompt()
                .name("action")
                .message("Main Menu")
                .newItem(StringConstants.PLAY.value)
                .add()
                .newItem(StringConstants.SIGN_OUT.value)
                .add()
                .newItem(StringConstants.QUIT.value)
                .add()
                .addPrompt();

        var cursor = terminal.getCursorPosition(null);
        placeCursor(cursor.getY() + 1, cursor.getX());

        try {
            var res = prompt.prompt(builder.build());
            resetPrompt();

            String action = res.get("action").getResult();

            switch (StringConstants.fromValue(action)) {
                case StringConstants.PLAY -> {
                    return ScreenType.PLAY;
                }
                case StringConstants.SIGN_OUT -> {
                    player = null;
                    return ScreenType.STARTUP;
                }
                case StringConstants.QUIT -> {
                    return ScreenType.QUIT;
                }
                case null, default -> {}
            }

        } catch (IOException e) {
            LOGGER.error("main menu", e);
        }

        return null;
    }

    private int translateRow(String cell) {
        char c = Character.toLowerCase(cell.charAt(0));
        return c - 'a';
    }

    private int translateCol(String cell) {
        return Integer.parseInt(cell.substring(1));
    }

    private Ship promptShipPlacement(int length) throws IOException {
        var builder = prompt.getPromptBuilder();
        builder.createInputPrompt()
                .name("location")
                .message("Choose a location for the ship")
                .defaultValue("")
                .addPrompt();
        builder.createInputPrompt()
                .name("direction")
                .message("Choose the direction of the ship")
                .defaultValue("")
                .addPrompt();

        while (true) {
            var res = prompt.prompt(builder.build());

            String location = res.get("location").getResult();
            String direction = res.get("direction").getResult();

            // gameService.createShip(new ShipDto());
            return null;
        }

        // return null;
    }

    @Override
    public ScreenType newGameInit() {
        clearScreen(false);
        displaySignedInContent();

        try {
            currentGame = gameService.createGame(new GameDto(10, 10));

            // TODO show grid
            // ASK FOR SHIPS
            List<Integer> shipLengths = List.of(5, 4, 3, 3, 2);
            for (int length : shipLengths) {
                currentGame.addShip(promptShipPlacement(length));
            }

            return ScreenType.GAMEPLAY;
        } catch (SQLException | IOException e) {
            LOGGER.info("new game init", e);
        }

        return null;
    }

    @Override
    public ScreenType newGameCpu() {
        return null;
    }

    @Override
    public ScreenType play() {
        clearScreen(false);
        displaySignedInContent();

        var builder = prompt.getPromptBuilder()
                .createListPrompt()
                .name("action")
                .message("Options")
                .newItem(StringConstants.NEW_GAME.value)
                .add()
                .newItem(StringConstants.CONTINUE_GAME.value)
                .add()
                .newItem(StringConstants.BACK.value)
                .add()
                .addPrompt();

        var cursor = terminal.getCursorPosition(null);
        placeCursor(cursor.getY() + 1, cursor.getX());

        try {
            var res = prompt.prompt(builder.build());
            resetPrompt();

            String action = res.get("action").getResult();

            switch (StringConstants.fromValue(action)) {
                case StringConstants.NEW_GAME -> {
                    return ScreenType.NEW_GAME_INIT;
                }
                case StringConstants.CONTINUE_GAME -> {
                    return ScreenType.GAME_SELECTION;
                }
                case StringConstants.BACK -> {
                    return ScreenType.MAIN_MENU;
                }
                case null, default -> {}
            }

        } catch (IOException e) {
            LOGGER.error("play", e);
        }

        return null;
    }

    @Override
    public ScreenType gameSelection() {
        return null;
    }

    @Override
    public ScreenType gamePlay() {
        return null;
    }

    @Override
    public void quit() {
        clearScreen(false);
        terminal.writer().print(StringConstants.GOODBYE.value);
        terminal.flush();
    }
}
