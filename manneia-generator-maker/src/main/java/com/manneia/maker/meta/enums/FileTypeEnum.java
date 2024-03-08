package com.manneia.maker.meta.enums;

import lombok.Getter;

/**
 * 文件类型枚举
 *
 * @author lkx
 */
@Getter
public enum FileTypeEnum {
    DIR("目录", "dir"),
    FILE("文件", "file"),
    GROUP("文件组", "group");

    private final String text;

    private final String value;

    FileTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

}
