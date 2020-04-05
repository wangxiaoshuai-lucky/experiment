package com.kelab.experiment.convert;

import com.kelab.experiment.dal.domain.ExperimentGroupDomain;
import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.experiment.dal.model.ExperimentGroupModel;
import com.kelab.info.experiment.info.ExperimentGroupInfo;
import com.kelab.info.experiment.info.ExperimentStudentInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ExperimentGroupConvert {

    public static ExperimentGroupDomain modelToDomain(ExperimentGroupModel model) {
        if (model == null) {
            return null;
        }
        ExperimentGroupDomain domain = new ExperimentGroupDomain();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static ExperimentGroupModel domainToModel(ExperimentGroupDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentGroupModel model = new ExperimentGroupModel();
        BeanUtils.copyProperties(domain, model);
        return model;
    }

    public static ExperimentGroupInfo domainToInfo(ExperimentGroupDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentGroupInfo info = new ExperimentGroupInfo();
        BeanUtils.copyProperties(domain, info);
        if (!CollectionUtils.isEmpty(domain.getMembers())) {
            List<ExperimentStudentInfo> members = domain.getMembers().stream().map(ExperimentStudentConvert::domainToInfo).collect(Collectors.toList());
            info.setMembers(members);
        }
        return info;
    }

    public static ExperimentGroupDomain infoToDomain(ExperimentGroupInfo info) {
        if (info == null) {
            return null;
        }
        ExperimentGroupDomain domain = new ExperimentGroupDomain();
        BeanUtils.copyProperties(info, domain);
        if (!CollectionUtils.isEmpty(info.getMembers())) {
            List<ExperimentStudentDomain> members = info.getMembers().stream().map(ExperimentStudentConvert::infoToDomain).collect(Collectors.toList());
            domain.setMembers(members);
        }
        return domain;
    }
}
