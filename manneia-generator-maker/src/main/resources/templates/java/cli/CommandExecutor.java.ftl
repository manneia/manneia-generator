package ${basePackage}.maker.cli;

import ${basePackage}.maker.cli.command.ConfigCommand;
import ${basePackage}.maker.cli.command.GeneratorCommand;
import ${basePackage}.maker.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * @author lkx
 */
@Command(name = "${name}", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {
    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GeneratorCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }


    @Override
    public void run() {
        // 不输入子命令时,给出友好提示
        System.out.println("请输入具体命令,或者输入--help 查看命令提示");
    }

    /**
     * 执行命令
     *
     * @param args 命令数组
     * @return 返回执行结果
     */
    public Integer doExecute(String[] args) {
        return commandLine.execute(args);
    }
}
