package com.manneia.maker.generator.main;

import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * @author manneia
 */
public class MainGenerator extends GenerateTemplate{
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }

    @Override
    protected void buildDist(String outputPath, String sourceCopyDestPath, String shellOutputFilePath, String jarPath) {
        System.out.println("不需要了");
    }
}
