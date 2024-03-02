package com.manneia.cli.pattern;

/**
 * @author lkx
 */
public class RemoteController {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        command.execute();
    }
}
