package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

<#--, <#if modelInfo.description??>description = "${modelInfo.description}", </#if>-->
<#--生成选项-->
<#macro generateOption indent modelInfo>
${indent}@Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}",</#if>"--${modelInfo.fieldName}"}, arity = "0..1",interactive = true, echo = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = <#if modelInfo.type=="Boolean">true<#else>${modelInfo.defaultValue?c}</#if></#if>;
</#macro>


<#macro generateCommand indent modelInfo>
${indent}System.out.println("输入${modelInfo.groupName}配置:");
${indent}CommandLine ${modelInfo.groupKey}commandLine = new CommandLine(${modelInfo.type}Command.class);
${indent}${modelInfo.groupKey}commandLine.execute(${modelInfo.allArgsStr});
</#macro>
/**
 * @author ${author}
 */
@Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {

<#list modelConfig.models as modelInfo>
        <#-- 有分组 -->
<#if modelInfo.groupKey??>
    /**
     * ${modelInfo.groupName}
     */
    static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();
<#--    , description = "${modelInfo.description}"-->
    @Command(name = "${modelInfo.groupName}")
    @Data
    public static class ${modelInfo.type}Command implements Runnable{
        <#list modelInfo.models as subModelInfo>
            <@generateOption indent="        " modelInfo=subModelInfo/>
        </#list>

        @Override
        public void run() {
            <#list modelInfo.models as subModelInfo>
            ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
            </#list>
        }
    }
    <#else>
    <#--无分组-->
    <@generateOption indent="    " modelInfo=modelInfo/>
    </#if>
</#list>
    <#--生成调用方法-->
    @Override
    public Integer call() throws Exception {
        <#list modelConfig.models as modelInfo>
            <#if modelInfo.groupKey??>
                <#if modelInfo.condition??>
        if(${modelInfo.condition}){
            <@generateCommand indent="            " modelInfo=modelInfo/>
        }
                <#else>
        <@generateCommand indent="        " modelInfo=modelInfo/>
                </#if>
            </#if>
        </#list>
        <#--填充数据模型对象-->
        DataModel datamodel = new DataModel();
        BeanUtil.copyProperties(this, datamodel);
        <#list modelConfig.models as modelInfo>
            <#if modelInfo.groupKey??>
        datamodel.${modelInfo.groupKey} = ${modelInfo.groupKey};
            </#if>
        </#list>
        System.out.println("配置信息: " + datamodel);
        MainGenerator.doGenerator(datamodel);
        return 0;
    }
}
