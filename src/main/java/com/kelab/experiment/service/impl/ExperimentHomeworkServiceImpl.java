package com.kelab.experiment.service.impl;

import com.google.common.base.Preconditions;
import com.kelab.experiment.constant.enums.ApplyClassStatus;
import com.kelab.experiment.constant.enums.HomeWorkType;
import com.kelab.experiment.convert.ExperimentHomeworkConvert;
import com.kelab.experiment.convert.ExperimentStudentHomeworkConvert;
import com.kelab.experiment.dal.domain.*;
import com.kelab.experiment.dal.repo.*;
import com.kelab.experiment.service.ExperimentHomeworkService;
import com.kelab.experiment.support.service.ProblemCenterService;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentHomeworkInfo;
import com.kelab.info.experiment.info.ExperimentStudentHomeworkInfo;
import com.kelab.info.experiment.query.ExperimentHomeworkQuery;
import com.kelab.info.experiment.query.ExperimentStudentHomeworkQuery;
import com.kelab.info.experiment.query.ExperimentStudentQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExperimentHomeworkServiceImpl implements ExperimentHomeworkService {

    private ExperimentHomeworkRepo experimentHomeworkRepo;

    private ExperimentStudentHomeworkRepo experimentStudentHomeworkRepo;

    private ExperimentStudentRepo experimentStudentRepo;

    private ExperimentGroupRepo experimentGroupRepo;

    private ExperimentClassRepo experimentClassRepo;

    private ExperimentContestRepo experimentContestRepo;


    private ProblemCenterService problemCenterService;


    public ExperimentHomeworkServiceImpl(ExperimentHomeworkRepo experimentHomeworkRepo,
                                         ExperimentStudentHomeworkRepo experimentStudentHomeworkRepo,
                                         ExperimentStudentRepo experimentStudentRepo,
                                         ExperimentGroupRepo experimentGroupRepo,
                                         ExperimentClassRepo experimentClassRepo,
                                         ExperimentContestRepo experimentContestRepo,
                                         ProblemCenterService problemCenterService) {
        this.experimentHomeworkRepo = experimentHomeworkRepo;
        this.experimentStudentHomeworkRepo = experimentStudentHomeworkRepo;
        this.experimentStudentRepo = experimentStudentRepo;
        this.experimentGroupRepo = experimentGroupRepo;
        this.experimentClassRepo = experimentClassRepo;
        this.experimentContestRepo = experimentContestRepo;
        this.problemCenterService = problemCenterService;
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
        List<ExperimentHomeworkDomain> old = experimentHomeworkRepo.queryByIds(Collections.singletonList(record.getId()));
        Preconditions.checkArgument(!CollectionUtils.isEmpty(old), "作业不存在");
        // 如果更改了作业的类型，那么删除之前所有的提交作业
        if (record.getType() != null && record.getType() != old.get(0).getType()) {
            experimentStudentHomeworkRepo.deleteByHomeworkId(old.get(0).getId());
        }
        experimentHomeworkRepo.update(record);
    }

    private void checkGroup(ExperimentHomeworkDomain record) {
        if (record.getType() == HomeWorkType.GROUP) {
            List<ExperimentGroupDomain> groupDomains = experimentGroupRepo.queryAllByClassId(null, record.getClassId(), false);
            Preconditions.checkArgument(!CollectionUtils.isEmpty(groupDomains), "班级没有分组，不可以布置分组作业");
        }
    }

    @Override
    public void deleteHomework(Context context, List<Integer> ids) {
        experimentHomeworkRepo.delete(ids);
    }

    @Override
    public PaginationResult<ExperimentStudentHomeworkInfo> queryStudentHomeworkPage(Context context, ExperimentStudentHomeworkQuery query) {
        PaginationResult<ExperimentStudentHomeworkInfo> result = new PaginationResult<>();
        // 获取作业的布置信息
        List<ExperimentHomeworkDomain> homework = experimentHomeworkRepo.queryByIds(Collections.singletonList(query.getHomeworkId()));
        Preconditions.checkArgument(!CollectionUtils.isEmpty(homework), "作业不存在");
        List<Integer> ids = CommonService.totalIds(query);
        if (CollectionUtils.isEmpty(ids)) {
            List<ExperimentStudentHomeworkInfo> infos = convertToStudentHomeworkInfo(context, experimentStudentHomeworkRepo.queryPage(context, query, true));
            result.setPagingList(infos);
            result.setTotal(experimentStudentHomeworkRepo.queryTotal(query));
        } else {
            List<ExperimentStudentHomeworkInfo> infos = convertToStudentHomeworkInfo(context, experimentStudentHomeworkRepo.queryByIds(context, ids, true));
            result.setPagingList(infos);
            result.setTotal(infos.size());
        }
        return result;
    }

    @Override
    public void submitHomework(Context context, ExperimentStudentHomeworkDomain record) {
        List<ExperimentHomeworkDomain> homeworkList = experimentHomeworkRepo.queryByIds(Collections.singletonList(record.getHomeworkId()));
        Preconditions.checkArgument(!CollectionUtils.isEmpty(homeworkList), "作业不存在");
        ExperimentHomeworkDomain homework = homeworkList.get(0);
        // 已经到了截至时间
        Preconditions.checkArgument(homework.getEndTime() > System.currentTimeMillis(), "到达截至时间");
        // 分组作业
        if (homework.getType() == HomeWorkType.GROUP) {
            ExperimentStudentDomain student = experimentStudentRepo.queryByUserIdAndClassId(context.getOperatorId(), homework.getClassId());
            Preconditions.checkArgument(student.getGroupId() != 0, "你没有加组");
            record.setTargetId(student.getGroupId());
        } else {
            record.setTargetId(context.getOperatorId());
        }
        // 教师未批改的状态
        record.setScore(0);
        record.setComment(null);
        record.setCommentTime(null);
        record.setPostTime(System.currentTimeMillis());
        // 查找之前的提交情况, 如果有就替换
        List<ExperimentStudentHomeworkDomain> oldSubmit = experimentStudentHomeworkRepo.queryAllByHomeworkIdsAndTargetIds(context,
                Collections.singletonList(homework.getId()),
                Collections.singletonList(record.getTargetId()),
                false);
        if (!CollectionUtils.isEmpty(oldSubmit)) {
            record.setId(oldSubmit.get(0).getId());
            experimentStudentHomeworkRepo.update(record);
        } else {
            experimentStudentHomeworkRepo.save(record);
        }
    }

    @Override
    public void reviewHomework(Context context, ExperimentStudentHomeworkDomain record) {
        Preconditions.checkArgument(record.getScore() > 0, "不能低于1分哦");
        Preconditions.checkArgument(record.getScore() <= 100, "不能超过100分");
        record.setAttachName(null);
        record.setAttachUrl(null);
        record.setPostTime(null);
        record.setCommentTime(System.currentTimeMillis());
        experimentStudentHomeworkRepo.update(record);
    }

    private List<ExperimentStudentHomeworkInfo> convertToStudentHomeworkInfo(Context context, List<ExperimentStudentHomeworkDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentStudentHomeworkConvert::domainToInfo).collect(Collectors.toList());
    }

    private List<ExperimentHomeworkInfo> convertToInfo(Context context, List<ExperimentHomeworkDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        // 填充自己的作业信息
        // 查询自己所在分组信息，查询分组作业
        ExperimentStudentDomain studentDomain = experimentStudentRepo.queryByUserIdAndClassId(context.getOperatorId(), domains.get(0).getClassId());
        List<ExperimentStudentHomeworkDomain> studentHomework = experimentStudentHomeworkRepo.queryAllByHomeworkIdsAndTargetIds(context,
                domains.stream().map(ExperimentHomeworkDomain::getId).collect(Collectors.toList()),
                Arrays.asList(context.getOperatorId(), studentDomain.getGroupId()),
                true);
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
        fillSubmitNumberAndTotal(context, domains);
        return domains.stream().map(ExperimentHomeworkConvert::domainToInfo).collect(Collectors.toList());
    }

    private void fillSubmitNumberAndTotal(Context context, List<ExperimentHomeworkDomain> domains) {
        // 班级的所有分组
        List<ExperimentGroupDomain> groupDomains = experimentGroupRepo.queryAllByClassId(context, domains.get(0).getClassId(), false);
        // 所有的学生
        ExperimentStudentQuery query = new ExperimentStudentQuery();
        query.setStatus(ApplyClassStatus.ALLOWED.value());
        query.setClassId(domains.get(0).getClassId());
        Integer totalUser = experimentStudentRepo.queryTotal(query);
        Map<Integer, Integer> submitTotal = experimentStudentHomeworkRepo.queryTotalByHomeworkIds(
                domains.stream().map(ExperimentHomeworkDomain::getId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(HomeworkSubmitDomain::getHomeworkId, HomeworkSubmitDomain::getTotalNum));
        domains.forEach(item -> {
            switch (item.getType()) {
                case PERSON:
                    item.setTotalNum(totalUser);
                    item.setPostNum(submitTotal.getOrDefault(item.getId(), 0));
                    break;
                case GROUP:
                    item.setTotalNum(groupDomains.size());
                    item.setPostNum(submitTotal.getOrDefault(item.getId(), 0));
                    break;
                default:
            }
        });
    }

}
