package com.manneia.maker.template;

import cn.hutool.core.io.FileUtil;
import com.manneia.maker.meta.Meta;
import com.manneia.maker.template.model.TemplateMakerFileConfig;
import com.manneia.maker.template.model.TemplateMakerModelConfig;
import com.manneia.maker.utils.Utils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
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
        long projectId = TemplateMaker.makeTemplate(meta,
                projectPath,
                originProjectPath,
                templateMakerFileConfig,
                templateMakerModelConfig,
                1767379836796596224L);
        System.out.println(projectId);
    }

    private static TemplateMakerFileConfig getTemplateMakerFileConfig(String fileInputPath2, TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.
                FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath2);
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList =
                Arrays.asList(fileInfoConfig,
                        fileInfoConfig1);
        return getTemplateMakerFileConfig(fileInfoConfigList);
    }

    private static TemplateMakerFileConfig getTemplateMakerFileConfig(List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList) {
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

    private static TemplateMakerModelConfig getTemplateMakerModelConfig() {
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig
                .ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
        // 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig
                .ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig
                .ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays
                .asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);
        return templateMakerModelConfig;
    }
}