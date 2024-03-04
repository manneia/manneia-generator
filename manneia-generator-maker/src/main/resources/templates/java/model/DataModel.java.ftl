package ${basePackage}.model;

import lombok.Data;

/**
 * 数据模型
 *
 * @author ${author}
 */
@Data
public class DataModel {

<#list modelConfig.models as modelInfo>

    <#if modelInfo.description??>
    /**
     * ${modelInfo.description}
     */
    </#if>
    private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> =<#if modelInfo.type=="String">"${modelInfo.defaultValue?c}"<#else>${modelInfo.defaultValue?c}</#if></#if>;

</#list>
}
