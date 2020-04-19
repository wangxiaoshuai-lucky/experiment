package com.kelab.experiment.dal.dao;

import com.kelab.experiment.dal.model.ExperimentChatModel;
import com.kelab.info.base.query.BaseQuery;
import com.kelab.info.experiment.query.ExperimentChatQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExperimentChatMapper {

    /**
     * 如果没指定id，则获取根数据
     * 否则根据 parentId 查询
     */
    List<ExperimentChatModel> queryPage(@Param("query") ExperimentChatQuery query);

    /**
     * 如果没指定id，则获取根数据
     * 否则获取具体数据
     */
    Integer queryTotal(@Param("query") ExperimentChatQuery query);

    /**
     * 查询指定 id 的根数据
     */
    ExperimentChatModel queryById(@Param("id") Integer id);

    /**
     * 添加讨论
     */
    void save(@Param("record") ExperimentChatModel record);
}
