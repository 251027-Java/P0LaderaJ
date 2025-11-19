package dev.ladera.battleship.screen;

import dev.ladera.battleship.config.StringConstants;
import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.model.Player;
import dev.ladera.battleship.service.IGameService;
import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class JlineBattleshipScreen implements IBattleshipScreen {
    private static final Logger LOGGER = LoggerFactory.getLogger(JlineBattleshipScreen.class);

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
    }

    @Override
    public ScreenType startUp() {
        clearScreen(false);

        terminal.writer().println("""

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
            prompt.prompt(List.of());

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

        try {
            var res = prompt.prompt(builder.build());
            // prompt.prompt(List.of()); // TODO is this necessary?
            String username = res.get("username").getResult();
            String passphrase = res.get("passphrase").getResult();

            // TODO check if able to log in
            terminal.writer().println("| " + username + " | " + passphrase);
            terminal.flush();
        } catch (IOException e) {
            LOGGER.error("sign in", e);
        }

        return null;
    }

    private String promptUsernameCreation() throws IOException, SQLException {
        boolean done = false;
        String username = null;

        var cursor = terminal.getCursorPosition(null);
        LOGGER.info("username cursor: {}", cursor);

        while (!done) {
            var builder = prompt.getPromptBuilder()
                    .createInputPrompt()
                    .name("username")
                    .message("Username:")
                    .addPrompt();

            var res = prompt.prompt(builder.build());
            prompt.prompt(List.of());
            LOGGER.info("cursor after re-prompting: {}", terminal.getCursorPosition(null));
            username = res.get("username").getResult();

            Player player = gameService.findPlayerByUsername(username);
            // TODO validate length of username
            done = player == null;

            if (!done) {
                terminal.puts(InfoCmp.Capability.cursor_address, cursor.getY() + 2, cursor.getX());
                terminal.flush();

                var str = new AttributedString("Try again.", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
                str.println(terminal);
                terminal.flush();

                terminal.puts(InfoCmp.Capability.cursor_address, cursor.getY(), cursor.getX());
                terminal.flush();
            }
        }

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
            prompt.prompt(List.of());

            p1 = res.get("passphrase").getResult();
            String p2 = res.get("re-passphrase").getResult();

            done = p1.equals(p2);

            if (!done) {
                terminal.puts(InfoCmp.Capability.cursor_address, cursor.getY() + 3, cursor.getX());

                var str = new AttributedString(
                        "Passphrases did not match.", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));

                str.println(terminal);

                terminal.puts(InfoCmp.Capability.cursor_address, cursor.getY(), cursor.getX());
                terminal.flush();
            }
        }

        return p1;
    }

    @Override
    public ScreenType createAccount() {
        clearScreen(false);

        try {
            String username = promptUsernameCreation();
            String passphrase = promptPassphraseCreation();

            player = gameService.createPlayer(new PlayerDto(username, passphrase, null));

            if (player != null) {
                return ScreenType.MAIN_MENU;
            }
        } catch (IOException | SQLException e) {
            LOGGER.error("create account", e);
        }

        return null;
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
