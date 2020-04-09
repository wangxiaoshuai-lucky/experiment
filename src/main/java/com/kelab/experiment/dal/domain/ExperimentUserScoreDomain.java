package com.kelab.experiment.dal.domain;

import java.util.Map;

public class ExperimentUserScoreDomain {

    private Integer userId;

    private ExperimentStudentDomain studentInfo;

    private Map<Integer, ExperimentHomeworkDomain> homeworkMap;

    private Map<Integer, ExperimentContestDomain> contestMap;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Map<Integer, ExperimentHomeworkDomain> getHomeworkMap() {
        return homeworkMap;
    }

    public void setHomeworkMap(Map<Integer, ExperimentHomeworkDomain> homeworkMap) {
        this.homeworkMap = homeworkMap;
    }

    public Map<Integer, ExperimentContestDomain> getContestMap() {
        return contestMap;
    }

    public void setContestMap(Map<Integer, ExperimentContestDomain> contestMap) {
        this.contestMap = contestMap;
    }

    public ExperimentStudentDomain getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(ExperimentStudentDomain studentInfo) {
        this.studentInfo = studentInfo;
    }
}
