package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentChatDomain;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentChatQuery;

import java.util.List;

public interface ExperimentChatRepo {

    List<ExperimentChatDomain> queryPage(Context context, ExperimentChatQuery query);

    Integer queryTotal(ExperimentChatQuery query);

    ExperimentChatDomain queryById(Context context, Integer id);

    void save(ExperimentChatDomain record);
}
