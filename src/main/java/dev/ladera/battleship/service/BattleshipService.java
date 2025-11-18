package dev.ladera.battleship.service;

import java.io.IOException;
import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleshipService implements IBattleshipService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BattleshipService.class);

    private final IGameService gameService;
    private Terminal terminal;
    private ConsolePrompt prompt;

    public BattleshipService(IGameService gameService) {
        this.gameService = gameService;

        try {
            terminal = TerminalBuilder.builder().build();
            prompt = new ConsolePrompt(terminal);
        } catch (IOException e) {
            LOGGER.error("Error while creating terminal", e);
        }
    }

    @Override
    public void run() {
        toStartup();
        /*
        TODO

         */
    }

    @Override
    public void toStartup() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();

        terminal.writer()
                .println(
                        """
            █████▄  ▄▄▄ ▄▄▄▄▄▄ ▄▄▄▄▄▄ ▄▄    ▄▄▄▄▄  ▄▄▄▄ ▄▄ ▄▄ ▄▄ ▄▄▄▄
            ██▄▄██ ██▀██  ██     ██   ██    ██▄▄  ███▄▄ ██▄██ ██ ██▄█▀
            ██▄▄█▀ ██▀██  ██     ██   ██▄▄▄ ██▄▄▄ ▄▄██▀ ██ ██ ██ ██
            """);
        terminal.writer().flush();

        var builder = prompt.getPromptBuilder()
                .createListPrompt()
                .name("startup-action")
                .message("What would you like to do?")
                .newItem("Sign in")
                .add()
                .newItem("Create account")
                .add()
                .newItem("Quit")
                .add()
                .addPrompt();

        try {
            var res = prompt.prompt(builder.build());
            terminal.writer().println("You chose " + res.get("startup-action").getResult());
            terminal.writer().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toSignIn() {}

    @Override
    public void toAccountCreation() {}

    @Override
    public void toMainMenu() {}

    @Override
    public void toNewGameInit() {}

    @Override
    public void toNewGameCpu() {}
}
