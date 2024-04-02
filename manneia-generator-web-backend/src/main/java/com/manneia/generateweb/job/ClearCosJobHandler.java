package com.manneia.generateweb.job;

import cn.hutool.core.util.StrUtil;
import com.manneia.generateweb.manager.CosManager;
import com.manneia.generateweb.mapper.GeneratorMapper;
import com.manneia.generateweb.model.entity.Generator;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时任务
 *
 * @author lkx
 */
@Component
@Slf4j
public class ClearCosJobHandler {

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorMapper generatorMapper;

    /**
     * 定时清除文件
     */
    @XxlJob("clearCosJobHandler")
    public void clearCosJobHandler() {
        log.info("clearCosJobHandler started");
        //每天清理所有无用的文件，包括用户上传的模板制作文件(generator_.make_template)、已删除的代码生成器对应的产物包文件(generator_dist)。
        // 1. 包括用户上传的模板制作文件(generator_make_template)
        cosManager.deleteDir("/generator_make_template/");
        // 2. 已删除的代码生成器对应的产物包文件(generator_dist)
        List<Generator> generatorList = generatorMapper.listDeleteGenerator();
        List<String> keyList = generatorList.stream().map(Generator::getDistPath)
                .filter(StrUtil::isNotBlank)
                .map(distPath -> distPath.substring(1))
                .collect(Collectors.toList());
        cosManager.deleteObjects(keyList);
        log.info("clearCosJobHandler end");
    }
}