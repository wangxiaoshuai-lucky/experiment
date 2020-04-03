package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentStudentModel;
import com.kelab.info.experiment.query.ExperimentStudentQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentStudentMapper {

    List<ExperimentStudentModel> queryPage(@Param("query") ExperimentStudentQuery query);

    List<ExperimentStudentModel> queryByIds(@Param("ids") List<Integer> ids);

    Integer queryTotal(@Param("query") ExperimentStudentQuery query);

    ExperimentStudentModel queryByUserIdAndClassId(@Param("userId") Integer userId, @Param("classId") Integer classId);

    void save(@Param("record") ExperimentStudentModel record);

    void update(@Param("record") ExperimentStudentModel record);

    void delete(@Param("ids") List<Integer> ids);
}
