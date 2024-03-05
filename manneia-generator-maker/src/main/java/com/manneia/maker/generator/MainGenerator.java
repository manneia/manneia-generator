package com.manneia.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.manneia.maker.generator.file.DynamicFileGenerator;
import com.manneia.maker.generator.utils.Utils;
import com.manneia.maker.meta.Meta;
import com.manneia.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author lkx
 */
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        System.setProperty("file.encoding", "UTF-8");

        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);
        // 输出的根路径
        String projectPath = Utils.getRootProperty();
        String outputPath = projectPath + File.separator + "generated" +
                File.separator + meta.getName();
        if (FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 从原始模板文件路径复制到生成的代码包中
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, true);

        // 读取resources 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String resourceAbsolutePath = classPathResource.getAbsolutePath();

        // java包的基础路径
        // com.manneia
        String outputBasePackage = meta.getBasePackage();
        // com/manneia
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage,
                "."));
        // generated/src/main/java/com/manneia/xxx
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" +
                outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // model.DataModel
        inputFilePath = resourceAbsolutePath + File.separator +
                "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath = resourceAbsolutePath + File.separator +
                "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator +
                "cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ListCommand
        inputFilePath = resourceAbsolutePath + File.separator +
                "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ConfigCommand
        inputFilePath = resourceAbsolutePath + File.separator +
                "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.commandExecutor
        inputFilePath = resourceAbsolutePath + File.separator +
                "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        // Main
        inputFilePath = resourceAbsolutePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        inputFilePath = resourceAbsolutePath + File.separator +
                "templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.DynamicFileGenerator
        inputFilePath = resourceAbsolutePath + File.separator +
                "templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.MainGenerator
        inputFilePath = resourceAbsolutePath + File.separator +
                "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // pom.xml
        inputFilePath = resourceAbsolutePath + File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        inputFilePath = resourceAbsolutePath + File.separator + "templates/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 构建jar包
        JarGenerator.doGenerate(outputPath);

        // 封装脚本
        String shellOutputFilePath = outputPath + File.separator + "generator";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar",
                meta.getName(), meta.getVersion());
        String jarPath = "target/" + jarName;
        ScriptGenerator.doGenerate(shellOutputFilePath, jarPath);

        // 生成精简版本的程序(产物包)
        String distOutputPath = outputPath + "-dist";
        // 拷贝jar包,
        String targetAbsolutePath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        // 拷贝脚本文件,
        FileUtil.copy(shellOutputFilePath, distOutputPath, true);
        FileUtil.copy(shellOutputFilePath + ".bat", distOutputPath, true);
        // 拷贝原始模板文件
        FileUtil.copy(sourceCopyDestPath, distOutputPath, true);
    }
}
