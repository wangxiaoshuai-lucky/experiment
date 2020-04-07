package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentHomeworkModel;
import com.kelab.info.experiment.query.ExperimentHomeworkQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentHomeworkMapper {

    List<ExperimentHomeworkModel> queryPage(@Param("query") ExperimentHomeworkQuery query);

    List<ExperimentHomeworkModel> queryByIds(@Param("ids") List<Integer> ids);

    Integer queryTotal(@Param("query") ExperimentHomeworkQuery query);

    List<ExperimentHomeworkModel> queryAllByClassId(@Param("classId") Integer classId);

    void save(@Param("record") ExperimentHomeworkModel record);

    void update(@Param("record") ExperimentHomeworkModel record);

    void delete(@Param("ids") List<Integer> ids);
}
