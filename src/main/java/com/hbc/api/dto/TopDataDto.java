package com.hbc.api.dto;

/**
 * Created by cheng on 16/7/26.
 */
public class TopDataDto {

    private String mobile;

    private Integer callTimes;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getCallTimes() {
        return callTimes;
    }

    public void setCallTimes(Integer callTimes) {
        this.callTimes = callTimes;
    }
}
