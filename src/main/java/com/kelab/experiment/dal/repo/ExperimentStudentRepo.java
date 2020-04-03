package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentStudentQuery;

import java.util.List;

public interface ExperimentStudentRepo {

    List<ExperimentStudentDomain> queryPage(Context context, ExperimentStudentQuery query, boolean isFillUserInfo);

    Integer queryTotal(ExperimentStudentQuery query);

    ExperimentStudentDomain queryByUserIdAndClassId(Integer userId, Integer classId);

    List<ExperimentStudentDomain> queryByIds(Context context, List<Integer> ids, boolean isFillUserInfo);

    void save(ExperimentStudentDomain record);

    void update(ExperimentStudentDomain record);

    void delete(List<Integer> ids);
}
