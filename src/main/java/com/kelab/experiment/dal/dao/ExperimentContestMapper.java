package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentContestModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentContestMapper {

    List<ExperimentContestModel> queryByClassId(@Param("classId") Integer classId);

    List<ExperimentContestModel> queryByIds(@Param("ids") List<Integer> ids);

    void save(@Param("record") ExperimentContestModel record);

    void update(@Param("record") ExperimentContestModel record);

    void delete(@Param("ids") List<Integer> ids);
}
