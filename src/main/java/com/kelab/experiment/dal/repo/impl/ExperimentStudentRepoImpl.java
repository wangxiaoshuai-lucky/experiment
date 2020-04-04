package com.kelab.experiment.dal.repo.impl;

import com.kelab.experiment.convert.ExperimentStudentConvert;
import com.kelab.experiment.dal.dao.ExperimentStudentMapper;
import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.experiment.dal.model.ExperimentStudentModel;
import com.kelab.experiment.dal.repo.ExperimentStudentRepo;
import com.kelab.experiment.support.service.UserCenterService;
import com.kelab.info.base.query.PageQuery;
import com.kelab.info.context.Context;
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

    @Autowired(required = false)
    public ExperimentStudentRepoImpl(ExperimentStudentMapper experimentStudentMapper,
                                     UserCenterService userCenterService) {
        this.experimentStudentMapper = experimentStudentMapper;
        this.userCenterService = userCenterService;
    }


    @Override
    public List<ExperimentStudentDomain> queryPage(Context context, ExperimentStudentQuery query, boolean isFillUserInfo) {
        return convertToDomain(context, experimentStudentMapper.queryPage(query), isFillUserInfo);
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
    public void delete(List<Integer> ids) {
        experimentStudentMapper.delete(ids);
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
    }

    @Override
    public void update(ExperimentStudentDomain record) {
        experimentStudentMapper.update(ExperimentStudentConvert.domainToModel(record));
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
