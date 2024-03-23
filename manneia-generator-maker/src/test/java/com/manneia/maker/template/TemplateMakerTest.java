package com.manneia.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.manneia.maker.meta.Meta;
import com.manneia.maker.template.model.TemplateMakerConfig;
import com.manneia.maker.template.model.TemplateMakerFileConfig;
import com.manneia.maker.template.model.TemplateMakerModelConfig;
import com.manneia.maker.template.model.TemplateMakerOutputConfig;
import com.manneia.maker.utils.Utils;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class TemplateMakerTest {

    private static final String PROJECT_PATH = "manneia-generator-demo-projects/springboot-init";

    @Test
    public void testMakeTemplateBug1() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("Acm 示例模板生成器");
        String projectPath = Utils.getRootProperty();
        String originProjectPath = FileUtil.getAbsolutePath(new File(projectPath).
                getParentFile()) + File.separator + PROJECT_PATH;
        String fileInputPath2 = "./";
        List<String> inputFilePathList = Collections.singletonList(fileInputPath2);
        // 文件参数配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig.setPath(fileInputPath2);
        templateMakerFileConfig.setFiles(Collections.singletonList(fileInfoConfig));
        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig.setFieldName("className");
        modelInfoConfig.setType("String");
        modelInfoConfig.setReplaceText("BaseResponse");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Collections.singletonList(modelInfoConfig);
        templateMakerModelConfig.setModels(modelInfoConfigList);
        TemplateMakerOutputConfig templateMakerOutputConfig =
                new TemplateMakerOutputConfig();
        long projectId = TemplateMaker.makeTemplate(meta,
                projectPath,
                originProjectPath,
                templateMakerFileConfig,
                templateMakerModelConfig,
                templateMakerOutputConfig,
                1767379836796596224L);
        System.out.println(projectId);
    }

    private static TemplateMakerFileConfig getTemplateMakerFileConfig(List<TemplateMakerFileConfig
            .FileInfoConfig> fileInfoConfigList) {
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(fileInfoConfigList);

        // 分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.
                FileGroupConfig();
        fileGroupConfig.setCondition("outputText");
        fileGroupConfig.setGroupKey("test");
        fileGroupConfig.setGroupName("测试分组");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);
        return templateMakerFileConfig;
    }

    @Test
    public void testMakeTemplateWithJson() {
        String configStr = ResourceUtil.readUtf8Str("meta.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    @Test
    public void testMakeSpringBootTemplate() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        long id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str("meta.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker2.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker3.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker4.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker5.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker6.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker7.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
//
//        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker8.json");
//        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
//        TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }
}