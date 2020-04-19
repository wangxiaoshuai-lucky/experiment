package com.kelab.experiment.dal.repo.impl;

import com.kelab.experiment.convert.ExperimentChatConvert;
import com.kelab.experiment.dal.dao.ExperimentChatMapper;
import com.kelab.experiment.dal.domain.ExperimentChatDomain;
import com.kelab.experiment.dal.model.ExperimentChatModel;
import com.kelab.experiment.dal.repo.ExperimentChatRepo;
import com.kelab.experiment.support.service.UserCenterService;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentChatQuery;
import com.kelab.info.usercenter.info.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ExperimentChatRepoImpl implements ExperimentChatRepo {

    private ExperimentChatMapper experimentChatMapper;

    private UserCenterService userCenterService;

    @Autowired(required = false)
    public ExperimentChatRepoImpl(ExperimentChatMapper experimentChatMapper,
                                  UserCenterService userCenterService) {
        this.experimentChatMapper = experimentChatMapper;
        this.userCenterService = userCenterService;
    }


    @Override

    public List<ExperimentChatDomain> queryPage(Context context, ExperimentChatQuery query) {
        return convertToDomain(context, experimentChatMapper.queryPage(query));
    }

    @Override
    public Integer queryTotal(ExperimentChatQuery query) {
        return experimentChatMapper.queryTotal(query);
    }

    @Override
    public ExperimentChatDomain queryById(Context context, Integer id) {
        List<ExperimentChatDomain> domains = convertToDomain(context, Collections.singletonList(experimentChatMapper.queryById(id)));
        if (CollectionUtils.isEmpty(domains)) {
            return null;
        }
        return domains.get(0);
    }

    @Override
    public void save(ExperimentChatDomain record) {
        experimentChatMapper.save(ExperimentChatConvert.domainToModel(record));
    }

    private List<ExperimentChatDomain> convertToDomain(Context context, List<ExperimentChatModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        List<ExperimentChatDomain> domains = models.stream().map(ExperimentChatConvert::modelToDomain).collect(Collectors.toList());
        // fill student info
        List<Integer> userIds = domains.stream().map(ExperimentChatDomain::getUserId).collect(Collectors.toList());
        Map<Integer, UserInfo> userInfoMap = userCenterService.queryByUserIds(context, userIds);
        domains.forEach(item -> item.setUserInfo(userInfoMap.get(item.getUserId())));
        return domains;
    }
}
