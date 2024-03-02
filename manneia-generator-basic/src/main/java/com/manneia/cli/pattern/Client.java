package com.manneia.cli.pattern;

/**
 * @author lkx
 */
public class Client {
    public static void main(String[] args) {
        Device tv = new Device("TV");
        Device stereo = new Device("Stereo");

        TurnOnCommand turnOnCommand = new TurnOnCommand(tv);
        TurnOffCommand turnOffCommand = new TurnOffCommand(stereo);

        RemoteController remoteController = new RemoteController();

        remoteController.setCommand(turnOnCommand);
        remoteController.pressButton();
        remoteController.setCommand(turnOffCommand);
        remoteController.pressButton();
    }
}
