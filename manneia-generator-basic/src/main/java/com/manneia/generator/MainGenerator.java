package com.manneia.generator;

import com.manneia.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author lkx
 */
public class MainGenerator {

    public static void doGenerator(MainTemplateConfig config) throws TemplateException, IOException {
        String inputRootPath = "D:\\Project\\project\\manneia-generator" +
                "\\manneia-generator-demo-projects" +
                "\\acm-template-pro";
        String outputRootPath = "D:\\Project\\project\\manneia-generator";
        String inputPath;
        String outputPath;
        inputPath = new File(inputRootPath, "src/com/manneia/acm/MainTemplate.java.ftl")
                .getAbsolutePath();
        outputPath = new File(outputRootPath, "src/com/manneia/acm/MainTemplate.java")
                .getAbsolutePath();
        DynamicGenerator.doGenerate(inputPath, outputPath, config);
        inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
        outputPath = new File(outputRootPath, ".gitignore").getAbsolutePath();
        StaticGenerator.copyFilesByHuTool(inputPath, outputPath);
        inputPath = new File(inputRootPath, "README.md").getAbsolutePath();
        outputPath = new File(outputRootPath, "README.md").getAbsolutePath();
        StaticGenerator.copyFilesByHuTool(inputPath, outputPath);
        // 1. 静态文件生成
//        String projectPath = Utils.getRootProperty();
        // 输入路径
//        String inputPath = projectPath + File.separator + "manneia-generator-demo-projects" +
//                File.separator + "acm-template";
//        StaticGenerator.copyFilesByRecursive(inputPath, projectPath);

        // 2. 动态文件生成
//        String dynamicInputPath = projectPath + File.separator + "manneia-generator-basic" +
//                File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
//        String dynamicOutputPath = projectPath + File.separator +
//                "acm-template/src/com/manneia/acm/MainTemplate.java";
//        MainTemplateConfig templateConfig = new MainTemplateConfig();
//        templateConfig.setAuthor(config.getAuthor());
//        templateConfig.setOutputText(config.getOutputText());
//        templateConfig.setLoop(config.getLoop());
//        DynamicGenerator.doGenerate(dynamicInputPath, dynamicOutputPath, templateConfig);
    }
}
