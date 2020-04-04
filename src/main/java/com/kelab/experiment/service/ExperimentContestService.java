package com.kelab.experiment.service;

import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentContestInfo;

import java.util.List;

public interface ExperimentContestService {

    /**
     * 查询一个班级下所有的实验
     */
    PaginationResult<ExperimentContestInfo> queryByClassId(Context context, Integer classId);


    /**
     * 更新实验
     */
    void updateContest(Context context, ExperimentContestDomain domain);

    /**
     * 删除实验
     */
    void deleteContest(Context context, List<Integer> ids);
}
