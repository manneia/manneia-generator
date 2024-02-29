package com.manneia.model;

import lombok.Data;

/**
 * 模板动态配置
 *
 * @author lkx
 */
@Data
public class MainTemplateConfig {

    /**
     * 作者
     */
    private String author = "manneia";

    /**
     * 输出信息
     */
    private String outputText = "输出结果";

    /**
     * 是否开启循环
     */
    private boolean loop = false;
}
