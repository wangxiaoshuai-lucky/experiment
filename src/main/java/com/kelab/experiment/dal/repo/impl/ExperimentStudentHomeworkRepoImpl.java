package com.kelab.experiment.dal.repo.impl;

import com.kelab.experiment.constant.enums.HomeWorkType;
import com.kelab.experiment.convert.ExperimentStudentHomeworkConvert;
import com.kelab.experiment.dal.dao.ExperimentStudentHomeworkMapper;
import com.kelab.experiment.dal.domain.ExperimentGroupDomain;
import com.kelab.experiment.dal.domain.ExperimentHomeworkDomain;
import com.kelab.experiment.dal.domain.ExperimentStudentHomeworkDomain;
import com.kelab.experiment.dal.domain.HomeworkSubmitDomain;
import com.kelab.experiment.dal.model.ExperimentStudentHomeworkModel;
import com.kelab.experiment.dal.repo.ExperimentGroupRepo;
import com.kelab.experiment.dal.repo.ExperimentHomeworkRepo;
import com.kelab.experiment.dal.repo.ExperimentStudentHomeworkRepo;
import com.kelab.experiment.support.service.UserCenterService;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentStudentHomeworkQuery;
import com.kelab.info.usercenter.info.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ExperimentStudentHomeworkRepoImpl implements ExperimentStudentHomeworkRepo {

    private ExperimentStudentHomeworkMapper experimentStudentHomeworkMapper;

    private ExperimentHomeworkRepo experimentHomeworkRepo;

    private ExperimentGroupRepo experimentGroupRepo;

    private UserCenterService userCenterService;

    @Autowired(required = false)
    public ExperimentStudentHomeworkRepoImpl(ExperimentStudentHomeworkMapper experimentStudentHomeworkMapper,
                                             ExperimentHomeworkRepo experimentHomeworkRepo,
                                             ExperimentGroupRepo experimentGroupRepo,
                                             UserCenterService userCenterService) {
        this.experimentStudentHomeworkMapper = experimentStudentHomeworkMapper;
        this.experimentHomeworkRepo = experimentHomeworkRepo;
        this.experimentGroupRepo = experimentGroupRepo;
        this.userCenterService = userCenterService;
    }

    @Override
    public List<ExperimentStudentHomeworkDomain> queryPage(Context context,
                                                           ExperimentStudentHomeworkQuery query,
                                                           boolean isFillSubmitInfo) {
        return convertToDomain(context, experimentStudentHomeworkMapper.queryPage(query), isFillSubmitInfo);
    }

    @Override
    public List<ExperimentStudentHomeworkDomain> queryByIds(Context context, List<Integer> ids, boolean isFillSubmitInfo) {
        return convertToDomain(context, experimentStudentHomeworkMapper.queryByIds(ids), isFillSubmitInfo);
    }

    @Override
    public Integer queryTotal(ExperimentStudentHomeworkQuery query) {
        return experimentStudentHomeworkMapper.queryTotal(query);
    }

    @Override
    public List<ExperimentStudentHomeworkDomain> queryAllByHomeworkIdsAndTargetIds(Context context,
                                                                                   List<Integer> homeworkIds,
                                                                                   List<Integer> targetIds,
                                                                                   boolean isFillSubmitInfo) {
        return convertToDomain(context, experimentStudentHomeworkMapper.queryAllByHomeworkIdsAndTargetIds(homeworkIds, targetIds), isFillSubmitInfo);
    }

    @Override
    public void save(ExperimentStudentHomeworkDomain record) {
        experimentStudentHomeworkMapper.save(ExperimentStudentHomeworkConvert.domainToModel(record));
    }

    @Override
    public void update(ExperimentStudentHomeworkDomain record) {
        experimentStudentHomeworkMapper.update(ExperimentStudentHomeworkConvert.domainToModel(record));
    }

    @Override
    public void deleteByHomeworkId(Integer homeworkId) {
        experimentStudentHomeworkMapper.deleteByHomeworkId(homeworkId);
    }

    @Override
    public List<HomeworkSubmitDomain> queryTotalByHomeworkIds(List<Integer> homeworkIds) {
        return experimentStudentHomeworkMapper.queryTotalByHomeworkIds(homeworkIds);
    }

    @Override
    public List<ExperimentStudentHomeworkDomain> queryByHomeworkIds(List<Integer> homeworkIds) {
        return convertToDomain(null, experimentStudentHomeworkMapper.queryByHomeworkIds(homeworkIds), false);
    }

    private List<ExperimentStudentHomeworkDomain> convertToDomain(Context context, List<ExperimentStudentHomeworkModel> models, boolean isFillSubmitInfo) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        List<ExperimentStudentHomeworkDomain> domains = models.stream().map(ExperimentStudentHomeworkConvert::modelToDomain).collect(Collectors.toList());
        if (context != null && isFillSubmitInfo) {
            // 填充提交信息(个人信息或者分组信息)
            // 查询作业列表获取作业的类型
            List<ExperimentHomeworkDomain> homework = experimentHomeworkRepo.queryByIds(domains.stream().map(ExperimentStudentHomeworkDomain::getHomeworkId).collect(Collectors.toList()));
            // 对于个人作业和分组作业进行分组
            Set<Integer> groupHomeworkSet = homework.stream().filter(item -> item.getType() == HomeWorkType.GROUP).map(ExperimentHomeworkDomain::getId).collect(Collectors.toSet());
            // 涉及到的分组信息
            List<Integer> groupIds = domains.stream().filter(item -> groupHomeworkSet.contains(item.getHomeworkId())).map(ExperimentStudentHomeworkDomain::getTargetId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(groupIds)) {
                Map<Integer, ExperimentGroupDomain> groups = experimentGroupRepo.queryByIds(context, groupIds, true)
                        .stream().collect(Collectors.toMap(ExperimentGroupDomain::getId, obj -> obj, (v1, v2) -> v2));
                domains.forEach(item -> {
                    if (groupHomeworkSet.contains(item.getHomeworkId())) {
                        item.setPostGroupInfo(groups.get(item.getTargetId()));
                    }
                });
            }
            // 设计到的个人信息
            List<Integer> userIds = domains.stream().filter(item -> !groupHomeworkSet.contains(item.getHomeworkId())).map(ExperimentStudentHomeworkDomain::getTargetId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(userIds)) {
                Map<Integer, UserInfo> userInfoMap = userCenterService.queryByUserIds(context, userIds);
                domains.forEach(item -> {
                    if (!groupHomeworkSet.contains(item.getHomeworkId())) {
                        item.setPostUserInfo(userInfoMap.get(item.getTargetId()));
                    }
                });
            }
        }
        return domains;
    }
}
