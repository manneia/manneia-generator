package com.manneia.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.manneia.maker.meta.enums.FileGenerateTypeEnum;
import com.manneia.maker.meta.enums.FileTypeEnum;
import com.manneia.maker.meta.enums.ModelTypeEnum;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 元信息校验
 *
 * @author lkx
 */
public class MetaValidator {

    public static void doValidateAndFillDefaultValue(Meta meta) {
        // 基础信息校验
        validAndFillMetaRoot(meta);

        // 文件配置信息校验
        validAndFillMetaFileConfig(meta);

        // 模板配置信息校验
        validAndFillMetaModelConfig(meta);

    }

    /**
     * 基础信息校验和默认值
     *
     * @param meta 元信息
     */
    private static void validAndFillMetaRoot(Meta meta) {
        // 项目名称
        String name = StrUtil.blankToDefault(meta.getName(), "my-generator");
        // 项目描述
        String description = StrUtil.emptyToDefault(meta.getDescription(), "我的模板代码生成器");
        // 项目基础包名
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.manneia");
        // 项目版本
        String version = StrUtil.emptyToDefault(meta.getVersion(), "1.0");
        // 项目作者
        String author = StrUtil.emptyToDefault(meta.getAuthor(), "manneia");
        // 项目创建时间
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());

        meta.setName(name);
        meta.setDescription(description);
        meta.setBasePackage(basePackage);
        meta.setVersion(version);
        meta.setAuthor(author);
        meta.setCreateTime(createTime);
    }

    /**
     * 文件配置信息校验和默认值
     *
     * @param meta 元信息
     */
    private static void validAndFillMetaFileConfig(Meta meta) {
        // fileConfig 校验和默认值
        Meta.FileConfig fileConfig = meta.getFileConfig();

        if (fileConfig == null) {
            return;
        }
        // 必填
        String sourceRootPath = fileConfig.getSourceRootPath();
        // inputRootPath .source + sourceRootPath的最后一个层级目录
        String inputRootPath = fileConfig.getInputRootPath();
        // outputRootPath 默认为当前路径下的generated
        String outputRootPath = fileConfig.getOutputRootPath();
        // 文件类型
        String fileConfigType = fileConfig.getType();

        List<Meta.FileConfig.FileInfo> fileInfoList = fileConfig.getFiles();

        if (StrUtil.isBlank(sourceRootPath)) {
            throw new MetaException("未填写 sourceRootPath");
        }

        String defaultInputRootPath = StrUtil.emptyToDefault(inputRootPath, ".source/" +
                FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString());
        meta.getFileConfig().setInputRootPath(defaultInputRootPath);

        String defaultOutputRootPath = StrUtil.emptyToDefault(outputRootPath, "generated");
        meta.getFileConfig().setOutputRootPath(defaultOutputRootPath);

        String defaultType = StrUtil.emptyToDefault(fileConfigType, FileTypeEnum.DIR.getValue());
        meta.getFileConfig().setType(defaultType);

        if (CollUtil.isEmpty(fileInfoList)) {
            return;
        }
        fileInfoList.forEach(fileInfo -> {
            // 必填
            String inputPath = fileInfo.getInputPath();
            // outputPath 默认等于 inputPath
            String outputPath = fileInfo.getOutputPath();
            // type: inputPath 有文件后缀（如.java）为 file，否则为 dir
            String type = fileInfo.getType();
            // 如果文件结尾不为 .ftl，默认为static，否则为 dynamic
            String generateType = fileInfo.getGenerateType();

            if (FileTypeEnum.GROUP.getValue().equals(type)) {
                return;
            }
            // inputPath 必填
            if (StrUtil.isBlank(inputPath)) {
                throw new MetaException("未填写 inputPath");
            }
            // outputPath 默认等于 inputPath
            String defaultOutputPath = StrUtil.emptyToDefault(outputPath, inputPath);
            fileInfo.setOutputPath(defaultOutputPath);
            // 无文件后缀则默认为 dir,否则为 file
            String defaultFileConfigType = StrUtil.blankToDefault(type,
                    StrUtil.isBlank(FileUtil.getSuffix(inputPath)) ?
                            FileTypeEnum.DIR.getValue() :
                            FileTypeEnum.FILE.getValue());
            fileInfo.setType(defaultFileConfigType);
            // 无 ftl后缀,则默认为 static,否则为 dynamic
            String defaultGenerateType = StrUtil.blankToDefault(generateType,
                    inputPath.endsWith(".ftl") ?
                            FileGenerateTypeEnum.DYNAMIC.getValue() :
                            FileGenerateTypeEnum.STATIC.getValue());
            fileInfo.setGenerateType(defaultGenerateType);
        });
    }

    /**
     * 模板配置信息校验和默认值
     *
     * @param meta 元信息
     */
    private static void validAndFillMetaModelConfig(Meta meta) {
        // modelConfig 校验和默认值
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            return;
        }
        List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
        if (CollUtil.isEmpty(modelInfoList)) {
            return;
        }
        for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList) {
            String fieldName = modelInfo.getFieldName();
            String type = modelInfo.getType();
            String groupKey = modelInfo.getGroupKey();
            String description = modelInfo.getDescription();
            StrUtil.emptyToDefault(description, "请输入" + fieldName);
            // 为group 不校验
            if (StrUtil.isNotEmpty(groupKey)) {
                // 生成中间参数
                List<Meta.ModelConfig.ModelInfo> subModelInfoList = modelInfo.getModels();
                String allArgsStr = subModelInfoList.stream().map(subModelInfo ->
                                String.format("\"--%s\"", subModelInfo.getFieldName()))
                        .collect(Collectors.joining(", "));
                modelInfo.setAllArgsStr(allArgsStr);
                continue;
            }

            if (StrUtil.isBlank(fieldName)) {
                throw new MetaException("未填写 filedName");
            }

            if (StrUtil.isBlankIfStr(type)) {
                modelInfo.setType(ModelTypeEnum.MODEL_STRING.getValue());
            }
        }

    }
}
