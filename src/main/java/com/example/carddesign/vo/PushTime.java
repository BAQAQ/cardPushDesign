package com.example.carddesign.vo;

import java.time.LocalDateTime;

public class PushTime {
    private Long id;
    private Long pushConfigId;
    private String weekday; // 1-7, 逗号分隔
    private String monthDay; // 1-31, 逗号分隔
    private String hour; // 00:00,15:00,17:00
    private Integer enableFlag; // 1有效 0无效
    private Long createUser;
    private LocalDateTime createTime;
    private Long updateUser;
    private LocalDateTime updateTime;

    public PushTime() {
    }

    public PushTime(Long pushConfigId, String weekday, String monthDay, String hour) {
        this.pushConfigId = pushConfigId;
        this.weekday = weekday;
        this.monthDay = monthDay;
        this.hour = hour;
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

    public Long getPushConfigId() {
        return pushConfigId;
    }

    public void setPushConfigId(Long pushConfigId) {
        this.pushConfigId = pushConfigId;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(String monthDay) {
        this.monthDay = monthDay;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
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
        return "PushTime{" +
                "id=" + id +
                ", pushConfigId=" + pushConfigId +
                ", weekday='" + weekday + '\'' +
                ", monthDay='" + monthDay + '\'' +
                ", hour='" + hour + '\'' +
                ", enableFlag=" + enableFlag +
                ", createUser=" + createUser +
                ", createTime=" + createTime +
                ", updateUser=" + updateUser +
                ", updateTime=" + updateTime +
                '}';
    }
}