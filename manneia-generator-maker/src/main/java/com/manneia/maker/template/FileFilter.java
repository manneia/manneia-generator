package com.manneia.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.manneia.maker.template.model.FileFilterConfig;
import com.manneia.maker.template.model.enums.FileFilterRangeEnum;
import com.manneia.maker.template.model.enums.FileFilterRuleEnum;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lkx
 */
public class FileFilter {

    /**
     * 单个文件过滤
     *
     * @param filterConfigList 过滤配置集合
     * @param file             过滤文件
     * @return 返回是否保留
     */
    public static boolean doSingleFilter(List<FileFilterConfig> filterConfigList, File file) {
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);
        // 所有过滤器校验结束后的结果
        boolean result = true;
        if (CollUtil.isEmpty(filterConfigList)) {
            return true;
        }
        for (FileFilterConfig fileFilterConfig : filterConfigList) {
            if (fileFilterConfig == null) {
                return false;
            }
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();
            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if (fileFilterRangeEnum == null) {
                continue;
            }
            // 要过滤的内容
            String content = fileName;
            switch (fileFilterRangeEnum) {
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }
            FileFilterRuleEnum fileFilterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if (fileFilterRuleEnum == null) {
                continue;
            }
            switch (fileFilterRuleEnum) {
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                default:
            }
        }
        return result;
    }

    /**
     * 过滤某个文件或目录,返回文件列表
     *
     * @param filterConfigList 过滤配置集合
     * @param filePath         过滤文件路径
     * @return 返回过滤后的文件
     */
    public static List<File> doFilter(List<FileFilterConfig> filterConfigList, String filePath) {
        // 根据路径获取所有文件
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream().filter(file ->
                        doSingleFilter(filterConfigList, file))
                .collect(Collectors.toList());
    }
}
