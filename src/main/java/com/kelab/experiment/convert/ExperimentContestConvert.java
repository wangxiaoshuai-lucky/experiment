package com.kelab.experiment.convert;

import com.kelab.experiment.dal.domain.ExperimentContestDomain;
import com.kelab.experiment.dal.domain.ExperimentProblemDomain;
import com.kelab.experiment.dal.model.ExperimentContestModel;
import com.kelab.info.experiment.info.ExperimentContestInfo;
import com.kelab.info.experiment.info.ExperimentProblemInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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
        if (!CollectionUtils.isEmpty(domain.getProblemDomains())) {
            List<ExperimentProblemInfo> proInfos = domain.getProblemDomains().stream().map(ExperimentProblemConvert::domainToInfo).collect(Collectors.toList());
            info.setProblemInfos(proInfos);
        }
        return info;
    }

    public static ExperimentContestDomain infoToDomain(ExperimentContestInfo info) {
        if (info == null) {
            return null;
        }
        ExperimentContestDomain domain = new ExperimentContestDomain();
        BeanUtils.copyProperties(info, domain);
        if (!CollectionUtils.isEmpty(info.getProblemInfos())) {
            List<ExperimentProblemDomain> probDomains = info.getProblemInfos().stream().map(ExperimentProblemConvert::infoToDomain).collect(Collectors.toList());
            domain.setProblemDomains(probDomains);
        }
        return domain;
    }
}
