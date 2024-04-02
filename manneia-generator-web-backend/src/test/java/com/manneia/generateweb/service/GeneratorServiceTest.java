package com.manneia.generateweb.service;

import com.manneia.generateweb.model.entity.Generator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GeneratorServiceTest {
    @Resource
    private GeneratorService generatorService;

    @Test
    public void testInsert(){
        Generator generator = generatorService.getById(1771862404599152641L);
        for (int i = 0; i < 100000; i++) {
            generator.setId(null);
            generatorService.save(generator);
        }
    }
}