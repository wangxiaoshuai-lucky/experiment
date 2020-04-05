package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentReviewStudentInfo;
import com.kelab.info.experiment.query.ExperimentStudentQuery;

import java.util.List;

public interface ExperimentStudentRepo {

    /**
     * 通过classId分页查询班级学生， 缓存
     */
    List<ExperimentStudentDomain> queryPage(Context context, ExperimentStudentQuery query, boolean isFillUserInfo);

    /**
     * 查询一个班级所有审核通过的学生， 缓存
     */
    List<ExperimentStudentDomain> queryAllByClassId(Context context, Integer classId, boolean isFillUserInfo);

    /**
     * 通过userId分页查询班级信息
     */
    List<ExperimentStudentDomain> queryByUserId(ExperimentStudentQuery query);

    /**
     * 查询申请总数
     */
    Integer queryTotal(ExperimentStudentQuery query);

    /**
     * 查询学生加班申请记录
     */
    ExperimentStudentDomain queryByUserIdAndClassId(Integer userId, Integer classId);

    /**
     * 根据申请 id 查看记录
     */
    List<ExperimentStudentDomain> queryByIds(Context context, List<Integer> ids, boolean isFillUserInfo);

    /**
     * 保存申请记录
     */
    void save(ExperimentStudentDomain record);

    /**
     * 审核申请记录，用于同意加班
     */
    void update(ExperimentReviewStudentInfo record);

    /**
     * 审核申请记录，用于拒绝加班
     */
    void delete(ExperimentReviewStudentInfo record);
}
