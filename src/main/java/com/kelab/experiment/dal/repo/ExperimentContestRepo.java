package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.info.experiment.query.ExperimentContestQuery;

import java.util.List;

public interface ExperimentContestRepo {

    List<ExperimentContestDomain> queryContest(ExperimentContestQuery query);

    Integer queryTotal(ExperimentContestQuery query);

    List<ExperimentContestDomain> queryByIds(List<Integer> ids);

    void save(ExperimentContestDomain record);

    void update(ExperimentContestDomain record);

    void delete(List<Integer> ids);
}
