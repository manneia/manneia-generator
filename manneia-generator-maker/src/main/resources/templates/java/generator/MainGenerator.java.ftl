package ${basePackage}.generator;

import ${basePackage}.model.MainTemplateConfig;
import ${basePackage}.utils.Utils;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author lkx
 */
public class MainGenerator {

    public static void doGenerator(MainTemplateConfig config) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";
        String inputPath;
        String outputPath;
        <#list fileConfig.files as fileInfo>
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType=="dynamic">
            DynamicGenerator.doGenerate(inputPath, outputPath, config);
        <#elseif fileInfo.generateType=="static">
            StaticGenerator.copyFilesByHuTool(inputPath, outputPath);
        </#if>
        </#list>
    }
}
