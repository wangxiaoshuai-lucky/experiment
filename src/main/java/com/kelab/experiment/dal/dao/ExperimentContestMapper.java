package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentContestModel;
import com.kelab.info.experiment.query.ExperimentContestQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentContestMapper {

    List<ExperimentContestModel> queryContest(@Param("query") ExperimentContestQuery query);


    List<ExperimentContestModel> queryAllByClassId(@Param("classId") Integer classId);

    Integer queryTotal(@Param("query") ExperimentContestQuery query);

    List<ExperimentContestModel> queryByIds(@Param("ids") List<Integer> ids);

    void save(@Param("record") ExperimentContestModel record);

    void update(@Param("record") ExperimentContestModel record);

    void delete(@Param("ids") List<Integer> ids);
}
