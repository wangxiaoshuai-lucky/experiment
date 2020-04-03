package com.kelab.experiment.convert;

import com.kelab.experiment.constant.enums.ApplyClassStatus;
import com.kelab.experiment.dal.domain.ExperimentStudentDomain;
import com.kelab.experiment.dal.model.ExperimentStudentModel;
import com.kelab.info.experiment.info.ExperimentStudentInfo;
import org.springframework.beans.BeanUtils;

public class ExperimentStudentConvert {

    public static ExperimentStudentDomain modelToDomain(ExperimentStudentModel model) {
        if (model == null) {
            return null;
        }
        ExperimentStudentDomain domain = new ExperimentStudentDomain();
        BeanUtils.copyProperties(model, domain);
        domain.setStatus(ApplyClassStatus.valueOf(model.getStatus()));
        return domain;
    }

    public static ExperimentStudentModel domainToModel(ExperimentStudentDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentStudentModel model = new ExperimentStudentModel();
        BeanUtils.copyProperties(domain, model);
        model.setStatus(domain.getStatus().value());
        return model;
    }

    public static ExperimentStudentInfo domainToInfo(ExperimentStudentDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentStudentInfo info = new ExperimentStudentInfo();
        BeanUtils.copyProperties(domain, info);
        info.setStatus(domain.getStatus().value());
        return info;
    }

    public static ExperimentStudentDomain infoToDomain(ExperimentStudentInfo info) {
        if (info == null) {
            return null;
        }
        ExperimentStudentDomain domain = new ExperimentStudentDomain();
        BeanUtils.copyProperties(info, domain);
        domain.setStatus(ApplyClassStatus.valueOf(info.getStatus()));
        return domain;
    }
}
