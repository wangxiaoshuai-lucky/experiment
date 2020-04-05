package com.kelab.experiment.dal.domain;

import java.util.List;

public class ExperimentContestDomain {

    private Integer id;

    private String title;

    private Integer classId;

    private Long endTime;

    private Integer acNum;

    private Integer totalNum;

    private List<ExperimentProblemDomain> problemDomains;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAcNum() {
        return acNum;
    }

    public void setAcNum(Integer acNum) {
        this.acNum = acNum;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
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
