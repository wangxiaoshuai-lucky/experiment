package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentClassModel;
import com.kelab.info.experiment.query.ExperimentClassQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentClassMapper {

    List<ExperimentClassModel> queryPage(@Param("query") ExperimentClassQuery query);

    List<ExperimentClassModel> queryByIds(@Param("ids") List<Integer> ids);

    Integer queryTotal(@Param("query") ExperimentClassQuery query);

    ExperimentClassModel queryByCode(@Param("code") String code);

    void save(@Param("record") ExperimentClassModel record);

    void update(@Param("record") ExperimentClassModel record);

    void delete(@Param("ids") List<Integer> ids);
}
