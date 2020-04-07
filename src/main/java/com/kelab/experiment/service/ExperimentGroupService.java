package com.kelab.experiment.service;

import com.kelab.experiment.dal.domain.ExperimentGroupDomain;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentChangeGroupInfo;
import com.kelab.info.experiment.info.ExperimentGroupInfo;
import com.kelab.info.experiment.info.ExperimentStudentInfo;

import java.util.List;

public interface ExperimentGroupService {

    /**
     * 查询所有的分组
     */
    PaginationResult<ExperimentGroupInfo> queryAllGroup(Context context, Integer classId);

    /**
     * 创建分组，
     * 只用创建分组名字，班级id
     */
    void createGroup(Context context, ExperimentGroupDomain record);


    /**
     * 更新分组,
     * 只更新分组名
     */
    void updateGroup(Context context, ExperimentGroupDomain record);

    /**
     * 删除分组
     */
    void deleteGroup(Context context, List<Integer> ids);

    /**
     * 班级学生
     * 切换分组
     */
    void changeStudentGroup(Context context, ExperimentChangeGroupInfo record);
}
