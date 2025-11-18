package dev.ladera.battleship.screen;

import dev.ladera.battleship.config.StringConstants;
import dev.ladera.battleship.dto.PlayerDto;
import dev.ladera.battleship.model.Player;
import dev.ladera.battleship.service.IGameService;
import java.io.IOException;
import java.sql.SQLException;
import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
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
            terminal.writer().flush();
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
            String username = res.get("username").getResult();
            String passphrase = res.get("passphrase").getResult();

            // TODO check if able to log in
            terminal.writer().println("| " + username + " | " + passphrase);
            terminal.writer().flush();
        } catch (IOException e) {
            LOGGER.error("sign in", e);
        }

        return null;
    }

    private String promptUsername() throws IOException, SQLException {
        boolean done = false;
        String username = null;

        while (!done) {
            var builder = prompt.getPromptBuilder()
                    .createInputPrompt()
                    .name("username")
                    .message("Username:")
                    .addPrompt();

            var res = prompt.prompt(builder.build());
            username = res.get("username").getResult();

            Player player = gameService.findPlayerByUsername(username);

            if (player != null) {
                // TODO duplicate username, prompt again
            }
        }

        return username;
    }

    private String promptPassphrase() throws IOException {
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
        String p1 = res.get("passphrase").getResult();
        String p2 = res.get("re-passphrase").getResult();

        if (!p1.equals(p2)) {
            // TODO passphrase was not the same
        }

        return p1;
    }

    @Override
    public ScreenType createAccount() {
        clearScreen(false);

        try {
            String username = promptUsername();
            String passphrase = promptPassphrase();

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
