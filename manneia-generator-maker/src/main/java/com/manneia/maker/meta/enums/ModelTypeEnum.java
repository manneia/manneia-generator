package com.manneia.maker.meta.enums;

import lombok.Getter;

/**
 * 模型类型枚举
 *
 * @author lkx
 */
@Getter
public enum ModelTypeEnum {
    MODEL_STRING("动态", "String"),
    MODEL_BOOLEAN("静态", "boolean");

    private final String text;

    private final String value;

    ModelTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

}
