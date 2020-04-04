package com.kelab.experiment.dal.domain;

public class ExperimentProblemDomain {

    private Integer id;

    private Integer contestId;

    private Integer probId;

    private String title;

    private Boolean ac;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    public Integer getProbId() {
        return probId;
    }

    public void setProbId(Integer probId) {
        this.probId = probId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAc() {
        return ac;
    }

    public void setAc(Boolean ac) {
        this.ac = ac;
    }
}
