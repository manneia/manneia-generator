package com.manneia.generateweb.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.manneia.generateweb.model.dto.Generator.GeneratorQueryRequest;
import com.manneia.generateweb.model.entity.Generator;
import com.manneia.generateweb.model.vo.GeneratorVo;

import javax.servlet.http.HttpServletRequest;

/**
 * 代码生成器服务
 *
 * @author lkx
 * 
 */
public interface GeneratorService extends IService<Generator> {

    /**
     * 校验
     *
     * @param generator 代码生成器对象
     * @param add 添加参数
     */
    void validGenerator(Generator generator, boolean add);

    /**
     * 获取查询条件
     *
     * @param generatorQueryRequest 代码生成器查询条件
     * @return 返回封装的查询条件
     */
    QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest);

    /**
     * 获取代码生成器封装
     *
     * @param generator 代码生成器
     * @param request 请求
     * @return 返回封装的代码生成器
     */
    GeneratorVo getGeneratorVo(Generator generator, HttpServletRequest request);

    /**
     * 分页获取代码生成器封装
     *
     * @param generatorPage 分页代码生成器
     * @param request 请求
     * @return 返回分页代码生成器
     */
    Page<GeneratorVo> getGeneratorVoPage(Page<Generator> generatorPage, HttpServletRequest request);
}
