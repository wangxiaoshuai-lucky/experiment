package com.kelab.experiment.dal.repo;

import com.kelab.experiment.dal.domain.ExperimentClassDomain;
import com.kelab.info.context.Context;
import com.kelab.info.experiment.query.ExperimentClassQuery;

import java.util.List;

public interface ExperimentClassRepo {

    List<ExperimentClassDomain> queryPage(Context context, ExperimentClassQuery query, boolean isFillUserInfo);

    List<ExperimentClassDomain> queryByIds(Context context, List<Integer> ids, boolean isFillUserInfo);

    Integer queryTotal(ExperimentClassQuery query);

    ExperimentClassDomain queryByCode(String code);

    void save(ExperimentClassDomain domain);

    void update(ExperimentClassDomain domain);

    void delete(List<Integer> ids);
}
