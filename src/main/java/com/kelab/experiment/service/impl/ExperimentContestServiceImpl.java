package com.kelab.experiment.service.impl;

import com.google.common.base.Preconditions;
import com.kelab.experiment.convert.ExperimentContestConvert;
import com.kelab.experiment.convert.ExperimentProblemConvert;
import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.experiment.dal.domain.ExperimentProblemDomain;
import com.kelab.experiment.dal.repo.ExperimentContestRepo;
import com.kelab.experiment.dal.repo.ExperimentProblemRepo;
import com.kelab.experiment.service.ExperimentContestService;
import com.kelab.experiment.support.service.ProblemCenterService;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentContestInfo;
import com.kelab.info.experiment.info.ExperimentProblemInfo;
import com.kelab.info.experiment.query.ExperimentProblemQuery;
import com.kelab.info.problemcenter.info.ProblemUserMarkInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExperimentContestServiceImpl implements ExperimentContestService {

    private ExperimentContestRepo experimentContestRepo;

    private ExperimentProblemRepo experimentProblemRepo;

    private ProblemCenterService problemCenterService;

    public ExperimentContestServiceImpl(ExperimentContestRepo experimentContestRepo,
                                        ExperimentProblemRepo experimentProblemRepo,
                                        ProblemCenterService problemCenterService) {
        this.experimentContestRepo = experimentContestRepo;
        this.experimentProblemRepo = experimentProblemRepo;
        this.problemCenterService = problemCenterService;
    }

    @Override
    public PaginationResult<ExperimentContestInfo> queryByClassId(Context context, Integer classId) {
        PaginationResult<ExperimentContestInfo> result = new PaginationResult<>();
        List<ExperimentContestInfo> infos = convertToExContestInfo(experimentContestRepo.queryByClassId(classId));
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

    @Override
    public PaginationResult<ExperimentProblemInfo> queryByContestIdPage(Context context, ExperimentProblemQuery query) {
        // 获取题目
        List<ExperimentContestDomain> contests = experimentContestRepo.queryByIds(Collections.singletonList(query.getContestId()));
        Preconditions.checkArgument(!CollectionUtils.isEmpty(contests), "实验不存在");
        ExperimentContestDomain nowContest = contests.get(0);
        List<ExperimentProblemDomain> problemDomains = experimentProblemRepo.queryPage(context, query, true);
        // 填充是否ac
        List<ProblemUserMarkInfo> userAcInfos = problemCenterService.queryByUserIdsAndProbIdsAndEndTime(context,
                Collections.singletonList(context.getOperatorId()),
                problemDomains.stream().map(ExperimentProblemDomain::getProbId).collect(Collectors.toList()),
                nowContest.getEndTime());
        Set<Integer> acProbSet = userAcInfos.stream().map(ProblemUserMarkInfo::getProblemId).collect(Collectors.toSet());
        problemDomains.forEach(item -> item.setAc(acProbSet.contains(item.getProbId())));
        // 返回结果
        PaginationResult<ExperimentProblemInfo> result = new PaginationResult<>();
        result.setPagingList(convertToExProbInfo(problemDomains));
        result.setTotal(problemDomains.size());
        return result;
    }

    private List<ExperimentContestInfo> convertToExContestInfo(List<ExperimentContestDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentContestConvert::domainToInfo).collect(Collectors.toList());
    }

    private List<ExperimentProblemInfo> convertToExProbInfo(List<ExperimentProblemDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentProblemConvert::domainToInfo).collect(Collectors.toList());
    }
}
