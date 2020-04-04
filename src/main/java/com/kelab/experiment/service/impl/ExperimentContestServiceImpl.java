package com.kelab.experiment.service.impl;

import com.kelab.experiment.convert.ExperimentContestConvert;
import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.experiment.dal.repo.ExperimentContestRepo;
import com.kelab.experiment.dal.repo.ExperimentProblemRepo;
import com.kelab.experiment.service.ExperimentContestService;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentContestInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentContestServiceImpl implements ExperimentContestService {

    private ExperimentContestRepo experimentContestRepo;

    private ExperimentProblemRepo experimentProblemRepo;

    public ExperimentContestServiceImpl(ExperimentContestRepo experimentContestRepo,
                                        ExperimentProblemRepo experimentProblemRepo) {
        this.experimentContestRepo = experimentContestRepo;
        this.experimentProblemRepo = experimentProblemRepo;
    }

    @Override
    public PaginationResult<ExperimentContestInfo> queryByClassId(Context context, Integer classId) {
        PaginationResult<ExperimentContestInfo> result = new PaginationResult<>();
        List<ExperimentContestInfo> infos = convertToInfo(experimentContestRepo.queryByClassId(classId));
        result.setPagingList(infos);
        result.setTotal(infos.size());
        return result;
    }

    @Override
    public void saveContest(Context context, ExperimentContestDomain domain) {
        // 插入主体信息，同时回填 id
        experimentContestRepo.save(domain);
        // 绑定 contestId
        domain.getProblemDomains().forEach(item -> item.setContestId(domain.getId()));
        experimentProblemRepo.saveList(domain.getProblemDomains());
    }

    @Override
    public void updateContest(Context context, ExperimentContestDomain domain) {
        // 更新主体信息
        experimentContestRepo.update(domain);
        // 绑定 contestId
        domain.getProblemDomains().forEach(item -> item.setContestId(domain.getId()));
        experimentProblemRepo.saveList(domain.getProblemDomains());
    }

    @Override
    public void deleteContest(Context context, List<Integer> ids) {
        experimentContestRepo.delete(ids);
    }

    private List<ExperimentContestInfo> convertToInfo(List<ExperimentContestDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentContestConvert::domainToInfo).collect(Collectors.toList());
    }
}
