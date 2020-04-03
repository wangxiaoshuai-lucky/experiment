package com.kelab.experiment.convert;

import com.kelab.experiment.dal.domain.ExperimentClassDomain;
import com.kelab.experiment.dal.model.ExperimentClassModel;
import com.kelab.info.experiment.info.ExperimentClassInfo;
import org.springframework.beans.BeanUtils;

public class ExperimentClassConvert {

    public static ExperimentClassDomain modelToDomain(ExperimentClassModel model) {
        if (model == null) {
            return null;
        }
        ExperimentClassDomain domain = new ExperimentClassDomain();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static ExperimentClassModel domainToModel(ExperimentClassDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentClassModel model = new ExperimentClassModel();
        BeanUtils.copyProperties(domain, model);
        return model;
    }

    public static ExperimentClassInfo domainToInfo(ExperimentClassDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentClassInfo info = new ExperimentClassInfo();
        BeanUtils.copyProperties(domain, info);
        return info;
    }

    public static ExperimentClassDomain infoToDomain(ExperimentClassInfo info) {
        if (info == null) {
            return null;
        }
        ExperimentClassDomain domain = new ExperimentClassDomain();
        BeanUtils.copyProperties(info, domain);
        return domain;
    }
}
