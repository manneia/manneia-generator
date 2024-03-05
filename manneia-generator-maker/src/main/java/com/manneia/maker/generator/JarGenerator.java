package com.manneia.maker.generator;

import com.manneia.maker.generator.utils.Utils;

import java.io.*;

/**
 * 程序构建jar包
 *
 * @author lkx
 */
public class JarGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        // 调用Process类,执行命令
        // 清理之前的构建并打包
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        // 注意不同操作系统，执行的命令不同，默认Windows系统

//        ProcessBuilder processBuilder = new ProcessBuilder(winMavenCommand.split(" "));
//        processBuilder.directory(new File(projectDir));
//
//        Process process = processBuilder.start();
//
//        InputStream inputStream = process.getInputStream();
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            System.out.println(line);
//        }
        Process process = Utils.getProcess(winMavenCommand, projectDir);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.out.println("命令执行结束");
            throw new RuntimeException("生成jar包失败");
        } else {
            System.out.println("生成jar包成功");
        }
    }

    public static void main(String[] args) throws Exception {
        doGenerate("D:\\Project\\project\\manneia-generator\\manneia-generator-basic");
    }
}
