package com.manneia.generateweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manneia.generateweb.model.entity.Generator;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author lkx
* @description 针对表【generator(代码生成器)】的数据库操作Mapper
* @createDate 2024-03-16 17:00:02
* @Entity com.manneia.generateweb.model.entity.Generator
*/
public interface GeneratorMapper extends BaseMapper<Generator> {
    @Select("select id,distPath from manneia_generator_db.generator where isDelete = 1")
    List<Generator> listDeleteGenerator();
}




