package com.example.carddesign.vo;

import java.time.LocalDateTime;

public class FavoriteCard {
    private Long id;
    private Long userId; // 用户ID
    private String projectCode; // 项目编码
    private String content; // 卡片类型或者问题内容
    private Integer businessType; // 1卡片 0问题
    private Integer isCancellable; // 是否可取消收藏：1可取消 0不可取消
    private Integer enableFlag; // 1有效 0无效
    private Long createUser;
    private LocalDateTime createTime;
    private Long updateUser;
    private LocalDateTime updateTime;

    public FavoriteCard() {
    }

    public FavoriteCard(Long userId, String projectCode, String content, Integer businessType, Integer isCancellable) {
        this.userId = userId;
        this.projectCode = projectCode;
        this.content = content;
        this.businessType = businessType;
        this.isCancellable = isCancellable;
        this.enableFlag = 1; // 默认有效
        this.createUser = -1L; // 默认-1
        this.createTime = LocalDateTime.now();
        this.updateUser = -1L; // 默认-1
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

    public Integer getIsCancellable() {
        return isCancellable;
    }

    public void setIsCancellable(Integer isCancellable) {
        this.isCancellable = isCancellable;
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

    @Override
    public String toString() {
        return "FavoriteCard{" +
                "id=" + id +
                ", userId=" + userId +
                ", projectCode='" + projectCode + '\'' +
                ", content='" + content + '\'' +
                ", businessType=" + businessType +
                ", isCancellable=" + isCancellable +
                ", enableFlag=" + enableFlag +
                ", createUser=" + createUser +
                ", createTime=" + createTime +
                ", updateUser=" + updateUser +
                ", updateTime=" + updateTime +
                '}';
    }
}