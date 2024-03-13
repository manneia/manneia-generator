package com.manneia.maker.template.model;

import com.manneia.maker.meta.Meta;
import com.manneia.maker.utils.Utils;
import lombok.Data;

/**
 * 模板制作配置
 *
 * @author lkx
 */
@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta = new Meta();

    private String projectPath = Utils.getRootProperty();

    private String originProjectPath;

    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();
}
