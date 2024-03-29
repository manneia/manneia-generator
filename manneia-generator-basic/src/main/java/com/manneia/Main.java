package com.manneia;

import com.manneia.generator.MainGenerator;
import com.manneia.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * @author lkx
 */
public class Main {
    public static void main(String[] args) throws TemplateException, IOException {
        //        args = new String[] {"generator", "-l", "-a", "-o"};
        //        args = new String[]{"config"};
        //        args = new String[]{"list"};
//        CommandExecutor commandExecutor = new CommandExecutor();
//        commandExecutor.doExecute(args);
        MainGenerator.doGenerator(new MainTemplateConfig());
    }
}