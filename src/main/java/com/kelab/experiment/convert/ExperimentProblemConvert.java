package com.kelab.experiment.convert;

import com.kelab.experiment.dal.domain.ExperimentProblemDomain;
import com.kelab.experiment.dal.model.ExperimentProblemModel;
import com.kelab.info.experiment.info.ExperimentProblemInfo;
import org.springframework.beans.BeanUtils;

public class ExperimentProblemConvert {

    public static ExperimentProblemDomain modelToDomain(ExperimentProblemModel model) {
        if (model == null) {
            return null;
        }
        ExperimentProblemDomain domain = new ExperimentProblemDomain();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static ExperimentProblemModel domainToModel(ExperimentProblemDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentProblemModel model = new ExperimentProblemModel();
        BeanUtils.copyProperties(domain, model);
        return model;
    }

    public static ExperimentProblemInfo domainToInfo(ExperimentProblemDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentProblemInfo info = new ExperimentProblemInfo();
        BeanUtils.copyProperties(domain, info);
        return info;
    }

    public static ExperimentProblemDomain infoToDomain(ExperimentProblemInfo info) {
        if (info == null) {
            return null;
        }
        ExperimentProblemDomain domain = new ExperimentProblemDomain();
        BeanUtils.copyProperties(info, domain);
        return domain;
    }
}
