package com.manneia.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.manneia.maker.meta.Meta;
import com.manneia.maker.meta.enums.FileGenerateTypeEnum;
import com.manneia.maker.meta.enums.FileTypeEnum;
import com.manneia.maker.template.model.FileFilterConfig;
import com.manneia.maker.template.model.TemplateMakerConfig;
import com.manneia.maker.template.model.TemplateMakerFileConfig;
import com.manneia.maker.template.model.TemplateMakerModelConfig;
import com.manneia.maker.template.model.enums.FileFilterRangeEnum;
import com.manneia.maker.template.model.enums.FileFilterRuleEnum;
import com.manneia.maker.utils.Utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 *
 * @author lkx
 */
public class TemplateMaker {

    /**
     * 制作模板
     * @param templateMakerConfig 模板制作配置
     * @return 返回文件id
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig) {
        Meta meta = templateMakerConfig.getMeta();
        Long id = templateMakerConfig.getId();
        String projectPath = templateMakerConfig.getProjectPath();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig fileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig modelConfig = templateMakerConfig.getModelConfig();

        return makeTemplate(meta, projectPath, originProjectPath, fileConfig, modelConfig, id);
    }

    /**
     * 制作模板
     *
     * @param newMeta                  元信息对象
     * @param projectPath              项目根目录
     * @param originProjectPath        原始项目路径
     * @param templateMakerFileConfig  项目模板文件配置
     * @param templateMakerModelConfig 模型配置
     * @param id                       文件id
     * @return 返回文件id
     */
    public static long makeTemplate(Meta newMeta,
                                    String projectPath,
                                    String originProjectPath,
                                    TemplateMakerFileConfig templateMakerFileConfig,
                                    TemplateMakerModelConfig templateMakerModelConfig,
                                    Long id) {
        // 没有id 则生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }
        // 业务逻辑...
        // 指定原始项目路径

        // 复制目录
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(tempDirPath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }
        // 一、输入信息
        // 1. 项目的基本信息

        // 2. 项目的输入文件
        // 输入文件为目录
        String sourceRootPath = templatePath + File.separator + FileUtil.
                getLastPathEle(Paths.get(originProjectPath)).toString();
        // window系统需要对路径进行转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList =
                templateMakerFileConfig.getFiles();
        // 遍历文件
        // 处理模型信息
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        // 转换为配置文件接受的modelInfo对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());
        List<Meta.ModelConfig.ModelInfo> newModelInfoList;
        // 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig
                .getModelGroupConfig();
        if (modelGroupConfig != null) {
            String condition = modelGroupConfig.getCondition();
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            groupModelInfo.setGroupKey(groupKey);
            groupModelInfo.setGroupName(groupName);
            groupModelInfo.setCondition(condition);
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList = new ArrayList<>();
            newModelInfoList.add(groupModelInfo);
        } else {
            // 不分组,添加所有的模型信息到列表
            newModelInfoList = new ArrayList<>(inputModelInfoList);
        }

        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList) {
            String inputFilePath = fileInfoConfig.getPath();
            String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;
            // 传入绝对路径
            // 得到过滤后的文件列表
            List<File> fileList = FileFilter.doFilter(fileInfoConfig.
                    getFilterConfigList(), inputFileAbsolutePath);
            // 不处理已生成的FTL 模板文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .collect(Collectors.toList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig,
                        sourceRootPath,
                        file);
                newFileInfoList.add(fileInfo);
            }
        }

        // 如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.
                getFileGroupConfig();
        if (fileGroupConfig != null) {
            Meta.FileConfig.FileInfo groupFileInfo = getFileInfo(fileGroupConfig, newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }

        // 三、生成配置文件
        String metaOutputPath = templatePath + File.separator + "meta.json";
        Meta.FileConfig fileConfig = new Meta.FileConfig();
        fileConfig.setSourceRootPath(sourceRootPath);
        // 如果已有配置文件,表示不是第一次生成,则在原有配置的基础上进行修改
        if (FileUtil.exist(metaOutputPath)) {
            newMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
//            BeanUtil.copyProperties(newMeta, newMeta, CopyOptions.create().ignoreNullValue());
            // 1. 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            fileInfoList.addAll(newFileInfoList);
            modelInfoList.addAll(newModelInfoList);
            // 去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
            // 2. 生成元信息文件
        } else {
            List<Meta.FileConfig.FileInfo> files = new ArrayList<>();
            fileConfig.setFiles(files);
            files.addAll(newFileInfoList);
            // 1. 构造配置参数对象
            newMeta.setFileConfig(fileConfig);
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            newMeta.setModelConfig(modelConfig);
            modelInfoList.addAll(newModelInfoList);
        }
        // 2. 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        // 4. 项目的输出规则
        return id;
    }

    private static Meta.FileConfig.FileInfo getFileInfo(TemplateMakerFileConfig.FileGroupConfig
                                                                fileGroupConfig,
                                                        List<Meta.FileConfig.FileInfo>
                                                                newFileInfoList) {
        String condition = fileGroupConfig.getCondition();
        String groupKey = fileGroupConfig.getGroupKey();
        String groupName = fileGroupConfig.getGroupName();
        // 新增分组配置
        Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
        groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
        groupFileInfo.setCondition(condition);
        groupFileInfo.setGroupKey(groupKey);
        groupFileInfo.setGroupName(groupName);
        // 文件全放到一个分组内
        groupFileInfo.setFiles(newFileInfoList);
        return groupFileInfo;
    }

    /**
     * 制作模板文件
     *
     * @param templateMakerModelConfig 模型配置
     * @param sourceRootPath           源文件路径
     * @param inputFile                输入文件
     * @return 返回文件配置
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(TemplateMakerModelConfig
                                                                     templateMakerModelConfig,
                                                             String sourceRootPath,
                                                             File inputFile) {
        // 注意一定是相对路径
        String fileInputAbsolutePath = inputFile.getAbsolutePath().
                replace(sourceRootPath + "/", "");
        fileInputAbsolutePath = fileInputAbsolutePath.replaceAll("\\\\", "/");
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";
        // 3. 项目的模型参数信息
        // 二、使用字符串替换,生成模板文件
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";
        // 如果已有模板文件,表示不是第一次生成,则在原有模板的基础上再挖坑
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig
                .getModelGroupConfig();
        String fileContent;
        // 如果已有模板文件,表示不是第一次生成,则在原有模板的基础上再挖坑
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if (hasTemplateFile) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig :
                templateMakerModelConfig.getModels()) {
            String filedName = modelInfoConfig.getFieldName();
            // 不是分组
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", filedName);
            } else {
                // 是分组
                String groupKey = modelGroupConfig.getGroupKey();
                // 注意多挖一层
                replacement = String.format("${%s.%s}", groupKey, filedName);
            }
            // 多次替换
            newFileContent = StrUtil.replace(newFileContent,
                    modelInfoConfig.getReplaceText(),
                    replacement);
        }
        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        // 是否更改了文件内容
        boolean contentEquals = newFileContent.equals(fileContent);
        // 之前不存在模板文件,并且这次替换没有修改文件的内容
        if (!hasTemplateFile) {
            // 和原文件内容一致,表示没有修改. 静态生成
            if (contentEquals) {
                // 输入路径 = 输出路径
                fileInfo.setOutputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            } else {
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        } else if (!contentEquals) {
            // 有模板文件,并且增加了新的配置, 更新模板文件
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return fileInfo;
    }

    /**
     * 文件配置去重
     *
     * @param fileInfoList 文件配置
     * @return 返回去重后的文件配置
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.
            FileConfig.FileInfo> fileInfoList) {
        // 策略: 同分组内文件 merge 不同分组文件 保留
        // 1. 有分组的,以组为单位划分
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));
        // 2. 同组内的文件配置合并
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergeFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry :
                groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList
                    .stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath,
                            o -> o, (e, r) -> r)).values());
            // 使用新的group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergeFileInfoMap.put(groupKey, newFileInfo);
        }
        // 3. 将文件分组添加到结果列表
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(
                groupKeyMergeFileInfoMap.values()
        );
        // 4. 将未分组的文件添加到结果列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath,
                        o -> o, (e, r) -> r)).values()));
        return resultList;
    }

    /**
     * 模型配置去重
     *
     * @param modelInfoList 文件配置
     * @return 返回去重后的文件配置
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.
            ModelConfig.ModelInfo> modelInfoList) {
        // 策略: 同分组内模型 merge 不同分组保留
        // 1. 有分组的,以组为单位划分
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));
        // 2. 同组内的文件配置合并
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergeModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry :
                groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList
                    .stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,
                            o -> o, (e, r) -> r)).values());
            // 使用新的group配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergeModelInfoMap.put(groupKey, newModelInfo);
        }
        // 3. 将模型分组添加到结果列表
        List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(
                groupKeyMergeModelInfoMap.values()
        );
        // 4. 将未分组的文件添加到结果列表
        List<Meta.ModelConfig.ModelInfo> noGroupModelInfoList = modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupModelInfoList.stream()
                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,
                        o -> o, (e, r) -> r)).values()));
        return resultList;
    }
}
