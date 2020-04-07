package com.kelab.experiment.service.impl;

import com.google.common.base.Preconditions;
import com.kelab.experiment.constant.enums.HomeWorkType;
import com.kelab.experiment.convert.ExperimentHomeworkConvert;
import com.kelab.experiment.dal.domain.ExperimentGroupDomain;
import com.kelab.experiment.dal.domain.ExperimentHomeworkDomain;
import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.experiment.dal.domain.ExperimentStudentHomeworkDomain;
import com.kelab.experiment.dal.repo.ExperimentGroupRepo;
import com.kelab.experiment.dal.repo.ExperimentHomeworkRepo;
import com.kelab.experiment.dal.repo.ExperimentStudentHomeworkRepo;
import com.kelab.experiment.dal.repo.ExperimentStudentRepo;
import com.kelab.experiment.service.ExperimentHomeworkService;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentHomeworkInfo;
import com.kelab.info.experiment.query.ExperimentHomeworkQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExperimentHomeworkServiceImpl implements ExperimentHomeworkService {

    private ExperimentHomeworkRepo experimentHomeworkRepo;

    private ExperimentStudentHomeworkRepo experimentStudentHomeworkRepo;

    private ExperimentStudentRepo experimentStudentRepo;

    private ExperimentGroupRepo experimentGroupRepo;


    public ExperimentHomeworkServiceImpl(ExperimentHomeworkRepo experimentHomeworkRepo,
                                         ExperimentStudentHomeworkRepo experimentStudentHomeworkRepo,
                                         ExperimentStudentRepo experimentStudentRepo,
                                         ExperimentGroupRepo experimentGroupRepo) {
        this.experimentHomeworkRepo = experimentHomeworkRepo;
        this.experimentStudentHomeworkRepo = experimentStudentHomeworkRepo;
        this.experimentStudentRepo = experimentStudentRepo;
        this.experimentGroupRepo = experimentGroupRepo;
    }

    @Override
    public PaginationResult<ExperimentHomeworkInfo> queryHomeworkPage(Context context, ExperimentHomeworkQuery query) {
        PaginationResult<ExperimentHomeworkInfo> result = new PaginationResult<>();
        List<Integer> totalIds = CommonService.totalIds(query);
        if (CollectionUtils.isEmpty(totalIds)) {
            List<ExperimentHomeworkInfo> infos = convertToInfo(context, experimentHomeworkRepo.queryPage(query));
            result.setPagingList(infos);
            result.setTotal(experimentHomeworkRepo.queryTotal(query));
        } else {
            List<ExperimentHomeworkInfo> infos = convertToInfo(context, experimentHomeworkRepo.queryByIds(totalIds));
            result.setPagingList(infos);
            result.setTotal(infos.size());
        }
        return result;
    }

    @Override
    public void createHomework(Context context, ExperimentHomeworkDomain record) {
        checkGroup(record);
        experimentHomeworkRepo.save(record);
    }

    @Override
    public void updateHomework(Context context, ExperimentHomeworkDomain record) {
        checkGroup(record);
        experimentHomeworkRepo.update(record);
    }

    private void checkGroup(ExperimentHomeworkDomain record) {
        if (record.getType() == HomeWorkType.GROUP) {
            List<ExperimentHomeworkDomain> old = experimentHomeworkRepo.queryByIds(Collections.singletonList(record.getId()));
            Preconditions.checkArgument(!CollectionUtils.isEmpty(old), "作业不存在");
            List<ExperimentGroupDomain> groupDomains = experimentGroupRepo.queryAllByClassId(null, old.get(0).getClassId(), false);
            Preconditions.checkArgument(!CollectionUtils.isEmpty(groupDomains), "班级没有分组，不可以布置分组作业");
        }
    }

    @Override
    public void deleteHomework(Context context, List<Integer> ids) {
        experimentHomeworkRepo.delete(ids);
    }

    private List<ExperimentHomeworkInfo> convertToInfo(Context context, List<ExperimentHomeworkDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        // 填充自己的作业信息
        // 查询自己所在分组信息，查询分组作业
        ExperimentStudentDomain studentDomain = experimentStudentRepo.queryByUserIdAndClassId(context.getOperatorId(), domains.get(0).getClassId());
        List<ExperimentStudentHomeworkDomain> studentHomework = experimentStudentHomeworkRepo.queryAllByHomeworkIdsAndTargetIds(
                domains.stream().map(ExperimentHomeworkDomain::getId).collect(Collectors.toList()),
                Arrays.asList(context.getOperatorId(), studentDomain.getGroupId()));
        // Map<作业 ID::提交人 ID, 学生作业> 这里的 targetId 有可能是用户 id 也有可能是分组 id
        Map<String, ExperimentStudentHomeworkDomain> studentHomeworkMap = studentHomework.stream().collect(
                Collectors.toMap(item -> item.getHomeworkId() + "::" + item.getTargetId(), obj -> obj, (v1, v2) -> v2));
        domains.forEach(item -> {
            switch (item.getType()) {
                case PERSON:
                    item.setSubmitInfo(studentHomeworkMap.get(item.getId() + "::" + context.getOperatorId()));
                    break;
                case GROUP:
                    item.setSubmitInfo(studentHomeworkMap.get(item.getId() + "::" + studentDomain.getGroupId()));
                    break;
                default:
            }
        });
        return domains.stream().map(ExperimentHomeworkConvert::domainToInfo).collect(Collectors.toList());
    }
}
