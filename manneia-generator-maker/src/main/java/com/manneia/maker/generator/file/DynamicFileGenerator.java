package com.manneia.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author lkx
 */
public class DynamicFileGenerator {

    /**
     * 生成文件
     *
     * @param inputPath  模板文件输入路径
     * @param outputPath 输出路径
     * @param model      数据模型
     * @throws IOException       IO异常
     * @throws TemplateException 模板异常
     */
    public static void doGenerate(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // 创建模板配置对象, 参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        File templateDir = new File(inputPath).getParentFile();
        // 指定模板文件所在路径
        configuration.setDirectoryForTemplateLoading(templateDir);
        // 指定字符集编码
        configuration.setDefaultEncoding("utf-8");
        // 创建模板对象,加载指定模板
        String templateName = new File(inputPath).getName();
        // 如果文件不存在,则创建目录
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }
        Template template = configuration.getTemplate(templateName, "utf-8");
        // 生成
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                Files.newOutputStream(Paths.get(outputPath)), StandardCharsets.UTF_8));
        template.process(model, out);
        out.close();
    }
}
