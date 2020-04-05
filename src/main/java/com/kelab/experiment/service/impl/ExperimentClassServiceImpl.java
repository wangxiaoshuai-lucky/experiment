package com.kelab.experiment.service.impl;

import com.alibaba.fastjson.JSON;
import com.kelab.experiment.constant.enums.ApplyClassStatus;
import com.kelab.experiment.convert.ExperimentClassConvert;
import com.kelab.experiment.convert.ExperimentStudentConvert;
import com.kelab.experiment.dal.domain.ExperimentClassDomain;
import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.experiment.dal.repo.ExperimentClassRepo;
import com.kelab.experiment.dal.repo.ExperimentStudentRepo;
import com.kelab.experiment.service.ExperimentClassService;
import com.kelab.experiment.support.ContextLogger;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.base.constant.UserRoleConstant;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentClassInfo;
import com.kelab.info.experiment.info.ExperimentReviewStudentInfo;
import com.kelab.info.experiment.info.ExperimentStudentInfo;
import com.kelab.info.experiment.query.ExperimentClassQuery;
import com.kelab.info.experiment.query.ExperimentStudentQuery;
import com.kelab.util.uuid.UuidUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentClassServiceImpl implements ExperimentClassService {

    private ExperimentClassRepo experimentClassRepo;

    private ExperimentStudentRepo experimentStudentRepo;

    private ContextLogger contextLogger;

    public ExperimentClassServiceImpl(ExperimentClassRepo experimentClassRepo,
                                      ExperimentStudentRepo experimentStudentRepo,
                                      ContextLogger contextLogger) {
        this.experimentClassRepo = experimentClassRepo;
        this.experimentStudentRepo = experimentStudentRepo;
        this.contextLogger = contextLogger;
    }

    @Override
    public PaginationResult<ExperimentClassInfo> queryPage(Context context, ExperimentClassQuery query) {
        if (context.getOperatorRoleId() == UserRoleConstant.TEACHER) {
            // 搜索自己创建的班级
            query.setTeacherId(context.getOperatorId());
        }
        PaginationResult<ExperimentClassInfo> result = new PaginationResult<>();
        List<Integer> ids = CommonService.totalIds(query);
        // 指定id 则返回指定id
        if (!CollectionUtils.isEmpty(ids)) {
            List<ExperimentClassInfo> infos = convertToInfo(experimentClassRepo.queryByIds(context, ids, true));
            result.setPagingList(infos);
            result.setTotal(infos.size());
        } else {
            result.setPagingList(convertToInfo(experimentClassRepo.queryPage(context, query, true)));
            result.setTotal(experimentClassRepo.queryTotal(query));
        }
        return result;
    }

    @Override
    public PaginationResult<ExperimentClassInfo> queryPageForUser(Context context, ExperimentStudentQuery query) {
        // 查看操作本人所加班级
        query.setUserId(context.getOperatorId());
        PaginationResult<ExperimentClassInfo> result = new PaginationResult<>();
        List<ExperimentStudentDomain> studentRef = experimentStudentRepo.queryByUserId(query);
        if (!CollectionUtils.isEmpty(studentRef)) {
            List<ExperimentClassInfo> experimentClassInfos = convertToInfo(experimentClassRepo.queryByIds(context,
                    studentRef.stream().map(ExperimentStudentDomain::getClassId).collect(Collectors.toList()), true));
            result.setTotal(experimentClassInfos.size());
            result.setPagingList(experimentClassInfos);
        } else {
            result.setPagingList(Collections.emptyList());
            result.setTotal(0);
        }
        return result;
    }

    @Override
    public void createExperimentClass(Context context, ExperimentClassDomain domain) {
        domain.setTeacherId(context.getOperatorId());
        domain.setCreateTime(System.currentTimeMillis());
        domain.setClassCode(UuidUtil.genUUID());
        experimentClassRepo.save(domain);
    }

    @Override
    public void updateExperimentClass(Context context, ExperimentClassDomain domain) {
        experimentClassRepo.update(domain);
    }

    @Override
    public void deleteExperimentClass(Context context, List<Integer> ids) {
        List<ExperimentClassDomain> old = experimentClassRepo.queryByIds(context, ids, true);
        experimentClassRepo.delete(ids);
        contextLogger.info(context, "删除班级: %s", JSON.toJSONString(old));
    }

    @Override
    public PaginationResult<ExperimentStudentInfo> queryStudentPage(Context context, ExperimentStudentQuery query) {
        PaginationResult<ExperimentStudentInfo> result = new PaginationResult<>();
        result.setPagingList(convertToStudentInfo(experimentStudentRepo.queryPage(context, query, true)));
        result.setTotal(experimentStudentRepo.queryTotal(query));
        return result;
    }

    @Override
    public ExperimentClassInfo applyClass(Context context, String classCode) {
        ExperimentClassDomain classDomain = experimentClassRepo.queryByCode(classCode);
        if (classDomain == null) {
            return null;
        }
        // 查看是否在之前申请过
        ExperimentStudentDomain old = experimentStudentRepo.queryByUserIdAndClassId(context.getOperatorId(), classDomain.getId());
        if (old == null) {
            ExperimentStudentDomain studentDomain = new ExperimentStudentDomain();
            studentDomain.setStatus(ApplyClassStatus.PADDING);
            studentDomain.setUserId(context.getOperatorId());
            studentDomain.setClassId(classDomain.getId());
            experimentStudentRepo.save(studentDomain);
        }
        return ExperimentClassConvert.domainToInfo(classDomain);
    }

    @Override
    public void reviewStudentApply(Context context, ExperimentReviewStudentInfo record) {
        if (record.getStatus().equals(ApplyClassStatus.REJECTED.value())) {
            experimentStudentRepo.reject(record);
        } else if (record.getStatus().equals(ApplyClassStatus.ALLOWED.value())) {
            experimentStudentRepo.allow(record);
        }
    }

    private List<ExperimentClassInfo> convertToInfo(List<ExperimentClassDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentClassConvert::domainToInfo).collect(Collectors.toList());
    }

    private List<ExperimentStudentInfo> convertToStudentInfo(List<ExperimentStudentDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentStudentConvert::domainToInfo).collect(Collectors.toList());
    }
}
