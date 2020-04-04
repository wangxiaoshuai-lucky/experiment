package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentContestDomain;

import java.util.List;

public interface ExperimentContestRepo {

    List<ExperimentContestDomain> queryByClassId(Integer classId);

    void save(ExperimentContestDomain record);

    void update(ExperimentContestDomain record);

    void delete(List<Integer> ids);
}
