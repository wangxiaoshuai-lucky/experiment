package com.kelab.experiment.service;

import com.kelab.experiment.dal.domain.ExperimentClassDomain;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentClassInfo;
import com.kelab.info.experiment.query.ExperimentClassQuery;

import java.util.List;

public interface ExperimentClassService {

    /**
     * 分页查询，管理员、教师查看
     */
    PaginationResult<ExperimentClassInfo> queryPage(Context context, ExperimentClassQuery query);

    /**
     * 创建班级
     */
    void createExperimentClass(Context context, ExperimentClassDomain domain);

    /**
     * 创建班级
     */
    void updateExperimentClass(Context context, ExperimentClassDomain domain);

    /**
     * 删除班级
     */
    void deleteExperimentClass(Context context, List<Integer> ids);
}
