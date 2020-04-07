package com.kelab.experiment.dal.repo.impl;

import com.kelab.experiment.convert.ExperimentStudentHomeworkConvert;
import com.kelab.experiment.dal.dao.ExperimentStudentHomeworkMapper;
import com.kelab.experiment.dal.domain.ExperimentStudentHomeworkDomain;
import com.kelab.experiment.dal.model.ExperimentStudentHomeworkModel;
import com.kelab.experiment.dal.repo.ExperimentStudentHomeworkRepo;
import com.kelab.info.experiment.query.ExperimentStudentHomeworkQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ExperimentStudentHomeworkRepoImpl implements ExperimentStudentHomeworkRepo {

    private ExperimentStudentHomeworkMapper experimentStudentHomeworkMapper;

    @Autowired(required = false)
    public ExperimentStudentHomeworkRepoImpl(ExperimentStudentHomeworkMapper experimentStudentHomeworkMapper) {
        this.experimentStudentHomeworkMapper = experimentStudentHomeworkMapper;
    }

    @Override
    public List<ExperimentStudentHomeworkDomain> queryPage(ExperimentStudentHomeworkQuery query) {
        return convertToDomain(experimentStudentHomeworkMapper.queryPage(query));
    }

    @Override
    public Integer queryTotal(ExperimentStudentHomeworkQuery query) {
        return experimentStudentHomeworkMapper.queryTotal(query);
    }

    @Override
    public List<ExperimentStudentHomeworkDomain> queryAllByHomeworkIdsAndTargetIds(List<Integer> homeworkIds, List<Integer> targetIds) {
        return convertToDomain(experimentStudentHomeworkMapper.queryAllByHomeworkIdsAndTargetIds(homeworkIds, targetIds));
    }

    @Override
    public void save(ExperimentStudentHomeworkDomain record) {
        experimentStudentHomeworkMapper.save(ExperimentStudentHomeworkConvert.domainToModel(record));
    }

    @Override
    public void update(ExperimentStudentHomeworkDomain record) {
        experimentStudentHomeworkMapper.update(ExperimentStudentHomeworkConvert.domainToModel(record));
    }

    private List<ExperimentStudentHomeworkDomain> convertToDomain(List<ExperimentStudentHomeworkModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        return models.stream().map(ExperimentStudentHomeworkConvert::modelToDomain).collect(Collectors.toList());
    }
}
