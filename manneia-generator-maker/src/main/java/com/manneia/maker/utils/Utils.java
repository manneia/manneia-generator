package com.manneia.maker.utils;

import cn.hutool.core.util.StrUtil;
import com.manneia.maker.meta.Meta;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 工具函数
 *
 * @author lkx
 */
public class Utils {
    /**
     * 获取项目根目录
     *
     * @return 返回目录路径字符串
     */
    public static String getRootProperty() {
        return System.getProperty("user.dir");
    }


    /**
     * 通过指定的命令和项目目录，在新的进程中执行命令，并返回该进程对象。
     *
     * @param command    需要执行的命令，可以是多个参数，用空格分隔。
     * @param projectDir 命令执行时的项目目录，如果为空或null，则不在指定目录下执行。
     * @return 返回启动的进程对象。
     * @throws IOException 如果启动进程时发生IO异常。
     */
    public static Process getProcess(String command, String projectDir) throws IOException {
        // 创建ProcessBuilder并设置执行的命令
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        // 如果项目目录非空，则设置命令执行的目录
        if (!StrUtil.isBlank(projectDir)) {
            processBuilder.directory(new File(projectDir));
        }
        // 启动进程
        Process process = processBuilder.start();
        // 获取进程的输出流，并读取输出信息
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line); // 打印命令执行的输出信息
        }
        return process;
    }

    /**
     * 从未分组文件中移除组内的同名文件
     *
     * @param fileInfoList 文件信息列表
     * @return 返回过滤后的文件信息列表
     */
    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(
            List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 获取所有分组
        List<Meta.FileConfig.FileInfo> groupFileInfoList = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        // 获取所有分组内的文件列表
        List<Meta.FileConfig.FileInfo> groupInnerFileInfoList = groupFileInfoList.stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .collect(Collectors.toList());
        // 获取所有分组内文件输入路径集合
        Set<String> fileInputPathSet = groupInnerFileInfoList.stream()
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toSet());
        // 移除所有名称在集合中的外层文件
        return fileInfoList.stream()
                .filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());
    }
}
