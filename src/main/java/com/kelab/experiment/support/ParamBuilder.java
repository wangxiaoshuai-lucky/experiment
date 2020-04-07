package com.kelab.experiment.support;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ParamBuilder {

    public static class Param extends HashMap<String, Object> {
        public Param param(String key, Object value) {
            put(key, value);
            return this;
        }
    }

    public static Param buildParam(Object... params) {
        Param param = new Param();
        if (params != null) {
            for (Object obj: params) {
                Map<String, Object> objectMap = JSON.parseObject(JSON.toJSONString(obj), Map.class);
                param.putAll(objectMap);
            }
        }
        return param;
    }
}
