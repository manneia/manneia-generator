package com.manneia.generateweb.vertx;

import com.manneia.generateweb.manager.CacheManager;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * vertx 操作类
 *
 * @author lkx
 */
@Component
public class VertxManager {

    @Resource
    private CacheManager cacheManager;

    @PostConstruct
    public void init() {
        Vertx vertx = Vertx.vertx();
        MainVerticle mainVerticle = new MainVerticle(cacheManager);
        vertx.deployVerticle(mainVerticle);
    }
}