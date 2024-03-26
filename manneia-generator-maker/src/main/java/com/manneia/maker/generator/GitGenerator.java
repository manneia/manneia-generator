package com.manneia.maker.generator;

import cn.hutool.core.util.StrUtil;
import com.manneia.maker.utils.Utils;

import java.io.*;

/**
 * Git 托管
 *
 * @author lkx
 */
public class GitGenerator {
    public static void doGenerate(String inputRootPath) throws IOException, InterruptedException {
        String command = "git init";
        // 创建ProcessBuilder并设置执行的命令
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        // 如果项目目录非空，则设置命令执行的目录
        if (!StrUtil.isBlank(inputRootPath)) {
            processBuilder.directory(new File(inputRootPath));
        }
        // 启动进程
        Process start = processBuilder.start();
        // 获取进程的输出流，并读取输出信息
        InputStream inputStream = start.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line); // 打印命令执行的输出信息
        }
        int exitCode = start.waitFor();
        if (exitCode != 0) {
            System.out.println("命令执行失败");
        } else {
            System.out.println("命令执行成功");
        }
    }
}
