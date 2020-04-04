package com.kelab.experiment.dal.model;

public class ExperimentProblemModel {

    private Integer id;

    private Integer contestId;

    private Integer probId;

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
}
