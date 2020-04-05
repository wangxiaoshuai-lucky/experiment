package com.kelab.experiment.dal.domain;

import java.util.List;

public class ExperimentGroupDomain {

    private Integer id;

    private Integer classId;

    private String name;

    private List<ExperimentStudentDomain> members;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExperimentStudentDomain> getMembers() {
        return members;
    }

    public void setMembers(List<ExperimentStudentDomain> members) {
        this.members = members;
    }
}
