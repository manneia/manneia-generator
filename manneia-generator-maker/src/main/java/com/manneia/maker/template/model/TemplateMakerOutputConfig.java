package com.manneia.maker.template.model;

import lombok.Data;

/**
 * 输出配置类
 *
 * @author lkx
 */
@Data
public class TemplateMakerOutputConfig {
    /**
     * 从未分组文件中移除组内的同名文件
     */
    private boolean removeGroupFilesFromRoot = true;
}
