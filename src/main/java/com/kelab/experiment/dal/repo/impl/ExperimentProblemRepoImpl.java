package com.kelab.experiment.dal.repo.impl;

import com.alibaba.fastjson.JSON;
import com.kelab.experiment.constant.enums.CacheBizName;
import com.kelab.experiment.convert.ExperimentProblemConvert;
import com.kelab.experiment.dal.dao.ExperimentProblemMapper;
import com.kelab.experiment.dal.domain.ExperimentProblemDomain;
import com.kelab.experiment.dal.model.ExperimentProblemModel;
import com.kelab.experiment.dal.redis.RedisCache;
import com.kelab.experiment.dal.repo.ExperimentProblemRepo;
import com.kelab.experiment.support.service.ProblemCenterService;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentProblemQuery;
import com.kelab.info.problemcenter.info.ProblemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ExperimentProblemRepoImpl implements ExperimentProblemRepo {

    private RedisCache redisCache;

    private ExperimentProblemMapper experimentProblemMapper;

    private ProblemCenterService problemCenterService;

    @Autowired(required = false)
    public ExperimentProblemRepoImpl(RedisCache redisCache,
                                     ExperimentProblemMapper experimentProblemMapper,
                                     ProblemCenterService problemCenterService) {
        this.redisCache = redisCache;
        this.experimentProblemMapper = experimentProblemMapper;
        this.problemCenterService = problemCenterService;
    }

    private String buildCacheKey(ExperimentProblemQuery query) {
        return query.getContestId() + "::" + query.getPage() + "::" + query.getRows();
    }

    @Override
    public List<ExperimentProblemDomain> queryPage(Context context, ExperimentProblemQuery query, boolean isFillTitle) {
        String cacheObj = redisCache.cacheOne(CacheBizName.EXPERIMENT_PROBLEM_PAGE,
                buildCacheKey(query), String.class,
                missKey -> JSON.toJSONString(experimentProblemMapper.queryPage(query)));
        return convertToDomain(context, JSON.parseArray(cacheObj, ExperimentProblemModel.class), isFillTitle);
    }

    @Override
    public Integer queryTotal(ExperimentProblemQuery query) {
        return experimentProblemMapper.queryTotal(query);
    }

    @Override
    public Map<Integer, List<ExperimentProblemDomain>> queryAllByContestIds(Context context, List<Integer> contestIds, boolean isFillTitle) {
        // cacheObj : List< JSON(List<ExperimentProblemModel>)>
        List<String> cacheObj = redisCache.cacheList(CacheBizName.EXPERIMENT_PROBLEM_PAGE,
                contestIds, String.class,
                missKeys -> {
                    List<ExperimentProblemModel> problemModels = experimentProblemMapper.queryAllByContestIds(missKeys);
                    if (CollectionUtils.isEmpty(problemModels)) {
                        return null;
                    }
                    Map<Integer, List<ExperimentProblemModel>> collect = problemModels.stream().
                            collect(Collectors.groupingBy(ExperimentProblemModel::getContestId, Collectors.toList()));
                    Map<Integer, String> dbData = new HashMap<>();
                    collect.forEach((k, v) -> dbData.put(k, JSON.toJSONString(v)));
                    return dbData;
                });
        // convert to Map
        Map<Integer, List<ExperimentProblemModel>> modelMap = cacheObj.stream().map(jsonList -> JSON.parseArray(jsonList, ExperimentProblemModel.class))
                .collect(Collectors.toMap(item -> item.get(0).getContestId(), obj -> obj));
        return convertToDomain(context, modelMap, isFillTitle);
    }


    @Override
    public void saveList(List<ExperimentProblemDomain> records) {
        Integer contestId = records.get(0).getContestId();
        experimentProblemMapper.deleteByContestId(contestId);
        experimentProblemMapper.saveList(records.stream().map(ExperimentProblemConvert::domainToModel).collect(Collectors.toList()));
        // 删除 contestId 下的所有分页缓存
        redisCache.deleteByPre(CacheBizName.EXPERIMENT_PROBLEM_PAGE, contestId);
    }

    private List<ExperimentProblemDomain> convertToDomain(Context context, List<ExperimentProblemModel> models, boolean isFillTitle) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        List<ExperimentProblemDomain> domains = models.stream().map(ExperimentProblemConvert::modelToDomain).collect(Collectors.toList());
        if (context != null && isFillTitle) {
            Map<Integer, ProblemInfo> problemInfoMap = problemCenterService.queryByProIds(context,
                    domains.stream().map(ExperimentProblemDomain::getProbId).collect(Collectors.toList()));
            domains.forEach(item -> item.setTitle(problemInfoMap.getOrDefault(item.getProbId(), new ProblemInfo()).getTitle()));
        }
        return domains;
    }

    private Map<Integer, List<ExperimentProblemDomain>> convertToDomain(Context context, Map<Integer, List<ExperimentProblemModel>> modelMap, boolean isFillTitle) {
        if (CollectionUtils.isEmpty(modelMap)) {
            return Collections.emptyMap();
        }
        Map<Integer, List<ExperimentProblemDomain>> result = new HashMap<>();
        modelMap.forEach((k, v) -> {
            List<ExperimentProblemDomain> domains = v.stream().map(ExperimentProblemConvert::modelToDomain).collect(Collectors.toList());
            result.put(k, domains);
        });
        if (context != null && isFillTitle) {
            // 记录所有的题目 id
            List<Integer> proIds = new ArrayList<>();
            modelMap.forEach((k, v) -> proIds.addAll(v.stream().map(ExperimentProblemModel::getProbId).collect(Collectors.toList())));
            Map<Integer, ProblemInfo> problemInfoMap = problemCenterService.queryByProIds(context, proIds);
            result.forEach((k, v) -> v.forEach(item -> item.setTitle(problemInfoMap.getOrDefault(item.getProbId(), new ProblemInfo()).getTitle())));
        }
        return result;
    }
}
