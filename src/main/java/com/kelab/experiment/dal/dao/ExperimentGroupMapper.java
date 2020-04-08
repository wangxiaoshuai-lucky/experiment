package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentGroupModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentGroupMapper {

    /**
     * 查询所有分组
     */
    List<ExperimentGroupModel> queryAllByClassId(@Param("classId") Integer classId);

    /**
     * 通过ids查询
     */
    List<ExperimentGroupModel> queryByIds(@Param("ids") List<Integer> ids);


    void save(@Param("record") ExperimentGroupModel record);

    void update(@Param("record") ExperimentGroupModel record);

    void delete(@Param("ids") List<Integer> ids);
}
