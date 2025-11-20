package dev.ladera.battleship.screen;

import dev.ladera.battleship.config.StringConstants;
import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.exception.InvalidPassphraseException;
import dev.ladera.battleship.exception.InvalidUsernameException;
import dev.ladera.battleship.exception.UsernameExistsException;
import dev.ladera.battleship.model.Player;
import dev.ladera.battleship.service.IGameService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.terminal.Cursor;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JlineBattleshipScreen implements IBattleshipScreen {
    private static final Logger LOGGER = LoggerFactory.getLogger(JlineBattleshipScreen.class);
    private static final AttributedStyle ERROR_STYLE = AttributedStyle.DEFAULT.foreground(AttributedStyle.RED);

    private IGameService gameService;
    private Terminal terminal;
    private ConsolePrompt prompt;
    private ScreenType currentScreen;

    private Player player;

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

        try {
            terminal.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ScreenType startUp() {
        clearScreen(false);

        terminal.writer()
                .println(
                        """

            █████▄  ▄▄▄ ▄▄▄▄▄▄ ▄▄▄▄▄▄ ▄▄    ▄▄▄▄▄  ▄▄▄▄ ▄▄ ▄▄ ▄▄ ▄▄▄▄
            ██▄▄██ ██▀██  ██     ██   ██    ██▄▄  ███▄▄ ██▄██ ██ ██▄█▀
            ██▄▄█▀ ██▀██  ██     ██   ██▄▄▄ ██▄▄▄ ▄▄██▀ ██ ██ ██ ██

            """);

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
                case null -> {}
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
        builder.createInputPrompt().name("username").message("Username:").addPrompt();
        builder.createInputPrompt()
                .name("passphrase")
                .message("Passphrase:")
                .mask('*')
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
                placeCursor(0, 0);
                var str = new AttributedString(e.getMessage(), ERROR_STYLE);
                str.print(terminal);
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

    private String promptUsernameCreation() throws IOException, SQLException {
        boolean done = false;
        String username = null;

        var cursor = terminal.getCursorPosition(null);
        var builder = prompt.getPromptBuilder()
                .createInputPrompt()
                .name("username")
                .message("Username:")
                .addPrompt();

        while (!done) {
            var res = prompt.prompt(builder.build());
            username = res.get("username").getResult();

            done = gameService.findPlayerByUsername(username) == null;

            if (!done) {
                resetPrompt();

                clearLine(0);
                placeCursor(0, 0);
                var str = new AttributedString("Username already exists", ERROR_STYLE);
                str.print(terminal);

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
                    .addPrompt()
                    .createInputPrompt()
                    .name("re-passphrase")
                    .message("Re-enter your passphrase:")
                    .mask('*')
                    .addPrompt();

            var res = prompt.prompt(builder.build());

            p1 = res.get("passphrase").getResult();
            String p2 = res.get("re-passphrase").getResult();

            done = p1.equals(p2);

            if (!done) {
                resetPrompt();

                clearLine(0);
                placeCursor(0, 0);
                var str = new AttributedString("Passphrases did not match.", ERROR_STYLE);
                str.print(terminal);

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
                placeCursor(0, 0);
                var str = new AttributedString(e.getMessage(), ERROR_STYLE);
                str.println(terminal);
            }
        }
    }

    @Override
    public ScreenType mainMenu() {
        return null;
    }

    @Override
    public ScreenType newGameInit() {
        return null;
    }

    @Override
    public ScreenType newGameCpu() {
        return null;
    }
}
