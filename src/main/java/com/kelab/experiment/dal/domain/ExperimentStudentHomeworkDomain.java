package com.kelab.experiment.dal.domain;

import com.kelab.info.usercenter.info.UserInfo;

public class ExperimentStudentHomeworkDomain {

    private Integer id;

    private Integer homeworkId;

    private Integer targetId;

    private String attachName;

    private String attachUrl;

    private Long postTime;

    private Integer score;

    private String comment;

    private Long commentTime;

    /**
     * 个人提交
     */
    private UserInfo postUserInfo;

    /**
     * 分组提交
     */
    private ExperimentGroupDomain postGroupInfo;

    public UserInfo getPostUserInfo() {
        return postUserInfo;
    }

    public void setPostUserInfo(UserInfo postUserInfo) {
        this.postUserInfo = postUserInfo;
    }

    public ExperimentGroupDomain getPostGroupInfo() {
        return postGroupInfo;
    }

    public void setPostGroupInfo(ExperimentGroupDomain postGroupInfo) {
        this.postGroupInfo = postGroupInfo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Integer homeworkId) {
        this.homeworkId = homeworkId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getAttachName() {
        return attachName;
    }

    public void setAttachName(String attachName) {
        this.attachName = attachName;
    }

    public String getAttachUrl() {
        return attachUrl;
    }

    public void setAttachUrl(String attachUrl) {
        this.attachUrl = attachUrl;
    }

    public Long getPostTime() {
        return postTime;
    }

    public void setPostTime(Long postTime) {
        this.postTime = postTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Long commentTime) {
        this.commentTime = commentTime;
    }
}
