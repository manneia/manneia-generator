package com.manneia.generateweb.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manneia.generateweb.common.ErrorCode;
import com.manneia.generateweb.constant.CommonConstant;
import com.manneia.generateweb.exception.BusinessException;
import com.manneia.generateweb.exception.ThrowUtils;
import com.manneia.generateweb.mapper.GeneratorMapper;
import com.manneia.generateweb.model.dto.Generator.GeneratorQueryRequest;
import com.manneia.generateweb.model.entity.Generator;
import com.manneia.generateweb.model.entity.User;
import com.manneia.generateweb.model.vo.GeneratorVo;
import com.manneia.generateweb.model.vo.UserVo;
import com.manneia.generateweb.service.GeneratorService;
import com.manneia.generateweb.service.UserService;
import com.manneia.generateweb.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 代码生成器服务实现
 *
 * @author lkx
 */
@Service
@Slf4j
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator> implements GeneratorService {

    @Resource
    private UserService userService;

    @Override
    public void validGenerator(Generator generator, boolean add) {
        if (generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = generator.getName();
        String description = generator.getDescription();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {
        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }
        Long id = generatorQueryRequest.getId();
        Long notId = generatorQueryRequest.getNotId();
        Long userId = generatorQueryRequest.getUserId();
        String searchText = generatorQueryRequest.getSearchText();
        String title = generatorQueryRequest.getTitle();
        List<String> tags = generatorQueryRequest.getTags();
        List<String> orTags = generatorQueryRequest.getOrTags();
        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        String basePackage = generatorQueryRequest.getBasePackage();
        String version = generatorQueryRequest.getVersion();
        String author = generatorQueryRequest.getAuthor();
        String distPath = generatorQueryRequest.getDistPath();
        Integer status = generatorQueryRequest.getStatus();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId),"userId",userId);
        queryWrapper.eq(StringUtils.isNotBlank(basePackage),"basePackage", basePackage);
        queryWrapper.eq(StringUtils.isNotBlank(version),"version", version);
        queryWrapper.eq(StringUtils.isNotBlank(author),"author", author);
        queryWrapper.eq(StringUtils.isNotBlank(distPath),"distPath", distPath);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status),"status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public GeneratorVo getGeneratorVo(Generator generator, HttpServletRequest request) {
        GeneratorVo generatorVo = GeneratorVo.objToVo(generator);
        // 1. 关联查询用户信息
        Long userId = generator.getId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVo userVo = userService.getUserVo(user);
        generatorVo.setUser(userVo);
        return generatorVo;
    }

    @Override
    public Page<GeneratorVo> getGeneratorVoPage(Page<Generator> generatorPage, HttpServletRequest request) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVo> generatorVoPage = new Page<>(generatorPage.getCurrent(), generatorPage.getSize(), generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVoPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<GeneratorVo> generatorVoList = generatorList.stream().map(generator -> {
            GeneratorVo generatorVo = GeneratorVo.objToVo(generator);
            Long userId = generator.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVo.setUser(userService.getUserVo(user));
            return generatorVo;
        }).collect(Collectors.toList());
        generatorVoPage.setRecords(generatorVoList);
        return generatorVoPage;
    }

}




