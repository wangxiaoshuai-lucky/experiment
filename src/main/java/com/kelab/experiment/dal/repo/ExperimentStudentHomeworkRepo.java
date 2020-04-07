package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentStudentHomeworkDomain;
import com.kelab.info.experiment.query.ExperimentStudentHomeworkQuery;

import java.util.List;


public interface ExperimentStudentHomeworkRepo {

    List<ExperimentStudentHomeworkDomain> queryPage(ExperimentStudentHomeworkQuery query);

    Integer queryTotal(ExperimentStudentHomeworkQuery query);

    List<ExperimentStudentHomeworkDomain> queryAllByHomeworkIdsAndTargetIds(List<Integer> homeworkIds, List<Integer> targetIds);

    void save(ExperimentStudentHomeworkDomain record);

    void update(ExperimentStudentHomeworkDomain record);
}
