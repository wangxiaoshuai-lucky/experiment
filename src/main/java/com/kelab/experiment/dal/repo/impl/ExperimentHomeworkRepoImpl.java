package com.kelab.experiment.dal.repo.impl;

import com.alibaba.fastjson.JSON;
import com.kelab.experiment.constant.enums.CacheBizName;
import com.kelab.experiment.convert.ExperimentHomeworkConvert;
import com.kelab.experiment.dal.dao.ExperimentHomeworkMapper;
import com.kelab.experiment.dal.domain.ExperimentHomeworkDomain;
import com.kelab.experiment.dal.model.ExperimentHomeworkModel;
import com.kelab.experiment.dal.redis.RedisCache;
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

    private RedisCache redisCache;

    @Autowired(required = false)
    public ExperimentHomeworkRepoImpl(ExperimentHomeworkMapper experimentHomeworkMapper,
                                      RedisCache redisCache) {
        this.experimentHomeworkMapper = experimentHomeworkMapper;
        this.redisCache = redisCache;
    }


    private String buildCacheKey(ExperimentHomeworkQuery query) {
        return query.getClassId() + "::" + query.getPage() + "::" + query.getRows();
    }

    @Override
    public List<ExperimentHomeworkDomain> queryPage(ExperimentHomeworkQuery query) {
        String cacheObj = redisCache.cacheOne(CacheBizName.EXPERIMENT_HOMEWORK_PAGE, buildCacheKey(query),
                String.class, missKey -> JSON.toJSONString(experimentHomeworkMapper.queryPage(query)));
        return convertToDomain(JSON.parseArray(cacheObj, ExperimentHomeworkModel.class));
    }

    @Override
    public List<ExperimentHomeworkDomain> queryByIds(List<Integer> ids) {
        List<ExperimentHomeworkModel> cacheModels = redisCache.cacheList(CacheBizName.EXPERIMENT_HOMEWORK, ids, ExperimentHomeworkModel.class,
                missKeyList -> {
                    List<ExperimentHomeworkModel> dbModels = experimentHomeworkMapper.queryByIds(missKeyList);
                    if (CollectionUtils.isEmpty(dbModels)) {
                        return null;
                    }
                    return dbModels.stream().collect(Collectors.toMap(ExperimentHomeworkModel::getId, obj -> obj, (v1, v2) -> v2));
                });
        return convertToDomain(cacheModels);
    }

    @Override
    public Integer queryTotal(ExperimentHomeworkQuery query) {
        return experimentHomeworkMapper.queryTotal(query);
    }

    @Override
    public List<ExperimentHomeworkDomain> queryAllByClassId(Integer classId) {
        String cacheObj = redisCache.cacheOne(CacheBizName.EXPERIMENT_HOMEWORK_PAGE, classId,
                String.class, missKey -> JSON.toJSONString(experimentHomeworkMapper.queryAllByClassId(classId)));
        return convertToDomain(JSON.parseArray(cacheObj, ExperimentHomeworkModel.class));
    }

    @Override
    public void save(ExperimentHomeworkDomain record) {
        experimentHomeworkMapper.save(ExperimentHomeworkConvert.domainToModel(record));
        // 删除班级下的作业缓存
        redisCache.deleteByPre(CacheBizName.EXPERIMENT_HOMEWORK_PAGE, record.getClassId());
    }

    @Override
    public void update(ExperimentHomeworkDomain record) {
        List<ExperimentHomeworkDomain> old = queryByIds(Collections.singletonList(record.getId()));
        if (!CollectionUtils.isEmpty(old)) {
            experimentHomeworkMapper.update(ExperimentHomeworkConvert.domainToModel(record));
            // 删除班级下的作业缓存
            redisCache.delete(CacheBizName.EXPERIMENT_HOMEWORK, record.getId());
            redisCache.deleteByPre(CacheBizName.EXPERIMENT_HOMEWORK_PAGE, old.get(0).getClassId());
        }
    }

    @Override
    public void delete(List<Integer> ids) {
        List<ExperimentHomeworkDomain> old = queryByIds(ids);
        if (!CollectionUtils.isEmpty(old)) {
            experimentHomeworkMapper.delete(ids);
            // 删除班级下的作业缓存
            redisCache.deleteList(CacheBizName.EXPERIMENT_HOMEWORK, ids);
            redisCache.deleteByPre(CacheBizName.EXPERIMENT_HOMEWORK_PAGE, old.get(0).getClassId());
        }
    }

    private List<ExperimentHomeworkDomain> convertToDomain(List<ExperimentHomeworkModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        return models.stream().map(ExperimentHomeworkConvert::modelToDomain).collect(Collectors.toList());
    }
}
