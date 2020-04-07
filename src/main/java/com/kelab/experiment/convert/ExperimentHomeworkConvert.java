package com.kelab.experiment.convert;

import com.kelab.experiment.constant.enums.HomeWorkType;
import com.kelab.experiment.dal.domain.ExperimentHomeworkDomain;
import com.kelab.experiment.dal.model.ExperimentHomeworkModel;
import com.kelab.info.experiment.info.ExperimentHomeworkInfo;
import org.springframework.beans.BeanUtils;

public class ExperimentHomeworkConvert {

    public static ExperimentHomeworkDomain modelToDomain(ExperimentHomeworkModel model) {
        if (model == null) {
            return null;
        }
        ExperimentHomeworkDomain domain = new ExperimentHomeworkDomain();
        BeanUtils.copyProperties(model, domain);
        domain.setType(HomeWorkType.valueOf(model.getType()));
        return domain;
    }

    public static ExperimentHomeworkModel domainToModel(ExperimentHomeworkDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentHomeworkModel model = new ExperimentHomeworkModel();
        BeanUtils.copyProperties(domain, model);
        if (domain.getType() != null) {
            model.setType(domain.getType().value());
        }
        return model;
    }

    public static ExperimentHomeworkInfo domainToInfo(ExperimentHomeworkDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentHomeworkInfo info = new ExperimentHomeworkInfo();
        BeanUtils.copyProperties(domain, info);
        if (domain.getType() != null) {
            info.setType(domain.getType().value());
        }
        if (domain.getSubmitInfo() != null) {
            info.setSubmitInfo(ExperimentStudentHomeworkConvert.domainToInfo(domain.getSubmitInfo()));
        }
        return info;
    }

    public static ExperimentHomeworkDomain infoToDomain(ExperimentHomeworkInfo info) {
        if (info == null) {
            return null;
        }
        ExperimentHomeworkDomain domain = new ExperimentHomeworkDomain();
        BeanUtils.copyProperties(info, domain);
        if (info.getType() != null) {
            domain.setType(HomeWorkType.valueOf(info.getType()));
        }
        return domain;
    }
}
