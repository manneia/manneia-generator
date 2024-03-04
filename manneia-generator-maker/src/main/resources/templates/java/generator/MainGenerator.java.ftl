package ${basePackage}.generator;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author ${author}
 */
public class MainGenerator {

    public static void doGenerator(Object model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";
        String inputPath;
        String outputPath;
        <#list fileConfig.files as fileInfo>
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType=="dynamic">
            DynamicGenerator.doGenerate(inputPath, outputPath, model);
        <#elseif fileInfo.generateType=="static">
            StaticGenerator.copyFilesByHuTool(inputPath, outputPath);
        </#if>
        </#list>
    }
}
