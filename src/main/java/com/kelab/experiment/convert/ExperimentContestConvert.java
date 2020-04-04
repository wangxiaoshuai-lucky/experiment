package com.kelab.experiment.convert;

import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.experiment.dal.model.ExperimentContestModel;
import com.kelab.info.experiment.info.ExperimentContestInfo;
import org.springframework.beans.BeanUtils;

public class ExperimentContestConvert {

    public static ExperimentContestDomain modelToDomain(ExperimentContestModel model) {
        if (model == null) {
            return null;
        }
        ExperimentContestDomain domain = new ExperimentContestDomain();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static ExperimentContestModel domainToModel(ExperimentContestDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentContestModel model = new ExperimentContestModel();
        BeanUtils.copyProperties(domain, model);
        return model;
    }

    public static ExperimentContestInfo domainToInfo(ExperimentContestDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentContestInfo info = new ExperimentContestInfo();
        BeanUtils.copyProperties(domain, info);
        return info;
    }

    public static ExperimentContestDomain infoToDomain(ExperimentContestInfo info) {
        if (info == null) {
            return null;
        }
        ExperimentContestDomain domain = new ExperimentContestDomain();
        BeanUtils.copyProperties(info, domain);
        return domain;
    }
}
