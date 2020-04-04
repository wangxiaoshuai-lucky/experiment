package com.kelab.experiment.service.impl;

import com.kelab.info.base.query.BaseQuery;

import java.util.ArrayList;
import java.util.List;

public class CommonService {

    static List<Integer> totalIds(BaseQuery query) {
        List<Integer> ids = new ArrayList<>();
        if (query == null) {
            return ids;
        }
        if (query.getId() != null) {
            ids.add(query.getId());
        }
        if (query.getIds() != null) {
            ids.addAll(query.getIds());
        }
        return ids;
    }
}
