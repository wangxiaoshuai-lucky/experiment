package com.kelab.experiment.service.impl;

import com.alibaba.fastjson.JSON;
import com.kelab.experiment.convert.ExperimentClassConvert;
import com.kelab.experiment.dal.domain.ExperimentClassDomain;
import com.kelab.experiment.dal.repo.ExperimentClassRepo;
import com.kelab.experiment.service.ExperimentClassService;
import com.kelab.experiment.support.ContextLogger;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.base.constant.UserRoleConstant;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentClassInfo;
import com.kelab.info.experiment.query.ExperimentClassQuery;
import com.kelab.util.uuid.UuidUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentClassServiceImpl implements ExperimentClassService {

    private ExperimentClassRepo experimentClassRepo;

    private ContextLogger contextLogger;

    public ExperimentClassServiceImpl(ExperimentClassRepo experimentClassRepo,
                                      ContextLogger contextLogger) {
        this.experimentClassRepo = experimentClassRepo;
        this.contextLogger = contextLogger;
    }

    @Override
    public PaginationResult<ExperimentClassInfo> queryPage(Context context, ExperimentClassQuery query) {
        if (context.getOperatorRoleId() == UserRoleConstant.TEACHER) {
            // 搜索自己创建的班级
            query.setTeacherId(context.getOperatorId());
        }
        PaginationResult<ExperimentClassInfo> result = new PaginationResult<>();
        result.setPagingList(convertToInfo(experimentClassRepo.queryPage(context, query)));
        result.setTotal(experimentClassRepo.queryTotal(query));
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
        List<ExperimentClassDomain> old = experimentClassRepo.queryByIds(context, ids);
        experimentClassRepo.delete(ids);
        contextLogger.info(context, "删除班级: %s", JSON.toJSONString(old));
    }

    private List<ExperimentClassInfo> convertToInfo(List<ExperimentClassDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentClassConvert::domainToInfo).collect(Collectors.toList());
    }
}
