package com.manneia.maker.generator.main;

import com.manneia.maker.generator.main.GenerateTemplate;

/**
 * @author lkx
 */
public class ZipGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputPath, String jarPath, String shellOutputPath, String sourceOutputPath) {
        String distPath = super.buildDist(outputPath, jarPath, shellOutputPath, sourceOutputPath);
        return super.buildZip(distPath);
    }
}
