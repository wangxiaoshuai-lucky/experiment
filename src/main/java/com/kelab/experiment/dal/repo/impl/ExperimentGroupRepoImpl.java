package com.kelab.experiment.dal.repo.impl;

import com.kelab.experiment.convert.ExperimentGroupConvert;
import com.kelab.experiment.dal.dao.ExperimentGroupMapper;
import com.kelab.experiment.dal.domain.ExperimentGroupDomain;
import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.experiment.dal.model.ExperimentGroupModel;
import com.kelab.experiment.dal.repo.ExperimentGroupRepo;
import com.kelab.experiment.dal.repo.ExperimentStudentRepo;
import com.kelab.info.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ExperimentGroupRepoImpl implements ExperimentGroupRepo {

    private ExperimentGroupMapper experimentGroupMapper;

    private ExperimentStudentRepo experimentStudentRepo;

    @Autowired(required = false)
    public ExperimentGroupRepoImpl(ExperimentGroupMapper experimentGroupMapper,
                                   ExperimentStudentRepo experimentStudentRepo) {
        this.experimentGroupMapper = experimentGroupMapper;
        this.experimentStudentRepo = experimentStudentRepo;
    }

    @Override
    public List<ExperimentGroupDomain> queryAllByClassId(Context context, Integer classId) {
        return convertToDomain(context, experimentGroupMapper.queryAllByClassId(classId));
    }

    @Override
    public void save(ExperimentGroupDomain record) {
        experimentGroupMapper.save(ExperimentGroupConvert.domainToModel(record));
    }

    @Override
    public void update(ExperimentGroupDomain record) {
        experimentGroupMapper.update(ExperimentGroupConvert.domainToModel(record));
    }

    @Override
    public void delete(List<Integer> ids) {
        // 修改之前的绑定的学生信息
        ExperimentGroupModel old = experimentGroupMapper.queryById(ids.get(0));
        if (old != null) {
            experimentGroupMapper.delete(ids);
            experimentStudentRepo.resetGroup(old.getClassId(), ids);
        }
    }

    private List<ExperimentGroupDomain> convertToDomain(Context context, List<ExperimentGroupModel> models) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        List<ExperimentGroupDomain> domains = models.stream().map(ExperimentGroupConvert::modelToDomain).collect(Collectors.toList());
        List<ExperimentStudentDomain> users = experimentStudentRepo.queryAllByClassId(context, models.get(0).getClassId(), true);
        if (CollectionUtils.isEmpty(users)) {
            return domains;
        }
        Map<Integer, List<ExperimentStudentDomain>> groups = users.stream().collect(Collectors.groupingBy(ExperimentStudentDomain::getGroupId, Collectors.toList()));
        domains.forEach(item -> item.setMembers(groups.get(item.getId())));
        return domains;
    }

}
