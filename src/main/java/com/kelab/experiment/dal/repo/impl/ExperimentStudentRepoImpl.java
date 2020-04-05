package com.kelab.experiment.dal.repo.impl;

import com.alibaba.fastjson.JSON;
import com.kelab.experiment.constant.enums.ApplyClassStatus;
import com.kelab.experiment.constant.enums.CacheBizName;
import com.kelab.experiment.convert.ExperimentStudentConvert;
import com.kelab.experiment.dal.dao.ExperimentStudentMapper;
import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.experiment.dal.model.ExperimentStudentModel;
import com.kelab.experiment.dal.redis.RedisCache;
import com.kelab.experiment.dal.repo.ExperimentStudentRepo;
import com.kelab.experiment.support.service.UserCenterService;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentReviewStudentInfo;
import com.kelab.info.experiment.query.ExperimentStudentQuery;
import com.kelab.info.usercenter.info.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ExperimentStudentRepoImpl implements ExperimentStudentRepo {

    private ExperimentStudentMapper experimentStudentMapper;

    private UserCenterService userCenterService;

    private RedisCache redisCache;

    @Autowired(required = false)
    public ExperimentStudentRepoImpl(ExperimentStudentMapper experimentStudentMapper,
                                     UserCenterService userCenterService,
                                     RedisCache redisCache) {
        this.experimentStudentMapper = experimentStudentMapper;
        this.userCenterService = userCenterService;
        this.redisCache = redisCache;
    }

    /**
     * classId::status::page::rows
     */
    private String buildCacheKey(ExperimentStudentQuery query) {
        return query.getClassId() + "::" + query.getStatus() + "::" + query.getPage() + "::" + query.getRows();
    }

    @Override
    public List<ExperimentStudentDomain> queryPage(Context context, ExperimentStudentQuery query, boolean isFillUserInfo) {
        String cacheObj = redisCache.cacheOne(CacheBizName.EXPERIMENT_STUDENT_PAGE, buildCacheKey(query),
                String.class, missKey -> JSON.toJSONString(experimentStudentMapper.queryPage(query)));
        return convertToDomain(context, JSON.parseArray(cacheObj, ExperimentStudentModel.class), isFillUserInfo);
    }

    @Override
    public List<ExperimentStudentDomain> queryAllByClassId(Context context, Integer classId, boolean isFillUserInfo) {
        String cacheObj = redisCache.cacheOne(CacheBizName.EXPERIMENT_STUDENT_PAGE, classId + "::" + ApplyClassStatus.ALLOWED.value(),
                String.class, missKey -> JSON.toJSONString(experimentStudentMapper.queryAllByClassId(classId)));
        return convertToDomain(context, JSON.parseArray(cacheObj, ExperimentStudentModel.class), isFillUserInfo);
    }

    @Override
    public List<ExperimentStudentDomain> queryByUserId(ExperimentStudentQuery query) {
        return convertToDomain(null, experimentStudentMapper.queryByUserId(query), false);
    }

    @Override
    public List<ExperimentStudentDomain> queryByIds(Context context, List<Integer> ids, boolean isFillUserInfo) {
        return convertToDomain(context, experimentStudentMapper.queryByIds(ids), isFillUserInfo);
    }

    @Override
    public Integer queryTotal(ExperimentStudentQuery query) {
        return experimentStudentMapper.queryTotal(query);
    }

    @Override
    public ExperimentStudentDomain queryByUserIdAndClassId(Integer userId, Integer classId) {
        return ExperimentStudentConvert.modelToDomain(experimentStudentMapper.queryByUserIdAndClassId(userId, classId));
    }

    @Override
    public void save(ExperimentStudentDomain record) {
        experimentStudentMapper.save(ExperimentStudentConvert.domainToModel(record));
        redisCache.deleteByPre(CacheBizName.EXPERIMENT_STUDENT_PAGE, record.getClassId() + "::" + ApplyClassStatus.PADDING.value());
    }

    @Override
    public void allow(ExperimentReviewStudentInfo record) {
        experimentStudentMapper.allow(record);
        // 状态从 padding => allow
        redisCache.deleteByPre(CacheBizName.EXPERIMENT_STUDENT_PAGE, record.getClassId());
    }

    @Override
    public void reject(ExperimentReviewStudentInfo record) {
        experimentStudentMapper.reject(record);
        // 状态从 padding => 删除记录
        redisCache.deleteByPre(CacheBizName.EXPERIMENT_STUDENT_PAGE, record.getClassId() + "::" + ApplyClassStatus.PADDING.value());
    }

    @Override
    public void resetGroup(Integer classId,  List<Integer> groupIds) {
        experimentStudentMapper.resetGroup(classId, groupIds);
        redisCache.deleteByPre(CacheBizName.EXPERIMENT_STUDENT_PAGE, getClass() + "::" + ApplyClassStatus.ALLOWED.value());
    }

    private List<ExperimentStudentDomain> convertToDomain(Context context, List<ExperimentStudentModel> models, boolean isFillUserInfo) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        List<ExperimentStudentDomain> domains = models.stream().map(ExperimentStudentConvert::modelToDomain).collect(Collectors.toList());
        // fill teacher info
        if (isFillUserInfo) {
            List<Integer> userIds = domains.stream().map(ExperimentStudentDomain::getUserId).collect(Collectors.toList());
            Map<Integer, UserInfo> userInfoMap = userCenterService.queryByUserIds(context, userIds);
            domains.forEach(item -> item.setStudentInfo(userInfoMap.get(item.getUserId())));
        }
        return domains;
    }
}
