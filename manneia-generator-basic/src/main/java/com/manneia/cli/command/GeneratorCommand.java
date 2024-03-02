package com.manneia.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.manneia.generator.MainGenerator;
import com.manneia.model.MainTemplateConfig;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * @author lkx
 */
@CommandLine.Command(name = "generator", description = "生成器", mixinStandardHelpOptions = true)
@Data
public class GeneratorCommand implements Callable<Integer> {

    /**
     * 是否生成循环
     */
    @Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否开启循环",
            interactive = true, echo = true)
    private boolean loop;

    /**
     * 作者名称
     */
    @Option(names = {"-a", "--author"}, arity = "0..1", description = "作者名称",
            interactive = true, echo = true)
    private String author = "manneia";

    /**
     * 输出文本
     */
    @Option(names = {"-o", "--output"}, arity = "0..1", description = "输出文本",
            interactive = true, echo = true)
    private String outputText = "sum = ";

    @Override
    public Integer call() throws Exception {
        MainTemplateConfig config = new MainTemplateConfig();
        BeanUtil.copyProperties(this, config);
        System.out.println("配置信息: " + config);
        MainGenerator.doGenerator(config);
        return 0;
    }
}
