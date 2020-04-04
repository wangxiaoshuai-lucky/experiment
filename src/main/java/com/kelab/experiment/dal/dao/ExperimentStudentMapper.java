package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentStudentModel;
import com.kelab.info.base.query.PageQuery;
import com.kelab.info.experiment.query.ExperimentStudentQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentStudentMapper {

    /**
     * 分页查询学生名单
     * 只根据 classId 查询
     */
    List<ExperimentStudentModel> queryPage(@Param("query") ExperimentStudentQuery query);

    List<ExperimentStudentModel> queryByIds(@Param("ids") List<Integer> ids);

    Integer queryTotal(@Param("query") ExperimentStudentQuery query);

    ExperimentStudentModel queryByUserIdAndClassId(@Param("userId") Integer userId, @Param("classId") Integer classId);

    /**
     * 分页查询学生加入的班级
     * 状态为已审核
     * 只根据 userId 查询
     */
    List<ExperimentStudentModel> queryByUserId(@Param("query") ExperimentStudentQuery query);

    void save(@Param("record") ExperimentStudentModel record);

    void update(@Param("record") ExperimentStudentModel record);

    void delete(@Param("ids") List<Integer> ids);
}
