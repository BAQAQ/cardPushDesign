package com.example.carddesign.vo;

import java.time.LocalDateTime;

public class MessageLog {
    private Long id;
    private Long favoriteId;
    private String messageContent;
    private Integer enableFlag; // 1有效 0无效
    private Long createUser;
    private LocalDateTime createTime;
    private Long updateUser;
    private LocalDateTime updateTime;

    public MessageLog() {
    }

    public MessageLog(Long favoriteId, String messageContent) {
        this.favoriteId = favoriteId;
        this.messageContent = messageContent;
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

    public Long getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Long favoriteId) {
        this.favoriteId = favoriteId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
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
        return "MessageLog{" +
                "id=" + id +
                ", favoriteId=" + favoriteId +
                ", messageContent='" + messageContent + '\'' +
                ", enableFlag=" + enableFlag +
                ", createUser=" + createUser +
                ", createTime=" + createTime +
                ", updateUser=" + updateUser +
                ", updateTime=" + updateTime +
                '}';
    }
}