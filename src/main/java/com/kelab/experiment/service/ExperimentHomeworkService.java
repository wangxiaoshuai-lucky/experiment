package com.kelab.experiment.service;

import com.kelab.experiment.dal.domain.ExperimentHomeworkDomain;
import com.kelab.experiment.dal.domain.ExperimentStudentHomeworkDomain;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentHomeworkInfo;
import com.kelab.info.experiment.info.ExperimentStudentHomeworkInfo;
import com.kelab.info.experiment.query.ExperimentHomeworkQuery;
import com.kelab.info.experiment.query.ExperimentStudentHomeworkQuery;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExperimentHomeworkService {

    /**
     * 分页查询作业
     */
    PaginationResult<ExperimentHomeworkInfo> queryHomeworkPage(Context context, ExperimentHomeworkQuery query);

    /**
     * 布置作业
     */
    void createHomework(Context context, ExperimentHomeworkDomain record);

    /**
     * 更新作业
     */
    void updateHomework(Context context, ExperimentHomeworkDomain record);

    /**
     * 删除作业
     */
    void deleteHomework(Context context, List<Integer> ids);


    /**
     * 分页查询学生提交的作业
     */
    PaginationResult<ExperimentStudentHomeworkInfo> queryStudentHomeworkPage(Context context, ExperimentStudentHomeworkQuery query);

    /**
     * 学生提交作业
     */
    void submitHomework(Context context, ExperimentStudentHomeworkDomain record);

    /**
     * 教师批改作业
     */
    void reviewHomework(Context context, ExperimentStudentHomeworkDomain record);


    /**
     * 下载班级的成绩
     */
    ResponseEntity<byte[]> downloadClassScore(Context context, Integer classId);

}
