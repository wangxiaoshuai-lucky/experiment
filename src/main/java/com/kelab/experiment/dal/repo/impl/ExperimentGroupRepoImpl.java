package com.kelab.experiment.dal.repo.impl;

import com.alibaba.fastjson.JSON;
import com.kelab.experiment.constant.enums.CacheBizName;
import com.kelab.experiment.convert.ExperimentGroupConvert;
import com.kelab.experiment.dal.dao.ExperimentGroupMapper;
import com.kelab.experiment.dal.domain.ExperimentGroupDomain;
import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.experiment.dal.model.ExperimentGroupModel;
import com.kelab.experiment.dal.redis.RedisCache;
import com.kelab.experiment.dal.repo.ExperimentGroupRepo;
import com.kelab.experiment.dal.repo.ExperimentStudentRepo;
import com.kelab.info.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ExperimentGroupRepoImpl implements ExperimentGroupRepo {

    private ExperimentGroupMapper experimentGroupMapper;

    private ExperimentStudentRepo experimentStudentRepo;

    private RedisCache redisCache;

    @Autowired(required = false)
    public ExperimentGroupRepoImpl(ExperimentGroupMapper experimentGroupMapper,
                                   ExperimentStudentRepo experimentStudentRepo,
                                   RedisCache redisCache) {
        this.experimentGroupMapper = experimentGroupMapper;
        this.experimentStudentRepo = experimentStudentRepo;
        this.redisCache = redisCache;
    }

    @Override
    public List<ExperimentGroupDomain> queryAllByClassId(Context context, Integer classId, boolean isFillUserInfo) {
        String cacheObj = redisCache.cacheOne(CacheBizName.EXPERIMENT_GROUP_PAGE, classId,
                String.class, missKey -> JSON.toJSONString(experimentGroupMapper.queryAllByClassId(classId)));
        return convertToDomain(context, JSON.parseArray(cacheObj, ExperimentGroupModel.class), isFillUserInfo);
    }

    @Override
    public List<ExperimentGroupDomain> queryByIds(Context context, List<Integer> ids, boolean isFillUserInfo) {
        List<ExperimentGroupModel> cacheModels = redisCache.cacheList(CacheBizName.EXPERIMENT_GROUP, ids, ExperimentGroupModel.class, missKeyList -> {
            List<ExperimentGroupModel> dbModels = experimentGroupMapper.queryByIds(missKeyList);
            if (CollectionUtils.isEmpty(dbModels)) {
                return null;
            }
            return dbModels.stream().collect(Collectors.toMap(ExperimentGroupModel::getId, obj -> obj, (v1, v2) -> v2));
        });
        return convertToDomain(context, cacheModels, isFillUserInfo);
    }

    @Override
    public void save(ExperimentGroupDomain record) {
        experimentGroupMapper.save(ExperimentGroupConvert.domainToModel(record));
        // 删除分页缓存
        redisCache.deleteByPre(CacheBizName.EXPERIMENT_GROUP_PAGE, record.getClassId());
    }

    @Override
    public void update(ExperimentGroupDomain record) {
        List<ExperimentGroupModel> old = experimentGroupMapper.queryByIds(Collections.singletonList(record.getId()));
        if (!CollectionUtils.isEmpty(old)) {
            experimentGroupMapper.update(ExperimentGroupConvert.domainToModel(record));
            // 删除分页缓存
            redisCache.deleteByPre(CacheBizName.EXPERIMENT_GROUP_PAGE, record.getClassId());
            // 删除单记录缓存
            redisCache.delete(CacheBizName.EXPERIMENT_GROUP, record.getId());
        }
    }

    @Override
    public void delete(List<Integer> ids) {
        // 修改之前的绑定的学生信息
        List<ExperimentGroupModel> old = experimentGroupMapper.queryByIds(ids);
        if (old != null) {
            experimentGroupMapper.delete(ids);
            experimentStudentRepo.resetGroup(old.get(0).getClassId(), ids);
            // 删除分页缓存
            redisCache.deleteByPre(CacheBizName.EXPERIMENT_GROUP_PAGE, old.get(0).getClassId());
            // 删除单记录缓存
            redisCache.deleteList(CacheBizName.EXPERIMENT_GROUP, ids);
        }
    }

    private List<ExperimentGroupDomain> convertToDomain(Context context, List<ExperimentGroupModel> models, boolean isFillUserInfo) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        List<ExperimentGroupDomain> domains = models.stream().map(ExperimentGroupConvert::modelToDomain).collect(Collectors.toList());
        List<ExperimentStudentDomain> users = experimentStudentRepo.queryAllByClassId(context, models.get(0).getClassId(), isFillUserInfo);
        if (CollectionUtils.isEmpty(users)) {
            return domains;
        }
        Map<Integer, List<ExperimentStudentDomain>> groups = users.stream().collect(Collectors.groupingBy(ExperimentStudentDomain::getGroupId, Collectors.toList()));
        domains.forEach(item -> item.setMembers(groups.get(item.getId())));
        return domains;
    }

}
