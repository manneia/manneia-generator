# ${name}

> ${description}
>
> 作者：${author}
>
> 基于 [manneia] 的 [定制代码生成器项目](https://github.com/manneia/manneia-generator) 制作，感谢您的使用！

可以通过命令行交互式输入的方式动态生成想要的项目代码

## 使用说明

执行项目根目录下的脚本文件：

```
generator <命令> <选项参数>
```

示例命令：

```
generator generate <#list modelConfig.models as modelInfo><#if modelInfo.models??><#list modelInfo.models as subModelInfo>-${subModelInfo.abbr}</#list><#else>-${modelInfo.abbr}</#if> </#list>
```

## 参数说明

<#list modelConfig.models as modelInfo>
${modelInfo?index + 1}.<#if modelInfo.models??><#list modelInfo.models as subModelInfo>${modelInfo?index + 1}-${subModelInfo.fieldName}</#list><#else>-${modelInfo.fieldName}</#if>

    类型：${modelInfo.type}

    描述：${modelInfo.description}

    默认值：<#if modelInfo.models??><#list modelInfo.models as subModelInfo>-${subModelInfo.defaultValue?c}</#list><#else>-${modelInfo.defaultValue?c}</#if>

    缩写： -<#if modelInfo.models??><#list modelInfo.models as subModelInfo>-${subModelInfo.abbr}</#list><#else>-${modelInfo.abbr}</#if>
</#list>