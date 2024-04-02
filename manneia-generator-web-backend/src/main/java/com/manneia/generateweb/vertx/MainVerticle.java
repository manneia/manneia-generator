package com.manneia.generateweb.vertx;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manneia.generateweb.common.ResultUtils;
import com.manneia.generateweb.controller.GeneratorController;
import com.manneia.generateweb.manager.CacheManager;
import com.manneia.generateweb.model.dto.Generator.GeneratorQueryRequest;
import com.manneia.generateweb.model.vo.GeneratorVo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;

/**
 * @author lkx
 */
public class MainVerticle extends AbstractVerticle {

    private final CacheManager cacheManager;

    public MainVerticle(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(Promise<Void> startPromise) throws Exception {
        // create the http server 创建http服务器
        vertx.createHttpServer()
                // handle every request using the router
                .requestHandler(req -> {
                    HttpMethod httpMethod = req.method();
                    String path = req.path();
                    // 分页获取生成器
                    if (HttpMethod.POST.equals(httpMethod) &&
                            "/generator/page".equals(path)) {
                        // 设置请求体处理器 Sets the request body handler
                        req.handler(buffer -> {
                            // get the request body data 获取请求体数据
                            String requestBody = buffer.toString();
                            GeneratorQueryRequest generatorQueryRequest = JSONUtil
                                    .toBean(requestBody, GeneratorQueryRequest.class);
                            // 处理数据
                            String cacheKey = GeneratorController
                                    .getPageCacheKey(generatorQueryRequest);
                            // 设置响应头 set response header
                            HttpServerResponse response = req.response();
                            response.putHeader("Content-Type", "application/json");
                            // 本地缓存
                            Object cacheValue = cacheManager.get(cacheKey);
                            if (cacheValue != null) {
                                // 返回json响应
                                response.end(JSONUtil.toJsonStr(ResultUtils
                                        .success((Page<GeneratorVo>) cacheValue)));
                                return;
                            }
                            response.end("");
                        });
                    }
                })
                // start listening
                .listen(8888)
                .onSuccess(server -> {
                    System.out.println(
                            "http server started on port" +
                                    server.actualPort());
                });
    }
}