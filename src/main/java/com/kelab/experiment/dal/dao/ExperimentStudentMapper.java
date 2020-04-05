package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentStudentModel;
import com.kelab.info.base.query.PageQuery;
import com.kelab.info.experiment.info.ExperimentReviewStudentInfo;
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

    /**
     * 查询一个班级所有审核通过的学生
     */
    List<ExperimentStudentModel> queryAllByClassId(@Param("classId") Integer classId);

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

    void update(@Param("record") ExperimentReviewStudentInfo record);

    void delete(@Param("record") ExperimentReviewStudentInfo record);
}
