package com.manneia.generateweb.model.dto.Generator;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 使用代码生成器
 *
 * @author lkx
 */
@Data
public class GeneratorUseRequest implements Serializable {

    /**
     * 生成器的 id
     */
    private long id;

    /**
     * 数据模型
     */
    private Map<String, Object> dataModel;

    private static final long serialVersionUID = -9036927636842835234L;
}
