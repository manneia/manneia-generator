package com.manneia.maker.model;

import lombok.Data;

/**
 * @author lkx
 */
@Data
public class MainTemplate {
    /**
     * 是否开启循环
     */
    private Boolean loop = false;

    /**
     * 是否生成 .gitignore 文件
     */
    private Boolean needGit = false;
}
