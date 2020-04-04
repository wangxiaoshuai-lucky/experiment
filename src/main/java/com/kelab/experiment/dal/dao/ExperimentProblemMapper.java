package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentProblemModel;
import com.kelab.info.experiment.query.ExperimentProblemQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentProblemMapper {

    List<ExperimentProblemModel> queryPage(@Param("query") ExperimentProblemQuery query);

    Integer queryTotal(@Param("query") ExperimentProblemQuery query);

    List<ExperimentProblemModel> queryAllByContestId(@Param("contestId") Integer contestId);

    void deleteByContestId(@Param("contestId") Integer contestId);

    void saveList(@Param("records") List<ExperimentProblemModel> records);
}
