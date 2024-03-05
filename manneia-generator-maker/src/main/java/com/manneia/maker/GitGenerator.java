package com.manneia.maker;

import com.manneia.maker.generator.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Git 托管
 *
 * @author lkx
 */
public class GitGenerator {
    public static void doGenerate(String inputRootPath) throws IOException, InterruptedException {
        String command = "git init";
        Process start = Utils.getProcess(command, inputRootPath);
        InputStream inputStream = start.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        int exitCode = start.waitFor();
        if (exitCode != 0) {
            System.out.println("命令执行失败");
        } else {
            System.out.println("命令执行成功");
        }
    }
}
