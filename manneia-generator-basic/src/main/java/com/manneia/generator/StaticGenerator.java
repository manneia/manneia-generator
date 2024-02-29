package com.manneia.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author lkx
 */
public class StaticGenerator {

    /**
     * 拷贝文件
     *
     * @param inputPath  源路径
     * @param outputPath 输出路径
     */
    public static void copyFilesByHuTool(String inputPath, String outputPath) {
        FileUtil.copy(inputPath, outputPath, false);
    }

    /**
     * 递归拷贝文件
     *
     * @param inputFile  输入文件
     * @param outputFile 输出文件
     * @throws IOException 异常类
     */
    private static void copyFilesByRecursive(File inputFile, File outputFile) throws IOException {
        if (inputFile.isDirectory()) {
            File destOutputFile = new File(outputFile, inputFile.getName());
            if (!destOutputFile.exists()) {
                destOutputFile.mkdirs();
            }
            File[] files = inputFile.listFiles();
            if (ArrayUtil.isEmpty(files)) {
                return;
            }
            for (File file : files) {
                copyFilesByRecursive(file, destOutputFile);
            }
        } else {
            Path destPath = outputFile.toPath().resolve(inputFile.getName());
            Files.copy(inputFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * @param inputPath 源路径
     * @param destPath  输出路径
     */
    public static void copyFilesByRecursive(String inputPath, String destPath) {
        File inputFile = new File(inputPath);
        File outputFile = new File(destPath);
        try {
            copyFilesByRecursive(inputFile, outputFile);
        } catch (IOException e) {
            System.out.println("file copy failed");
            e.printStackTrace();
        }

    }
}
