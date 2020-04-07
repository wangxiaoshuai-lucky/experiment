package com.kelab.experiment.service.impl;

import com.kelab.experiment.convert.ExperimentGroupConvert;
import com.kelab.experiment.dal.domain.ExperimentGroupDomain;
import com.kelab.experiment.dal.repo.ExperimentGroupRepo;
import com.kelab.experiment.dal.repo.ExperimentStudentRepo;
import com.kelab.experiment.service.ExperimentGroupService;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentChangeGroupInfo;
import com.kelab.info.experiment.info.ExperimentGroupInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentGroupServiceImpl implements ExperimentGroupService {

    private ExperimentGroupRepo experimentGroupRepo;

    private ExperimentStudentRepo experimentStudentRepo;

    public ExperimentGroupServiceImpl(ExperimentGroupRepo experimentGroupRepo,
                                      ExperimentStudentRepo experimentStudentRepo) {
        this.experimentGroupRepo = experimentGroupRepo;
        this.experimentStudentRepo = experimentStudentRepo;
    }

    @Override
    public PaginationResult<ExperimentGroupInfo> queryAllGroup(Context context, Integer classId) {
        List<ExperimentGroupInfo> infos = convertToInfo(experimentGroupRepo.queryAllByClassId(context, classId));
        PaginationResult<ExperimentGroupInfo> result = new PaginationResult<>();
        result.setPagingList(infos);
        result.setTotal(infos.size());
        return result;
    }

    @Override
    public void createGroup(Context context, ExperimentGroupDomain record) {
        experimentGroupRepo.save(record);
    }

    @Override
    public void updateGroup(Context context, ExperimentGroupDomain record) {
        experimentGroupRepo.update(record);
    }

    @Override
    public void deleteGroup(Context context, List<Integer> ids) {
        experimentGroupRepo.delete(ids);
    }

    @Override
    public void changeStudentGroup(Context context, ExperimentChangeGroupInfo record) {
        experimentStudentRepo.changeGroup(record);
    }

    private List<ExperimentGroupInfo> convertToInfo(List<ExperimentGroupDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentGroupConvert::domainToInfo).collect(Collectors.toList());
    }
}
