package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentStudentHomeworkDomain;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentStudentHomeworkQuery;

import java.util.List;


public interface ExperimentStudentHomeworkRepo {

    List<ExperimentStudentHomeworkDomain> queryPage(Context context,
                                                    ExperimentStudentHomeworkQuery query,
                                                    boolean isFillSubmitInfo);

    List<ExperimentStudentHomeworkDomain> queryByIds(Context context,
                                                     List<Integer> ids,
                                                     boolean isFillSubmitInfo);

    Integer queryTotal(ExperimentStudentHomeworkQuery query);

    List<ExperimentStudentHomeworkDomain> queryAllByHomeworkIdsAndTargetIds(Context context,
                                                                            List<Integer> homeworkIds,
                                                                            List<Integer> targetIds,
                                                                            boolean isFillSubmitInfo);

    void save(ExperimentStudentHomeworkDomain record);

    void update(ExperimentStudentHomeworkDomain record);

    void deleteByHomeworkId(Integer homeworkId);
}
