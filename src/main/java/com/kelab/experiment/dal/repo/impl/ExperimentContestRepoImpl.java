package com.kelab.experiment.dal.repo.impl;

import com.kelab.experiment.convert.ExperimentContestConvert;
import com.kelab.experiment.dal.dao.ExperimentContestMapper;
import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.experiment.dal.domain.ExperimentProblemDomain;
import com.kelab.experiment.dal.model.ExperimentContestModel;
import com.kelab.experiment.dal.repo.ExperimentContestRepo;
import com.kelab.experiment.dal.repo.ExperimentProblemRepo;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentContestQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ExperimentContestRepoImpl implements ExperimentContestRepo {

    private ExperimentContestMapper experimentContestMapper;

    private ExperimentProblemRepo experimentProblemRepo;

    @Autowired(required = false)
    public ExperimentContestRepoImpl(ExperimentContestMapper experimentContestMapper,
                                     ExperimentProblemRepo experimentProblemRepo) {
        this.experimentContestMapper = experimentContestMapper;
        this.experimentProblemRepo = experimentProblemRepo;
    }


    @Override
    public List<ExperimentContestDomain> queryContest(Context context, ExperimentContestQuery query, boolean isFillTitle) {
        return convertToDomain(context, experimentContestMapper.queryContest(query), isFillTitle);
    }

    @Override
    public List<ExperimentContestDomain> queryAllByClassId(Context context, Integer classId, boolean isFillTitle) {
        return convertToDomain(context, experimentContestMapper.queryAllByClassId(classId), isFillTitle);
    }

    @Override
    public Integer queryTotal(ExperimentContestQuery query) {
        return experimentContestMapper.queryTotal(query);
    }

    @Override
    public List<ExperimentContestDomain> queryByIds(Context context, List<Integer> ids, boolean isFillTitle) {
        return convertToDomain(context, experimentContestMapper.queryByIds(ids), isFillTitle);
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

    private List<ExperimentContestDomain> convertToDomain(Context context, List<ExperimentContestModel> models, boolean isFillTitle) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        Map<Integer, List<ExperimentProblemDomain>> probMap = experimentProblemRepo.queryAllByContestIds(context, models.stream().map(ExperimentContestModel::getId).collect(Collectors.toList()), isFillTitle);
        List<ExperimentContestDomain> domains = models.stream().map(ExperimentContestConvert::modelToDomain).collect(Collectors.toList());
        domains.forEach(item -> {
            item.setProblemDomains(probMap.get(item.getId()));
            item.setTotalNum(item.getProblemDomains().size());
        });
        return domains;
    }
}
