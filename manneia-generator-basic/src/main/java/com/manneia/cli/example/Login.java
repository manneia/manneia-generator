package com.manneia.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * @author lkx
 */
@CommandLine.Command(subcommands = {ASCIIArt.class})
public class Login implements Callable<Integer> {

    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @Option(names = {"-p", "--password"}, description = "Passphrase",
            arity = "0..1", interactive = true, prompt = "请输入密码: ")
    String password;

    @Option(names = {"-cp", "--checkPassword"}, description = "check password",
            arity = "0..1", interactive = true, prompt = "请再次输入密码: ")
    String checkPassword;

    @Override
    public Integer call() throws Exception {
        System.out.println("password: " + password);
        System.out.println("checkPassword: " + checkPassword);
        return 0;
    }

    public static void main(String[] args) {
        new CommandLine(new Login()).execute("-u", "user123", "-p", "xxx", "-cp");
    }
}
