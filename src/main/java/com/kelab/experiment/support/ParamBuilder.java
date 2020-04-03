package com.kelab.experiment.support;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ParamBuilder {

    public static Map<String, Object> buildParam(Object... params) {
        Map<String, Object> contextParam = new HashMap<>();
        if (params != null) {
            for (Object obj: params) {
                Map<String, Object> objectMap = JSON.parseObject(JSON.toJSONString(obj), Map.class);
                contextParam.putAll(objectMap);
            }
        }
        return contextParam;
    }
}
