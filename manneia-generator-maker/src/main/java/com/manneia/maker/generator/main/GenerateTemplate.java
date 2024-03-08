package com.manneia.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.manneia.maker.generator.JarGenerator;
import com.manneia.maker.generator.ScriptGenerator;
import com.manneia.maker.generator.file.DynamicFileGenerator;
import com.manneia.maker.generator.utils.Utils;
import com.manneia.maker.meta.Meta;
import com.manneia.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author lkx
 */
public class GenerateTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();
        // 输出的根路径
        String projectPath = Utils.getRootProperty();
        String outputPath = projectPath + File.separator + "generated" +
                File.separator + meta.getName();
        if (FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 1. 复制原始文件
        String sourceCopyDestPath = copySource(meta, outputPath);

        // 2. 代码生成
        generateCode(meta, outputPath);

        // 3. 构建jar包
        String jarPath = buildJar(outputPath, meta);

        // 4. 封装脚本
        String shellOutputFilePath = buildScript(outputPath, jarPath);

        // 5. 生成精简版本的程序(产物包)
        buildDist(outputPath, shellOutputFilePath, sourceCopyDestPath, jarPath);
    }

    /**
     * 复制原始模板文件
     *
     * @param meta       元信息对象
     * @param outputPath 输出路径
     * @return 返回原始模板文件的路径
     */
    protected String copySource(Meta meta, String outputPath) {
        // 从原始模板文件路径复制到生成的代码包中
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, true);
        return sourceCopyDestPath;
    }

    /**
     * 代码生成
     *
     * @param meta       元信息对象
     * @param outputPath 输出路径
     * @throws IOException          IO流异常
     * @throws TemplateException    模板异常
     * @throws InterruptedException 线程异常
     */
    protected void generateCode(Meta meta, String outputPath) throws IOException, TemplateException, InterruptedException {
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
    }

    /**
     * 构建jar包
     *
     * @param outputPath 输出路径
     * @param meta       元信息对象
     * @return 返回jar包路径
     * @throws IOException          IO流异常
     * @throws InterruptedException 线程异常
     */
    protected String buildJar(String outputPath, Meta meta) throws IOException, InterruptedException {
        JarGenerator.doGenerate(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar",
                meta.getName(), meta.getVersion());
        return "target/" + jarName;
    }

    /**
     * 封装脚本
     *
     * @param outputPath 输出路径
     * @param jarPath    jar包路径
     * @return 返回shell脚本路径
     */
    protected String buildScript(String outputPath, String jarPath) {
        String shellOutputFilePath = outputPath + File.separator + "generator";
        ScriptGenerator.doGenerate(shellOutputFilePath, jarPath);
        return shellOutputFilePath;
    }

    /**
     * 构建dist精简版代码
     *
     * @param outputPath          输出路径
     * @param sourceCopyDestPath  原始模板复制到的路径
     * @param shellOutputFilePath shell脚本路径
     * @param jarPath             jar包路径
     */
    protected void buildDist(String outputPath, String sourceCopyDestPath, String shellOutputFilePath, String jarPath) {
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
