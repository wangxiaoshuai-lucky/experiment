package com.kelab.experiment.convert;

import com.kelab.experiment.dal.domain.ExperimentChatDomain;
import com.kelab.experiment.dal.model.ExperimentChatModel;
import com.kelab.info.experiment.info.ExperimentChatInfo;
import org.springframework.beans.BeanUtils;

public class ExperimentChatConvert {

    public static ExperimentChatDomain modelToDomain(ExperimentChatModel model) {
        if (model == null) {
            return null;
        }
        ExperimentChatDomain domain = new ExperimentChatDomain();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static ExperimentChatModel domainToModel(ExperimentChatDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentChatModel model = new ExperimentChatModel();
        BeanUtils.copyProperties(domain, model);
        return model;
    }

    public static ExperimentChatInfo domainToInfo(ExperimentChatDomain domain) {
        if (domain == null) {
            return null;
        }
        ExperimentChatInfo info = new ExperimentChatInfo();
        BeanUtils.copyProperties(domain, info);
        return info;
    }

    public static ExperimentChatDomain infoToDomain(ExperimentChatInfo info) {
        if (info == null) {
            return null;
        }
        ExperimentChatDomain domain = new ExperimentChatDomain();
        BeanUtils.copyProperties(info, domain);
        return domain;
    }
}
