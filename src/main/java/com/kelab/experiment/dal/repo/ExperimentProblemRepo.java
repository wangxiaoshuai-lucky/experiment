package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentProblemDomain;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentProblemQuery;

import java.util.List;
import java.util.Map;


public interface ExperimentProblemRepo {

    /**
     * 带缓存
     */
    List<ExperimentProblemDomain> queryPage(Context context, ExperimentProblemQuery query, boolean isFillTitle);


    Integer queryTotal(ExperimentProblemQuery query);

    /**
     * 带缓存
     */
    Map<Integer, List<ExperimentProblemDomain>> queryAllByContestIds(Context context, List<Integer> contestIds, boolean isFillTitle);


    /**
     * 全量替换，先删除后添加
     */
    void saveList(List<ExperimentProblemDomain> records);
}
