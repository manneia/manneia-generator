package com.manneia;

import com.manneia.cli.CommandExecutor;

/**
 * @author lkx
 */
public class Main {
    public static void main(String[] args) {
        //        args = new String[] {"generator", "-l", "-a", "-o"};
        //        args = new String[]{"config"};
        //        args = new String[]{"list"};
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}