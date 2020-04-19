package com.kelab.experiment.service.impl;

import com.google.common.base.Preconditions;
import com.kelab.experiment.convert.ExperimentContestConvert;
import com.kelab.experiment.convert.ExperimentProblemConvert;
import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.experiment.dal.domain.ExperimentProblemDomain;
import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.experiment.dal.repo.ExperimentContestRepo;
import com.kelab.experiment.dal.repo.ExperimentProblemRepo;
import com.kelab.experiment.dal.repo.ExperimentStudentRepo;
import com.kelab.experiment.result.UserContestRankResult;
import com.kelab.experiment.service.ExperimentContestService;
import com.kelab.experiment.support.service.ProblemCenterService;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentContestInfo;
import com.kelab.info.experiment.info.ExperimentProblemInfo;
import com.kelab.info.experiment.query.ExperimentContestQuery;
import com.kelab.info.experiment.query.ExperimentProblemQuery;
import com.kelab.info.problemcenter.info.ProblemUserMarkInfo;
import com.kelab.info.problemcenter.info.ProblemUserMarkInnerInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExperimentContestServiceImpl implements ExperimentContestService {

    private ExperimentContestRepo experimentContestRepo;

    private ExperimentStudentRepo experimentStudentRepo;

    private ExperimentProblemRepo experimentProblemRepo;

    private ProblemCenterService problemCenterService;

    public ExperimentContestServiceImpl(ExperimentContestRepo experimentContestRepo,
                                        ExperimentStudentRepo experimentStudentRepo,
                                        ExperimentProblemRepo experimentProblemRepo,
                                        ProblemCenterService problemCenterService) {
        this.experimentContestRepo = experimentContestRepo;
        this.experimentStudentRepo = experimentStudentRepo;
        this.experimentProblemRepo = experimentProblemRepo;
        this.problemCenterService = problemCenterService;
    }

    @Override
    public PaginationResult<ExperimentContestInfo> queryContest(Context context, ExperimentContestQuery query) {
        PaginationResult<ExperimentContestInfo> result = new PaginationResult<>();
        List<Integer> ids = CommonService.totalIds(query);
        if (!CollectionUtils.isEmpty(ids)) {
            List<ExperimentContestInfo> infos = convertToExContestInfo(context, experimentContestRepo.queryByIds(context, ids, true));
            result.setPagingList(infos);
            result.setTotal(infos.size());
        } else if (query.getClassId() != null) {
            List<ExperimentContestInfo> infos = convertToExContestInfo(context, experimentContestRepo.queryContest(context, query, true));
            result.setPagingList(infos);
            result.setTotal(experimentContestRepo.queryTotal(query));
        }
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
        List<ExperimentContestDomain> contests = experimentContestRepo.queryByIds(context, Collections.singletonList(query.getContestId()), false);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(contests), "实验不存在");
        ExperimentContestDomain nowContest = contests.get(0);
        List<ExperimentProblemDomain> problemDomains = experimentProblemRepo.queryPage(context, query, true);
        // 填充是否ac
        List<ProblemUserMarkInnerInfo> userAcInfos = problemCenterService.queryByUserIdsAndProbIdsAndEndTime(context,
                Collections.singletonList(context.getOperatorId()),
                problemDomains.stream().map(ExperimentProblemDomain::getProbId).collect(Collectors.toList()),
                nowContest.getEndTime());
        Set<Integer> acProbSet = userAcInfos.stream().map(ProblemUserMarkInnerInfo::getProblemId).collect(Collectors.toSet());
        problemDomains.forEach(item -> item.setAc(acProbSet.contains(item.getProbId())));
        // 返回结果
        PaginationResult<ExperimentProblemInfo> result = new PaginationResult<>();
        result.setPagingList(convertToExProbInfo(problemDomains));
        result.setTotal(experimentProblemRepo.queryTotal(query));
        return result;
    }

    @Override
    public PaginationResult<UserContestRankResult> queryRankByContestId(Context context, Integer contestId) {
        // 获取题目
        List<ExperimentContestDomain> contests = experimentContestRepo.queryByIds(context, Collections.singletonList(contestId), false);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(contests), "实验不存在");
        ExperimentContestDomain contest = contests.get(0);
        Map<Integer, List<ExperimentProblemDomain>> contestProblems = experimentProblemRepo.queryAllByContestIds(context, Collections.singletonList(contestId), false);
        Preconditions.checkArgument(contestProblems.containsKey(contestId) && contestProblems.get(contestId).size() > 0, "实验题目不存在");
        List<Integer> allProblemIds = contestProblems.get(contestId).stream().map(ExperimentProblemDomain::getProbId).collect(Collectors.toList());
        // 所有学生
        List<ExperimentStudentDomain> students = experimentStudentRepo.queryAllByClassId(context, contest.getClassId(), true);
        if (CollectionUtils.isEmpty(students)) {
            PaginationResult<UserContestRankResult> result = new PaginationResult<>();
            result.setTotal(0);
            result.setPagingList(Collections.emptyList());
            return result;
        }
        List<Integer> userIds = students.stream().map(ExperimentStudentDomain::getUserId).collect(Collectors.toList());
        // 所有ac记录
        List<ProblemUserMarkInnerInfo> userAcInfos = problemCenterService.queryByUserIdsAndProbIdsAndEndTime(context, userIds, allProblemIds, contest.getEndTime());
        Map<Integer, Long> userAcNum = userAcInfos.stream().collect(Collectors.groupingBy(ProblemUserMarkInnerInfo::getUserId, Collectors.counting()));
        // 转换模型
        PaginationResult<UserContestRankResult> result = new PaginationResult<>();
        List<UserContestRankResult> userResult = students.stream().map(item -> {
            UserContestRankResult single = new UserContestRankResult();
            single.setAcNum(userAcNum.getOrDefault(item.getUserId(), 0L).intValue());
            single.setUserId(item.getUserId());
            single.setUserInfo(item.getStudentInfo());
            single.setTotalNum(allProblemIds.size());
            return single;
        }).sorted((a, b) -> b.getAcNum().compareTo(a.getAcNum())).collect(Collectors.toList());
        fillRank(userResult);
        result.setTotal(userResult.size());
        result.setPagingList(userResult);
        return result;
    }

    /**
     * 填充rank
     */
    private void fillRank(List<UserContestRankResult> userResult) {
        UserContestRankResult pre = userResult.get(0);
        pre.setRank(1);
        for (int i = 0; i < userResult.size(); i++) {
            UserContestRankResult single = userResult.get(i);
            if (single.getAcNum().equals(pre.getAcNum())) {
                single.setRank(pre.getRank());
            } else {
                single.setRank(i + 1);
            }
            pre = single;
        }
    }

    private List<ExperimentContestInfo> convertToExContestInfo(Context context, List<ExperimentContestDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        // 获取所有的题目 ids
        List<Integer> proIds = new ArrayList<>();
        domains.forEach(item -> proIds.addAll(item.getProblemDomains().stream().map(ExperimentProblemDomain::getProbId).collect(Collectors.toList())));
        // 截止时间 endTime， 缩小搜索的范围
        long maxEndTime = 0L;
        for (ExperimentContestDomain single : domains) {
            maxEndTime = maxEndTime < single.getEndTime() ? single.getEndTime() : maxEndTime;
        }
        // 题目的 ac 记录
        Map<Integer, ProblemUserMarkInnerInfo> userAcMap = problemCenterService.queryByUserIdsAndProbIdsAndEndTime(context,
                Collections.singletonList(context.getOperatorId()), proIds, maxEndTime)
                .stream().collect(Collectors.toMap(ProblemUserMarkInnerInfo::getProblemId, obj -> obj, (v1, v2) -> v2));
        // 填充每个实验的进度
        domains.forEach(singleContest -> {
            List<ExperimentProblemDomain> problems = singleContest.getProblemDomains();
            List<ExperimentProblemDomain> acProblems = problems.stream()
                    .filter(singleProblem ->
                            userAcMap.containsKey(singleProblem.getProbId()) && userAcMap.get(singleProblem.getProbId()).getMarkTime() < singleContest.getEndTime())
                    .collect(Collectors.toList());
            singleContest.setAcNum(acProblems.size());
            singleContest.setTotalNum(problems.size());
        });
        return domains.stream().map(ExperimentContestConvert::domainToInfo).collect(Collectors.toList());
    }

    private List<ExperimentProblemInfo> convertToExProbInfo(List<ExperimentProblemDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentProblemConvert::domainToInfo).collect(Collectors.toList());
    }
}
