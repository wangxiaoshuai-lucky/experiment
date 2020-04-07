package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentGroupDomain;
import com.kelab.info.context.Context;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExperimentGroupRepo {

    /**
     * 班级所有分组
     */
    List<ExperimentGroupDomain> queryAllByClassId(Context context, Integer classId, boolean isFillUserInfo);

    /**
     * 创建分组
     */
    void save(ExperimentGroupDomain record);

    /**
     * 创建分组
     */
    void update(ExperimentGroupDomain record);

    /**
     * 删除分组
     */
    void delete(List<Integer> ids);
}
