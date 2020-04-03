package com.kelab.experiment.dal.repo.impl;

import com.kelab.experiment.convert.ExperimentClassConvert;
import com.kelab.experiment.dal.dao.ExperimentClassMapper;
import com.kelab.experiment.dal.domain.ExperimentClassDomain;
import com.kelab.experiment.dal.model.ExperimentClassModel;
import com.kelab.experiment.dal.repo.ExperimentClassRepo;
import com.kelab.experiment.support.service.UserCenterService;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentClassQuery;
import com.kelab.info.usercenter.info.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ExperimentClassRepoImpl implements ExperimentClassRepo {

    private ExperimentClassMapper experimentClassMapper;

    private UserCenterService userCenterService;

    @Autowired(required = false)
    public ExperimentClassRepoImpl(ExperimentClassMapper experimentClassMapper,
                                   UserCenterService userCenterService) {
        this.experimentClassMapper = experimentClassMapper;
        this.userCenterService = userCenterService;
    }

    @Override
    public List<ExperimentClassDomain> queryPage(Context context, ExperimentClassQuery query) {
        return convertToDomain(context, experimentClassMapper.queryPage(query));
    }

    @Override
    public List<ExperimentClassDomain> queryByIds(Context context, List<Integer> ids) {
        return convertToDomain(context, experimentClassMapper.queryByIds(ids));
    }

    @Override
    public Integer queryTotal(ExperimentClassQuery query) {
        return experimentClassMapper.queryTotal(query);
    }

    @Override
    public ExperimentClassDomain queryByCode(String code) {
        return ExperimentClassConvert.modelToDomain(experimentClassMapper.queryByCode(code));
    }

    @Override
    public void save(ExperimentClassDomain domain) {
        ExperimentClassModel model = ExperimentClassConvert.domainToModel(domain);
        experimentClassMapper.save(model);
        domain.setId(model.getId());
    }

    @Override
    public void update(ExperimentClassDomain domain) {
        ExperimentClassModel model = ExperimentClassConvert.domainToModel(domain);
        experimentClassMapper.update(model);
    }

    @Override
    public void delete(List<Integer> ids) {
        experimentClassMapper.delete(ids);
    }

    private List<ExperimentClassDomain> convertToDomain(Context context, List<ExperimentClassModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        List<ExperimentClassDomain> domains = models.stream().map(ExperimentClassConvert::modelToDomain).collect(Collectors.toList());
        // fill teacher info
        List<Integer> teacherIds = domains.stream().map(ExperimentClassDomain::getTeacherId).collect(Collectors.toList());
        Map<Integer, UserInfo> userInfoMap = userCenterService.queryByUserIds(context, teacherIds)
                .stream().collect((Collectors.toMap(UserInfo::getId, obj -> obj, (v1, v2) -> v2)));
        domains.forEach(item -> item.setTeacherInfo(userInfoMap.get(item.getTeacherId())));
        return domains;
    }
}
