package com.kelab.experiment.dal.domain;

import com.kelab.info.usercenter.info.UserInfo;

public class ExperimentClassDomain {

    private Integer id;

    private String className;

    private String termName;

    private String classCode;

    private Integer teacherId;

    private Long createTime;

    private UserInfo teacherInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public UserInfo getTeacherInfo() {
        return teacherInfo;
    }

    public void setTeacherInfo(UserInfo teacherInfo) {
        this.teacherInfo = teacherInfo;
    }
}
