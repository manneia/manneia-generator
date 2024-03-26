package com.manneia.generateweb.model.dto.Generator;

import com.manneia.maker.meta.Meta;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lkx
 */
@Data
public class GeneratorMakeRequest implements Serializable {

    /**
     * 元信息
     */
    private Meta meta;

    /**
     * 模板文件压缩包
     */
    private String zipFilePath;

    private static final long serialVersionUID = -3348307957035435416L;
}
