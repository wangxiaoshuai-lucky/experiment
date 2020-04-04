package com.kelab.experiment.service.impl;

import com.kelab.experiment.convert.ExperimentContestConvert;
import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.experiment.dal.repo.ExperimentContestRepo;
import com.kelab.experiment.service.ExperimentContestService;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.base.constant.UserRoleConstant;
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

    public ExperimentContestServiceImpl(ExperimentContestRepo experimentContestRepo) {
        this.experimentContestRepo = experimentContestRepo;
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
    public void updateContest(Context context, ExperimentContestDomain domain) {
        experimentContestRepo.update(domain);
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
