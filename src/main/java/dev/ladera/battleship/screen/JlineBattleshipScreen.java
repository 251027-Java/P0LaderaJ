package dev.ladera.battleship.screen;

import dev.ladera.battleship.config.StringConstants;
import dev.ladera.battleship.dto.GameDto;
import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.dto.ShipDto;
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
import java.util.function.Function;
import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
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
                placeCursor(3, 0);
                var res = prompt.prompt(builder.build());
                resetPrompt();

                String username = res.get("username").getResult().trim();
                String passphrase = res.get("passphrase").getResult().trim();

                player = gameService.findPlayerByUsername(username);

                if (player == null || !Objects.equals(passphrase, player.getPassphrase())) {
                    throw new RuntimeException("Invalid username or passphrase");
                }

                return ScreenType.MAIN_MENU;
            } catch (IOException | SQLException e) {
                LOGGER.error("sign in", e);
                return null;
            } catch (RuntimeException e) {
                clearLines(1, 4);
                displayError(1, 0, e.getMessage());
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
            username = res.get("username").getResult().trim();

            done = gameService.findPlayerByUsername(username) == null;

            if (!done) {
                resetPrompt();
                displayError(1, 0, "Username already exists");
                placeCursor(cursor);
            }
        }

        clearLine(1);
        placeNextPromptAt(cursor.getY() + 1, cursor.getX());

        return username;
    }

    private String promptPassphraseCreation() throws IOException {
        var cursor = terminal.getCursorPosition(null);

        while (true) {
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

            String p1 = res.get("passphrase").getResult().trim();
            String p2 = res.get("re-passphrase").getResult().trim();

            if (!p1.equals(p2)) {
                resetPrompt();
                displayError(1, 0, "Passphrases did not match");
                placeCursor(cursor);
                continue;
            }

            clearLine(1);
            placeNextPromptAt(cursor.getY() + 2, cursor.getX());

            return p1;
        }
    }

    @Override
    public ScreenType createAccount() {
        clearScreen(false);

        while (true) {
            try {
                placeCursor(3, 0);
                String username = promptUsernameCreation();
                String passphrase = promptPassphraseCreation();

                this.player = gameService.createPlayer(new PlayerDto(username, passphrase, null));

                return ScreenType.MAIN_MENU;
            } catch (IOException | SQLException e) {
                LOGGER.error("create account", e);
                return null;
            } catch (InvalidUsernameException | UsernameExistsException | InvalidPassphraseException e) {
                clearLines(1, 5);
                displayError(1, 0, e.getMessage());
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

    private int[] translateLocation(String location) {
        int r = Character.toLowerCase(location.charAt(0)) - 'a';
        int c = location.charAt(1) - '0';
        // TODO goes wrong if grid is not 10x10
        return new int[] {9 - r, c};
    }

    private boolean isValidLocation(String location) {
        return location.matches("^(?i)[a-j][0-9]$");
    }

    private int[] applyDirection(int row, int col, int length, String direction) {
        int rowMul =
                switch (direction) {
                    case "North" -> -1;
                    case "South" -> 1;
                    default -> 0;
                };

        int colMul =
                switch (direction) {
                    case "East" -> 1;
                    case "West" -> -1;
                    default -> 0;
                };

        return new int[] {row + (length - 1) * rowMul, col + (length - 1) * colMul};
    }

    private void displayGameBoard(int row, int col, Long playerId) {
        var originalCursor = terminal.getCursorPosition(null);
        placeCursor(row, col);

        // general board
        for (int r = row; r < row + currentGame.getRows(); r++) {
            placeCursor(r, col);
            char c = (char) (9 - (r - row) + 'A');
            terminal.writer().print(c);
            terminal.writer().print(".".repeat(currentGame.getCols()));
        }

        placeCursor(row + currentGame.getRows(), col + 1);
        terminal.writer().print("0123456789");

        // show your board
        for (Ship e : currentGame.getShips()) {
            if (Objects.equals(e.getPlayerId(), playerId)) {
                for (int r = e.minRow(); r <= e.maxRow(); r++) {
                    for (int c = e.minCol(); c <= e.maxCol(); c++) {
                        placeCursor(r + row, c + col + 1);
                        var str = new AttributedString("*", AttributedStyle.DEFAULT.background(AttributedStyle.GREEN));
                        str.print(terminal);
                    }
                }
            }
        }

        terminal.flush();

        placeCursor(originalCursor);
    }

    private Ship promptShipPlacement(int shipNum, int length) throws IOException, SQLException {
        var builder = prompt.getPromptBuilder();
        builder.createInputPrompt()
                .name("location")
                .message(String.format("Choose a location for Ship #%d (%dx1)", shipNum, length))
                .defaultValue("")
                .addPrompt();
        builder.createListPrompt()
                .name("direction")
                .message("Choose the direction of the ship")
                .newItem("North")
                .add()
                .newItem("East")
                .add()
                .newItem("South")
                .add()
                .newItem("West")
                .add()
                .addPrompt();

        var cursor = terminal.getCursorPosition(null);

        Function<String, Void> onError = msg -> {
            try {
                resetPrompt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            displayError(3, 0, msg);
            placeCursor(cursor);

            return null;
        };

        while (true) {
            var res = prompt.prompt(builder.build());

            String location = res.get("location").getResult().trim();
            String direction = res.get("direction").getResult();

            if (!isValidLocation(location)) {
                onError.apply("Invalid location provided");
                continue;
            }

            int[] start = translateLocation(location);
            int[] end = applyDirection(start[0], start[1], length, direction);

            if (!currentGame.isValidLocation(start[0], start[1]) || !currentGame.isValidLocation(end[0], end[1])) {
                onError.apply("Invalid location: out of bounds");
                continue;
            }

            if (currentGame.isSpaceOccupied(player.getId(), start[0], start[1], end[0], end[1])) {
                onError.apply("Invalid location: overlaps with other ship");
                continue;
            }

            clearLine(3);
            resetPrompt();

            return gameService.createShip(
                    new ShipDto(start[0], end[0], start[1], end[1], player.getId(), currentGame.getId()));
        }
    }

    /*
    general format for game
    0
    1 signed in line
    2
    3 error
    4
    5 board
    .
    20 input
     */

    @Override
    public ScreenType newGameInit() {
        clearScreen(false);
        displaySignedInContent();

        try {
            currentGame = gameService.createGame(new GameDto(10, 10));

            List<Integer> shipLengths = List.of(5, 4, 3, 3, 2);
            for (int i = 0; i < shipLengths.size(); i++) {
                displayGameBoard(5, 10, player.getId());
                placeCursor(20, 0);
                currentGame.addShip(promptShipPlacement(i + 1, shipLengths.get(i)));
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
