package com.manneia.maker.meta.enums;

import lombok.Getter;

/**
 * 文件生成方式类型枚举
 *
 * @author lkx
 */
@Getter
public enum FileGenerateTypeEnum {
    DYNAMIC("动态", "dynamic"),
    STATIC("静态", "static");

    private final String text;

    private final String value;

    FileGenerateTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

}
