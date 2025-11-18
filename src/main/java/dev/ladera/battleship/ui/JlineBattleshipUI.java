package dev.ladera.battleship.ui;

import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class JlineBattleshipUI implements IBattleshipUI {
    private Terminal terminal;
    private ConsolePrompt prompt;

    public JlineBattleshipUI() throws IOException {
        terminal = TerminalBuilder.builder().build();
        prompt = new ConsolePrompt(terminal);
    }

    @Override
    public Screen screenStartup() {
        return null;
    }

    @Override
    public Screen screenSignIn() {
        return null;
    }

    @Override
    public Screen screenCreateAccount() {
        return null;
    }

    @Override
    public Screen screenMainMenu() {
        return null;
    }

    @Override
    public Screen screenNewGameInit() {
        return null;
    }

    @Override
    public Screen screenNewGameCpu() {
        return null;
    }
}
