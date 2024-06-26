package com.manneia.generateweb.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manneia.generateweb.annotation.AuthCheck;
import com.manneia.generateweb.common.BaseResponse;
import com.manneia.generateweb.common.DeleteRequest;
import com.manneia.generateweb.common.ErrorCode;
import com.manneia.generateweb.common.ResultUtils;
import com.manneia.generateweb.constant.UserConstant;
import com.manneia.generateweb.exception.BusinessException;
import com.manneia.generateweb.exception.ThrowUtils;
import com.manneia.generateweb.manager.CacheManager;
import com.manneia.generateweb.manager.CosManager;
import com.manneia.generateweb.model.dto.Generator.*;
import com.manneia.generateweb.model.entity.Generator;
import com.manneia.generateweb.model.entity.User;
import com.manneia.generateweb.model.vo.GeneratorVo;
import com.manneia.generateweb.service.GeneratorService;
import com.manneia.generateweb.service.UserService;
import com.manneia.maker.generator.main.GenerateTemplate;
import com.manneia.maker.generator.main.ZipGenerator;
import com.manneia.maker.meta.Meta;
import com.manneia.maker.meta.MetaValidator;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 代码生成器接口
 *
 * @author lkx
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private CacheManager cacheManager;


    // region 增删改查

    /**
     * 创建
     *
     * @param generatorAddRequest 代码生成器添加参数
     * @param request             请求
     * @return 返回id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        List<String> tags = generatorAddRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorAddRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorAddRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        generator.setStatus(0);
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除
     *
     * @param deleteRequest 删除参数
     * @param request       请求
     * @return 返回是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param generatorUpdateRequest 代码生成器更新参数
     * @return 返回更新是否成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorUpdateRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorUpdateRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return 返回代码生成器封装对象
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVo> getGeneratorVoById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVo(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest 代码生成器查询参数
     * @return 返回分页的代码生成器列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest 代码生成器查询参数
     * @param request               请求
     * @return 返回分页后的代码生成器
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVo>> listGeneratorVoByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                 HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVoPage(generatorPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param generatorQueryRequest 代码生成器查询参数
     * @param request               请求
     * @return 返回分页后的当前用户的代码生成器
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVo>> listMyGeneratorVoByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                   HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVoPage(generatorPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param generatorEditRequest 代码生成器编辑参数
     * @param request              请求
     * @return 返回是否编辑成功
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorEditRequest, generator);
        List<String> tags = generatorEditRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorEditRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorEditRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        if (result) {
            String cacheFileDir = getCacheFileDir(id);
            FileUtil.del(cacheFileDir);
        }
        return ResultUtils.success(result);
    }

    /**
     * 根据id下载代码生成器
     *
     * @param id       id
     * @param request  请求
     * @param response 响应
     * @throws IOException 异常
     */
    @GetMapping("/download")
    public void downloadGeneratorById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String filePath = generator.getDistPath();
        if (StrUtil.isBlank(filePath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }
        // 追踪事件日志
        log.info("用户{} 下载了 {}", loginUser, filePath);

        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filePath);

        String zipFilePath = getCacheFilePath(id, filePath);
        if (FileUtil.exist(zipFilePath)) {
            Files.copy(Paths.get(zipFilePath), response.getOutputStream());
            return;
        }
        COSObjectInputStream cosObjectInput = null;

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            COSObject cosObject = cosManager.getObject(filePath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            stopWatch.stop();
            System.out.println("下载耗时:" + stopWatch.getTotalTimeMillis());
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }


    /**
     * 快速分页获取列表（封装类）
     *
     * @param generatorQueryRequest 代码生成器查询参数
     * @param request               请求
     * @return 返回分页后的代码生成器
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/list/page/vo/fast")
    public BaseResponse<Page<GeneratorVo>> listGeneratorVoByPageFast(
            @RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<GeneratorVo> generatorVoPage;
        // 优先从缓存读取
        String pageCacheKey = getPageCacheKey(generatorQueryRequest);
        // 多级缓存
        Object cacheValue = cacheManager.get(pageCacheKey);
        if (cacheValue != null) {
            return ResultUtils.success((Page<GeneratorVo>) cacheValue);
        }
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Generator> queryWrapper = generatorService.
                getQueryWrapper(generatorQueryRequest);
        queryWrapper.select(
                "id",
                "name",
                "description",
                "tags",
                "picture",
                "status",
                "userId",
                "createTime",
                "updateTime");
        Page<Generator> generatorPage = generatorService.page(
                new Page<>(current, size), queryWrapper);
        generatorVoPage = generatorService.getGeneratorVoPage(
                generatorPage, request);
        // 写入多级缓存
        cacheManager.put(pageCacheKey, generatorVoPage);
        return ResultUtils.success(generatorVoPage);
    }

    /**
     * 使用代码生成器
     *
     * @param generatorUseRequest 代码生成器使用参数
     * @param request             请求
     * @param response            响应
     * @throws IOException 异常
     */
    @PostMapping("/use")
    public void useGenerator(@RequestBody GeneratorUseRequest generatorUseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取用户输入的请求参数
        long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId = {} 使用了生成器 id = {}", loginUser.getId(), id);
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 生成器的存储路径
        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }
        // 从对象存储下载代码生成器压缩包
        // 定义独立的工作空间
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/use/%s", projectPath, id);
        String zipFilePath = tempDirPath + "/dist.zip";
        // 新建文件
        if (!FileUtil.exist(zipFilePath)) {
            FileUtil.touch(zipFilePath);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            cosManager.download(distPath, zipFilePath);
            stopWatch.stop();
            System.out.println("下载耗时:" + stopWatch.getTotalTimeMillis());
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
        }
        // 解压压缩包
        stopWatch = new StopWatch();
        stopWatch.start();
        File unzipDistDir = ZipUtil.unzip(zipFilePath);
        stopWatch.stop();
        System.out.println("解压耗时:" + stopWatch.getTotalTimeMillis());
        // 将用户输入的参数写入json文件中
        stopWatch = new StopWatch();
        stopWatch.start();
        String dataModelFilePath = tempDirPath + "/dataModel.json";
        String jsonStr = JSONUtil.toJsonStr(dataModel);
        FileUtil.writeUtf8String(jsonStr, dataModelFilePath);
        stopWatch.stop();
        System.out.println("写入数据文件耗时:" + stopWatch.getTotalTimeMillis());        // 执行脚本
        // 找到脚本文件所在路径
        // 要注意, 如果不是windows系统,找 generator 文件而不是bat 文件
        File scriptFile = FileUtil.loopFiles(unzipDistDir, 2, null)
                .stream().filter(file -> file.isFile() && "generator".equals(file.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        // 添加可执行权限
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        File scriptDir = scriptFile.getParentFile();
//        String scriptAbsolutePath = scriptFile.getAbsolutePath().replace("\\", "/");
        String scriptAbsolutePath = scriptFile.getAbsolutePath();
        String[] commands = new String[]{scriptAbsolutePath, "json-generate", "--file=" + dataModelFilePath};
        // 创建ProcessBuilder并设置执行的命令
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        // 如果项目目录非空，则设置命令执行的目录
        processBuilder.directory(scriptDir);
        try {
            stopWatch = new StopWatch();
            stopWatch.start();
            // 启动进程
            Process process = processBuilder.start();
            // 获取进程的输出流，并读取输出信息
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line); // 打印命令执行的输出信息
            }
            int exitCode = process.waitFor();
            System.out.println("命令执行结束,退出码:" + exitCode);
            stopWatch.stop();
            System.out.println("执行命令耗时:" + stopWatch.getTotalTimeMillis());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行生成器脚本错误");
        }
        // 压缩得到的生成结果, 返回给前端
        String generatedPath = scriptDir.getAbsolutePath() + "/generated";
        String resultPath = tempDirPath + "/result.zip";
        stopWatch = new StopWatch();
        stopWatch.start();
        File resultFile = ZipUtil.zip(generatedPath, resultPath);
        stopWatch.stop();
        System.out.println("压缩耗时:" + stopWatch.getTotalTimeMillis());
        // 下载文件
        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + resultFile.getName());
        Files.copy(resultFile.toPath(), response.getOutputStream());
        // 清理工作空间临时文件
        CompletableFuture.runAsync(() -> FileUtil.del(tempDirPath));
    }

    /**
     * 制作代码生成器
     *
     * @param generatorMakeRequest 代码生成器制作参数
     * @param request              请求
     * @param response             响应
     * @throws IOException 异常
     */
    @PostMapping("/make")
    public void makeGenerator(@RequestBody GeneratorMakeRequest generatorMakeRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1) 输入参数
        Meta meta = generatorMakeRequest.getMeta();
        String zipFilePath = generatorMakeRequest.getZipFilePath();

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId = {} 在线制作生成器", loginUser.getId());

        // 2) 创建独立的工作空间，下载压缩包到本地
        String projectPath = System.getProperty("user.dir");
        String id = IdUtil.getSnowflakeNextId() + RandomUtil.randomString(6);
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);
        String localZipFilePath = tempDirPath + "/project.zip";

        if (!FileUtil.exist(localZipFilePath)) {
            FileUtil.touch(localZipFilePath);
        }

        // 下载文件
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            cosManager.download(zipFilePath, localZipFilePath);
            stopWatch.stop();
            System.out.println("下载文件：" + stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩包下载失败");
        }

        // 3）解压，得到项目模板文件
        File unzipDistDir = ZipUtil.unzip(localZipFilePath);

        // 4）构造 meta 对象和生成器的输出路径
        String sourceRootPath = unzipDistDir.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        // 校验和处理默认值
        MetaValidator.doValidateAndFillDefaultValue(meta);
        String outputPath = tempDirPath + "/generated/" + meta.getName();
        // 5）调用 maker 方法制作生成器
        GenerateTemplate generateTemplate = new ZipGenerator();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            generateTemplate.doGenerate(meta, outputPath);
            stopWatch.stop();
            System.out.println("制作耗时：" + stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "制作失败");
        }

        // 6）下载制作好的生成器压缩包
        String suffix = "-dist.zip";
        String zipFileName = meta.getName() + suffix;
        // 生成器压缩包的绝对路径
        String distZipFilePath = outputPath + suffix;

        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
        Files.copy(Paths.get(distZipFilePath), response.getOutputStream());

        // 7）清理工作空间的文件
        CompletableFuture.runAsync(() -> FileUtil.del(tempDirPath));
    }

    /**
     * 缓存代码生成器
     *
     * @param generatorCacheRequest 缓存生成器请求参数
     */
    @PostMapping("/cache")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void cacheGenerator(@RequestBody GeneratorCacheRequest generatorCacheRequest) {
        if (generatorCacheRequest == null || generatorCacheRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = generatorCacheRequest.getId();
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }
        // 追踪事件日志
        log.info("下载了 {}", distPath);
        String zipFilePath = getCacheFilePath(id, distPath);
        // 新建文件
        if (!FileUtil.exist(zipFilePath)) {
            FileUtil.touch(zipFilePath);
        }
        try {
            cosManager.download(distPath, zipFilePath);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
        }
    }

    /**
     * 获取缓存路径
     *
     * @param id       生成器id
     * @param distPath 生成器路径
     * @return 返回缓存路径
     */
    public String getCacheFilePath(long id, String distPath) {
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/cache/%s", projectPath, id);
        return tempDirPath + distPath;
    }

    /**
     * 获取分页缓存 key
     *
     * @param generatorQueryRequest 代码生成器查询参数
     * @return 返回key
     */
    public static String getPageCacheKey(GeneratorQueryRequest generatorQueryRequest) {
        String jsonStr = JSONUtil.toJsonStr(generatorQueryRequest);
        // 请求参数编码
        String base64 = Base64Encoder.encode(jsonStr);
        return "generator:page:" + base64;
    }

    /**
     * 获取缓存文件所在的目录
     *
     * @param id 生成器 id
     * @return 返回目录
     */
    public String getCacheFileDir(long id) {
        String projectPath = System.getProperty("user.dir");
        return String.format("%s/.temp/cache/%s", projectPath, id);
    }
}
