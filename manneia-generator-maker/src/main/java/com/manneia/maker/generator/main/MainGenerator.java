package com.manneia.maker.generator.main;

import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * @author manneia
 */
public class MainGenerator extends GenerateTemplate{
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
//        GenerateTemplate generateTemplate = new MainGenerator();
        GenerateTemplate generateTemplate = new ZipGenerator();
        generateTemplate.doGenerate();
    }

    @Override
    protected String buildDist(String outputPath, String sourceCopyDestPath, String shellOutputFilePath, String jarPath) {
        System.out.println("不需要了");
        return "";
    }
}
