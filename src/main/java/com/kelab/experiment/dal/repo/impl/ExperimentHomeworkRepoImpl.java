package com.kelab.experiment.dal.repo.impl;

import com.kelab.experiment.convert.ExperimentHomeworkConvert;
import com.kelab.experiment.dal.dao.ExperimentHomeworkMapper;
import com.kelab.experiment.dal.domain.ExperimentHomeworkDomain;
import com.kelab.experiment.dal.model.ExperimentHomeworkModel;
import com.kelab.experiment.dal.repo.ExperimentHomeworkRepo;
import com.kelab.info.experiment.query.ExperimentHomeworkQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ExperimentHomeworkRepoImpl implements ExperimentHomeworkRepo {

    private ExperimentHomeworkMapper experimentHomeworkMapper;

    @Autowired(required = false)
    public ExperimentHomeworkRepoImpl(ExperimentHomeworkMapper experimentHomeworkMapper) {
        this.experimentHomeworkMapper = experimentHomeworkMapper;
    }

    @Override
    public List<ExperimentHomeworkDomain> queryPage(ExperimentHomeworkQuery query) {
        return convertToDomain(experimentHomeworkMapper.queryPage(query));
    }

    @Override
    public List<ExperimentHomeworkDomain> queryByIds(List<Integer> ids) {
        return convertToDomain(experimentHomeworkMapper.queryByIds(ids));
    }

    @Override
    public Integer queryTotal(ExperimentHomeworkQuery query) {
        return experimentHomeworkMapper.queryTotal(query);
    }

    @Override
    public List<ExperimentHomeworkDomain> queryAllByClassId(Integer classId) {
        return convertToDomain(experimentHomeworkMapper.queryAllByClassId(classId));
    }

    @Override
    public void save(ExperimentHomeworkDomain record) {
        experimentHomeworkMapper.save(ExperimentHomeworkConvert.domainToModel(record));
    }

    @Override
    public void update(ExperimentHomeworkDomain record) {
        experimentHomeworkMapper.update(ExperimentHomeworkConvert.domainToModel(record));
    }

    @Override
    public void delete(List<Integer> ids) {
        experimentHomeworkMapper.delete(ids);
    }

    private List<ExperimentHomeworkDomain> convertToDomain(List<ExperimentHomeworkModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        return models.stream().map(ExperimentHomeworkConvert::modelToDomain).collect(Collectors.toList());
    }
}
