package com.hbc.api.dto;

/**
 * Created by cheng on 16/7/26.
 */
public class DataDto {

    private String callType;//0:主叫 1:被叫

    private String callStyle;//0:市话 1:长途

    private String callMobile;//对方号码

    private String callTime;//通话时间

    private String callTimeCost;//通话时长

    private String callArea;//通话地点

    private Double callFee;//通话费用

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallStyle() {
        return callStyle;
    }

    public void setCallStyle(String callStyle) {
        this.callStyle = callStyle;
    }

    public String getCallMobile() {
        return callMobile;
    }

    public void setCallMobile(String callMobile) {
        this.callMobile = callMobile;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallTimeCost() {
        return callTimeCost;
    }

    public void setCallTimeCost(String callTimeCost) {
        this.callTimeCost = callTimeCost;
    }

    public String getCallArea() {
        return callArea;
    }

    public void setCallArea(String callArea) {
        this.callArea = callArea;
    }

    public double getCallFee() {
        return callFee;
    }

    public void setCallFee(double callFee) {
        this.callFee = callFee;
    }
}
