package com.manneia.maker.generator.file;

import com.manneia.maker.utils.Utils;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author manneia
 */
public class FileGenerator {

    public static void doGenerator(Object config) {
        // 1. 静态文件生成
        String projectPath = Utils.getRootProperty();
        // 输入路径
        String inputPath = projectPath + File.separator + "manneia-generator-demo-projects" + File.separator + "acm-template";
        StaticFileGenerator.copyFilesByHuTool(inputPath, projectPath);

        // 2. 动态文件生成
        String dynamicInputPath = projectPath + File.separator + "manneia-generator-maker" + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/src/com/manneia/acm/MainTemplate.java";
        try {
            DynamicFileGenerator.doGenerate(dynamicInputPath, dynamicOutputPath, config);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}
