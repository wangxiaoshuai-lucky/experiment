package com.kelab.experiment.service;

import com.kelab.experiment.dal.domain.ExperimentHomeworkDomain;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentHomeworkInfo;
import com.kelab.info.experiment.query.ExperimentHomeworkQuery;

import java.util.List;

public interface ExperimentHomeworkService {

    /**
     * 分页查询作业
     */
    PaginationResult<ExperimentHomeworkInfo> queryHomeworkPage(Context context, ExperimentHomeworkQuery query);

    /**
     * 布置作业
     */
    void createHomework(Context context, ExperimentHomeworkDomain record);

    /**
     * 更新作业
     */
    void updateHomework(Context context, ExperimentHomeworkDomain record);

    /**
     * 删除作业
     */
    void deleteHomework(Context context, List<Integer> ids);

}
