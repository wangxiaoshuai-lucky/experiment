package com.kelab.experiment.service;

import com.kelab.experiment.dal.domain.ExperimentChatDomain;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.info.ExperimentChatInfo;
import com.kelab.info.experiment.query.ExperimentChatQuery;

public interface ExperimentChatService {

    PaginationResult<ExperimentChatInfo> queryPage(Context context, ExperimentChatQuery query);

    void createChat(Context context, ExperimentChatDomain record);
}
