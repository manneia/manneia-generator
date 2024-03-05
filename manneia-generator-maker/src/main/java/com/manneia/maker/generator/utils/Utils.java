package com.manneia.maker.generator.utils;

import cn.hutool.core.util.StrUtil;

import java.io.*;

/**
 * 工具函数
 *
 * @author lkx
 */
public class Utils {
    /**
     * 获取项目根目录
     * @return 返回目录路径字符串
     */
    public static String getRootProperty() {
        return System.getProperty("user.dir");
    }

    public static Process getProcess(String command,String  projectDir) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        if (!StrUtil.isBlank(projectDir)) {
            processBuilder.directory(new File(projectDir));
        }

        Process process = processBuilder.start();

        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        return process;
    }
}
