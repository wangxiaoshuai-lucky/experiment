package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentStudentHomeworkModel;
import com.kelab.info.experiment.query.ExperimentStudentHomeworkQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentStudentHomeworkMapper {

    List<ExperimentStudentHomeworkModel> queryPage(@Param("query") ExperimentStudentHomeworkQuery query);

    Integer queryTotal(@Param("query") ExperimentStudentHomeworkQuery query);

    List<ExperimentStudentHomeworkModel> queryAllByHomeworkIdsAndTargetIds(@Param("homeworkIds") List<Integer> homeworkIds,
                                                                           @Param("targetIds") List<Integer> targetIds);

    void save(@Param("record") ExperimentStudentHomeworkModel record);

    void update(@Param("record") ExperimentStudentHomeworkModel record);
}
