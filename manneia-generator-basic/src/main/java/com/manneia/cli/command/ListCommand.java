package com.manneia.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;

/**
 * @author lkx
 */
@Command(name = "list", description = "查看文件列表", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {
    @Override
    public void run() {
        String projectPath = System.getProperty("user.dir");
        // 整个项目的根路径
        File parentFile = new File(projectPath);
        System.out.println(parentFile);
        // 输入路径
        List<File> files = FileUtil.loopFiles(parentFile);
        // 遍历输出文件
        files.forEach(System.out::println);
    }
}
