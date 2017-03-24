package com.hbc.api.model;

import javax.persistence.*;

public class LtCallDetail {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String calllonghour;

    private String calldate;

    private String calltime;

    private String homearea;

    private String calltype;

    private String landtype;

    private Double totalfee;

    private String othernum;

    private Integer call_id;

    @Transient
    private Integer callTimes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCalllonghour() {
        return calllonghour;
    }

    public void setCalllonghour(String calllonghour) {
        this.calllonghour = calllonghour == null ? null : calllonghour.trim();
    }

    public String getCalldate() {
        return calldate;
    }

    public void setCalldate(String calldate) {
        this.calldate = calldate == null ? null : calldate.trim();
    }

    public String getCalltime() {
        return calltime;
    }

    public void setCalltime(String calltime) {
        this.calltime = calltime == null ? null : calltime.trim();
    }

    public String getHomearea() {
        return homearea;
    }

    public void setHomearea(String homearea) {
        this.homearea = homearea == null ? null : homearea.trim();
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype == null ? null : calltype.trim();
    }

    public String getLandtype() {
        return landtype;
    }

    public void setLandtype(String landtype) {
        this.landtype = landtype == null ? null : landtype.trim();
    }

    public Double getTotalfee() {
        return totalfee;
    }

    public void setTotalfee(Double totalfee) {
        this.totalfee = totalfee;
    }

    public String getOthernum() {
        return othernum;
    }

    public void setOthernum(String othernum) {
        this.othernum = othernum == null ? null : othernum.trim();
    }

    public Integer getCall_id() {
        return call_id;
    }

    public void setCall_id(Integer call_id) {
        this.call_id = call_id;
    }

    public Integer getCallTimes() {
        return callTimes;
    }

    public void setCallTimes(Integer callTimes) {
        this.callTimes = callTimes;
    }
}