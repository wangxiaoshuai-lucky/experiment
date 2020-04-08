package com.kelab.experiment.convert;

import com.kelab.experiment.dal.domain.ExperimentStudentHomeworkDomain;
import com.kelab.experiment.dal.model.ExperimentStudentHomeworkModel;
import com.kelab.info.experiment.info.ExperimentStudentHomeworkInfo;
import org.springframework.beans.BeanUtils;

public class ExperimentStudentHomeworkConvert {

    public static ExperimentStudentHomeworkDomain modelToDomain(ExperimentStudentHomeworkModel model) {
        if (model == null) {
            return null;
        }
        ExperimentStudentHomeworkDomain domain = new ExperimentStudentHomeworkDomain();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static ExperimentStudentHomeworkModel domainToModel(ExperimentStudentHomeworkDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentStudentHomeworkModel model = new ExperimentStudentHomeworkModel();
        BeanUtils.copyProperties(domain, model);
        return model;
    }

    public static ExperimentStudentHomeworkInfo domainToInfo(ExperimentStudentHomeworkDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentStudentHomeworkInfo info = new ExperimentStudentHomeworkInfo();
        BeanUtils.copyProperties(domain, info);
        if (domain.getPostGroupInfo() != null) {
            info.setPostGroupInfo(ExperimentGroupConvert.domainToInfo(domain.getPostGroupInfo()));
        }
        return info;
    }

    public static ExperimentStudentHomeworkDomain infoToDomain(ExperimentStudentHomeworkInfo info) {
        if (info == null) {
            return null;
        }
        ExperimentStudentHomeworkDomain domain = new ExperimentStudentHomeworkDomain();
        BeanUtils.copyProperties(info, domain);
        return domain;
    }
}
