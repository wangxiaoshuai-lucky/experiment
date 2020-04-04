package com.kelab.experiment.dal.domain;

import java.util.List;

public class ExperimentContestDomain {

    private Integer id;

    private String title;

    private Integer classId;

    private Long endTime;

    private List<ExperimentProblemDomain> problemDomains;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public List<ExperimentProblemDomain> getProblemDomains() {
        return problemDomains;
    }

    public void setProblemDomains(List<ExperimentProblemDomain> problemDomains) {
        this.problemDomains = problemDomains;
    }
}
