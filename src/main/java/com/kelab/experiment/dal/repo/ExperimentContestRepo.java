package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentContestQuery;

import java.util.List;

public interface ExperimentContestRepo {

    List<ExperimentContestDomain> queryContest(Context context, ExperimentContestQuery query, boolean isFillTitle);

    List<ExperimentContestDomain> queryAllByClassId(Context context, Integer classId, boolean isFillTitle);

    Integer queryTotal(ExperimentContestQuery query);

    List<ExperimentContestDomain> queryByIds(Context context, List<Integer> ids, boolean isFillTitle);

    void save(ExperimentContestDomain record);

    void update(ExperimentContestDomain record);

    void delete(List<Integer> ids);
}
