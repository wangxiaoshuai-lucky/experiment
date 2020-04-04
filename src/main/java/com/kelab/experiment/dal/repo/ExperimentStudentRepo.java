package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.info.base.query.PageQuery;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentStudentQuery;

import java.util.List;

public interface ExperimentStudentRepo {

    /**
     * 通过classId分页查询班级学生
     */
    List<ExperimentStudentDomain> queryPage(Context context, ExperimentStudentQuery query, boolean isFillUserInfo);

    /**
     * 通过userId分页查询班级信息
     */
    List<ExperimentStudentDomain> queryByUserId(ExperimentStudentQuery query);

    Integer queryTotal(ExperimentStudentQuery query);

    ExperimentStudentDomain queryByUserIdAndClassId(Integer userId, Integer classId);

    List<ExperimentStudentDomain> queryByIds(Context context, List<Integer> ids, boolean isFillUserInfo);

    void save(ExperimentStudentDomain record);

    void update(ExperimentStudentDomain record);

    void delete(List<Integer> ids);
}
