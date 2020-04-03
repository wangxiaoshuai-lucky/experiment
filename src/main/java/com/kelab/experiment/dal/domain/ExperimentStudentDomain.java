package com.kelab.experiment.dal.domain;

import com.kelab.experiment.constant.enums.ApplyClassStatus;
import com.kelab.info.usercenter.info.UserInfo;

public class ExperimentStudentDomain {

    private Integer id;

    private Integer userId;

    private Integer classId;

    private ApplyClassStatus status;

    private UserInfo studentInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public ApplyClassStatus getStatus() {
        return status;
    }

    public void setStatus(ApplyClassStatus status) {
        this.status = status;
    }

    public UserInfo getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(UserInfo studentInfo) {
        this.studentInfo = studentInfo;
    }
}
