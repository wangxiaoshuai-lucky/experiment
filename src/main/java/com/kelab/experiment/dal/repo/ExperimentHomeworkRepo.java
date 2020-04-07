package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentHomeworkDomain;
import com.kelab.info.experiment.query.ExperimentHomeworkQuery;

import java.util.List;

public interface ExperimentHomeworkRepo {

    List<ExperimentHomeworkDomain> queryPage(ExperimentHomeworkQuery query);


    List<ExperimentHomeworkDomain> queryByIds(List<Integer> ids);

    Integer queryTotal(ExperimentHomeworkQuery query);

    List<ExperimentHomeworkDomain> queryAllByClassId(Integer classId);

    void save(ExperimentHomeworkDomain record);

    void update(ExperimentHomeworkDomain record);

    void delete(List<Integer> ids);
}
