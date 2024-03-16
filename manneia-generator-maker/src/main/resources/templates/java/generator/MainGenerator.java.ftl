package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
    <#if fileInfo.generateType=="static">
${indent}StaticGenerator.copyFilesByHuTool(inputPath, outputPath);
    <#else>
${indent}DynamicGenerator.doGenerate(inputPath, outputPath, model);
    </#if>
</#macro>



/**
 * @author ${author}
 */
public class MainGenerator {

    public static void doGenerator(DataModel model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";
        String inputPath;
        String outputPath;
        <#list modelConfig.models as modelInfo>
        <#-- 有分组 -->
        <#if modelInfo.groupKey??>
            <#list modelInfo.models as subModelInfo>
        ${subModelInfo.type} ${subModelInfo.fieldName} = model.${modelInfo.groupKey}.${subModelInfo.fieldName};
            </#list>
        <#-- 无分组 -->
        <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
        </#list>
    <#list fileConfig.files as fileInfo>
        <#if fileInfo.groupKey??>
        // groupKey = ${fileInfo.groupKey}
            <#if fileInfo.condition??>
        if (${fileInfo.condition}) {
            <#list fileInfo.files as subFileInfo>
                <@generateFile indent="            " fileInfo=subFileInfo />
            </#list>
        }
        <#else>
        <#list fileInfo.files as subFileInfo>
            <@generateFile indent="        " fileInfo=subFileInfo />
        </#list>
        </#if>
        <#else>
        <#if fileInfo.condition??>
        if (${fileInfo.condition}) {
                <@generateFile indent="            " fileInfo=fileInfo />
        }
        <#else>
            <@generateFile fileInfo=fileInfo indent="        " />
        </#if>
        </#if>
    </#list>
    }
}
