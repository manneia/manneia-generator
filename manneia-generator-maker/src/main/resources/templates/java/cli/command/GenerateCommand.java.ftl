package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * @author lkx
 */
@Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {

    <#list modelConfig.models as modelInfo>
    /**
     * ${modelInfo.description}
     */
    @Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}",</#if>"--${modelInfo.fieldName}" }, arity = "0..1", <#if modelInfo.description??>description = "${modelInfo.description}",</#if>
            interactive = true, echo = true)
    private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??>= <#if modelInfo.type=="String">"${modelInfo.defaultValue?c}"<#else>${modelInfo.defaultValue?c}</#if></#if>;
    </#list>

    @Override
    public Integer call() throws Exception {
        DataModel config = new DataModel();
        BeanUtil.copyProperties(this, config);
        System.out.println("配置信息: " + config);
        MainGenerator.doGenerator(config);
        return 0;
    }
}
