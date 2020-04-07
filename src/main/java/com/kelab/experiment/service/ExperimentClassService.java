package com.kelab.experiment.service;

import com.kelab.experiment.dal.domain.ExperimentClassDomain;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentClassInfo;
import com.kelab.info.experiment.info.ExperimentReviewStudentInfo;
import com.kelab.info.experiment.info.ExperimentStudentInfo;
import com.kelab.info.experiment.query.ExperimentClassQuery;
import com.kelab.info.experiment.query.ExperimentStudentQuery;

import java.util.List;

public interface ExperimentClassService {

    /**
     * 分页查询，管理员、教师端查看
     */
    PaginationResult<ExperimentClassInfo> queryPage(Context context, ExperimentClassQuery query);

    /**
     * 分页查询 用户端查看
     */
    PaginationResult<ExperimentClassInfo> queryPageForUser(Context context, ExperimentStudentQuery query);

    /**
     * 创建班级
     */
    void createExperimentClass(Context context, ExperimentClassDomain domain);

    /**
     * 创建班级
     */
    void updateExperimentClass(Context context, ExperimentClassDomain domain);

    /**
     * 删除班级
     */
    void deleteExperimentClass(Context context, List<Integer> ids);

    /**
     * 分页查询班级学生
     */
    PaginationResult<ExperimentStudentInfo> queryStudentPage(Context context, ExperimentStudentQuery query);

    /**
     * 申请加班
     */
    ExperimentClassInfo applyClass(Context context, String classCode);

    /**
     * 审核学生的加班
     */
    void reviewStudentApply(Context context, ExperimentReviewStudentInfo record);

    /**
     * 查询所有未分组的学生名单
     */
    PaginationResult<ExperimentStudentInfo> queryAllStudentWithoutGroup(Context context, Integer classId);
}
