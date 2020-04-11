package com.kelab.experiment.dal.domain;

import com.kelab.info.problemcenter.info.ProblemUserMarkInnerInfo;

import java.util.Map;

public class ExperimentUserContestDomain {

    private Integer userId;

    private ExperimentStudentDomain studentInfo;

    private Map<Integer, ProblemUserMarkInnerInfo> submitHistory;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ExperimentStudentDomain getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(ExperimentStudentDomain studentInfo) {
        this.studentInfo = studentInfo;
    }

    public Map<Integer, ProblemUserMarkInnerInfo> getSubmitHistory() {
        return submitHistory;
    }

    public void setSubmitHistory(Map<Integer, ProblemUserMarkInnerInfo> submitHistory) {
        this.submitHistory = submitHistory;
    }
}
