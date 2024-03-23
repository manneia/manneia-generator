package com.manneia.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author lkx
 */
public class MetaManager {

    private static volatile Meta meta;

    public static Meta getMetaObject() {
        if (meta == null) {
            synchronized (Meta.class) {
                if (meta == null) {
                    meta = initMeta();

                }
            }
        }
        return meta;
    }

    private static Meta initMeta() {
//        String meatJson = ResourceUtil.readUtf8Str("meta.json");
        String meatJson = ResourceUtil.readUtf8Str("meta.json");
        Meta meta = JSONUtil.toBean(meatJson, Meta.class);
        // todo 校验配置参数是否合法, 处理默认值
        MetaValidator.doValidateAndFillDefaultValue(meta);
        return meta;
    }

}
