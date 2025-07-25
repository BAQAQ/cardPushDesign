package com.example.carddesign.vo;

import java.time.LocalDateTime;
import java.util.List;

public class PushConfig {
    private Long id;
    private Long userId; // 用户ID
    private String projectCode; // 项目编码
    private String pushFrequency; // DAY WEEK MONTH
    private String content;
    private Integer businessType; // 1卡片 0问题
    private Long favoriteId; // 关联的收藏卡片ID
    private Integer enableFlag; // 1有效 0无效
    private Long createUser;
    private LocalDateTime createTime;
    private Long updateUser;
    private LocalDateTime updateTime;

    private List<PushTime> pushTimes; // 关联的推送时间列表

    public PushConfig() {
    }

    public PushConfig(Long userId, String projectCode, String pushFrequency, String content, Integer businessType, Long favoriteId) {
        this.userId = userId;
        this.projectCode = projectCode;
        this.pushFrequency = pushFrequency;
        this.content = content;
        this.businessType = businessType;
        this.favoriteId = favoriteId;
        this.enableFlag = 1;
        this.createUser = -1L;
        this.createTime = LocalDateTime.now();
        this.updateUser = -1L;
        this.updateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getPushFrequency() {
        return pushFrequency;
    }

    public void setPushFrequency(String pushFrequency) {
        this.pushFrequency = pushFrequency;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Long getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Long favoriteId) {
        this.favoriteId = favoriteId;
    }

    public Integer getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Integer enableFlag) {
        this.enableFlag = enableFlag;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public List<PushTime> getPushTimes() {
        return pushTimes;
    }

    public void setPushTimes(List<PushTime> pushTimes) {
        this.pushTimes = pushTimes;
    }

    @Override
    public String toString() {
        return "PushConfig{" +
                "id=" + id +
                ", userId=" + userId +
                ", projectCode='" + projectCode + '\'' +
                ", pushFrequency='" + pushFrequency + '\'' +
                ", content='" + content + '\'' +
                ", businessType=" + businessType +
                ", favoriteId=" + favoriteId +
                ", enableFlag=" + enableFlag +
                ", createUser=" + createUser +
                ", createTime=" + createTime +
                ", updateUser=" + updateUser +
                ", updateTime=" + updateTime +
                ", pushTimes=" + pushTimes +
                '}';
    }
}