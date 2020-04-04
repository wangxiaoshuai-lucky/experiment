package com.kelab.experiment.dal.repo.impl;

import com.kelab.experiment.convert.ExperimentContestConvert;
import com.kelab.experiment.dal.dao.ExperimentContestMapper;
import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.experiment.dal.model.ExperimentContestModel;
import com.kelab.experiment.dal.repo.ExperimentContestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ExperimentContestRepoImpl implements ExperimentContestRepo {

    private ExperimentContestMapper experimentContestMapper;

    @Autowired(required = false)
    public ExperimentContestRepoImpl(ExperimentContestMapper experimentContestMapper) {
        this.experimentContestMapper = experimentContestMapper;
    }


    @Override
    public List<ExperimentContestDomain> queryByClassId(Integer classId) {
        return convertToDomain(experimentContestMapper.queryByClassId(classId));
    }

    @Override
    public void save(ExperimentContestDomain record) {
        ExperimentContestModel model = ExperimentContestConvert.domainToModel(record);
        this.experimentContestMapper.save(model);
        record.setId(model.getId());
    }

    @Override
    public void update(ExperimentContestDomain record) {
        this.experimentContestMapper.update(ExperimentContestConvert.domainToModel(record));
    }

    @Override
    public void delete(List<Integer> ids) {
        this.experimentContestMapper.delete(ids);
    }

    private List<ExperimentContestDomain> convertToDomain(List<ExperimentContestModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        return models.stream().map(ExperimentContestConvert::modelToDomain).collect(Collectors.toList());
    }
}
