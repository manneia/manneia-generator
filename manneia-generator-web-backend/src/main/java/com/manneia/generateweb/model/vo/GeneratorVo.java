package com.manneia.generateweb.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.manneia.generateweb.meta.Meta;
import com.manneia.generateweb.model.entity.Generator;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 代码生成器视图
 *
 * @author lkx
 */
@Data
public class GeneratorVo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签列表(json 数组)
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置(json 字符串)
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置(json 字符串)
     */
    private Meta.ModelConfig modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人信息
     */
    private UserVo user;


    private static final long serialVersionUID = 1L;


    /**
     * 包装类转对象
     *
     * @param generatorVo
     * @return
     */
    public static Generator voToObj(GeneratorVo generatorVo) {
        if (generatorVo == null) {
            return null;
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorVo, generator);
        List<String> tagList = generatorVo.getTags();
        generator.setTags(JSONUtil.toJsonStr(tagList));

        Meta.FileConfig fileConfig = generatorVo.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorVo.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        return generator;
    }

    /**
     * 对象转包装类
     *
     * @param generator 代码生成器对象
     * @return 返回封装对象
     */
    public static GeneratorVo objToVo(Generator generator) {
        if (generator == null) {
            return null;
        }
        GeneratorVo generatorVo = new GeneratorVo();
        BeanUtils.copyProperties(generator, generatorVo);
        generatorVo.setTags(JSONUtil.toList(generator.getTags(), String.class));
        generatorVo.setFileConfig(JSONUtil.toBean(generator.getFileConfig(), Meta.FileConfig.class));
        generatorVo.setModelConfig(JSONUtil.toBean(generator.getModelConfig(), Meta.ModelConfig.class));
        return generatorVo;
    }
}
