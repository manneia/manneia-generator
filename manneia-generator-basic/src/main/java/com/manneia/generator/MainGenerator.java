package com.manneia.generator;

import com.manneia.model.MainTemplateConfig;
import com.manneia.utils.Utils;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

import static com.manneia.generator.StaticGenerator.copyFilesByHuTool;

/**
 * @author lkx
 */
public class MainGenerator {
    public static void main(String[] args) {

        // 1. 静态文件生成
        String projectPath = Utils.getRootProperty();
        // 输入路径
        String inputPath = projectPath + File.separator + "manneia-generator-demo-projects" + File.separator + "acm-template";
        StaticGenerator.copyFilesByRecursive(inputPath, projectPath);

        // 2. 动态文件生成
        String dynamicInputPath = projectPath + File.separator+ "manneia-generator-basic" + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/src/com/manneia/acm/MainTemplate.java";
        MainTemplateConfig templateConfig = new MainTemplateConfig();
        templateConfig.setAuthor("manneia");
        templateConfig.setOutputText("你好,结果是");
        templateConfig.setLoop(true);

        try {
            DynamicGenerator.doGenerate(dynamicInputPath, dynamicOutputPath, templateConfig);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}
