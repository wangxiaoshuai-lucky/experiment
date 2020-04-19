package com.kelab.experiment.service.impl;

import com.kelab.experiment.convert.ExperimentChatConvert;
import com.kelab.experiment.dal.domain.ExperimentChatDomain;
import com.kelab.experiment.dal.repo.ExperimentChatRepo;
import com.kelab.experiment.service.ExperimentChatService;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentChatInfo;
import com.kelab.info.experiment.query.ExperimentChatQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentChatServiceImpl implements ExperimentChatService {

    private ExperimentChatRepo experimentChatRepo;

    public ExperimentChatServiceImpl(ExperimentChatRepo experimentChatRepo) {
        this.experimentChatRepo = experimentChatRepo;
    }

    @Override
    public PaginationResult<ExperimentChatInfo> queryPage(Context context, ExperimentChatQuery query) {
        PaginationResult<ExperimentChatInfo> result = new PaginationResult<>();
        List<ExperimentChatDomain> domainList = experimentChatRepo.queryPage(context, query);
        // 如果查询的不是根消息，追加根消息
        if (query.getId() != null) {
            ExperimentChatDomain root = experimentChatRepo.queryById(context, query.getId());
            List<ExperimentChatDomain> newList = new ArrayList<>();
            newList.add(root);
            newList.addAll(domainList);
            domainList = newList;
        }
        result.setPagingList(convertToInfo(domainList));
        result.setTotal(experimentChatRepo.queryTotal(query));
        return result;
    }

    @Override
    public void createChat(Context context, ExperimentChatDomain record) {
        record.setUserId(context.getOperatorId());
        record.setPostTime(System.currentTimeMillis());
        experimentChatRepo.save(record);
    }

    private List<ExperimentChatInfo> convertToInfo(List<ExperimentChatDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ExperimentChatConvert::domainToInfo).collect(Collectors.toList());
    }

}
