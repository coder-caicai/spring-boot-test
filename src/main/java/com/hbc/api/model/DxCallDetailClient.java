package com.hbc.api.model;

import javax.persistence.*;

public class DxCallDetailClient {

	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String callType;
	
	private String callMobile;//0:主叫 1:被叫 2:无
	
	private String callTime;

	private String callTimeCost;

	private String callStyle;//0:市话 1:长途 2:无

	private String callArea;

	private Double callFee;

	private Integer callId;

	@Transient
	private Integer callTimes;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
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

	public String getCallStyle() {
		return callStyle;
	}

	public void setCallStyle(String callStyle) {
		this.callStyle = callStyle;
	}

	public String getCallArea() {
		return callArea;
	}

	public void setCallArea(String callArea) {
		this.callArea = callArea;
	}

	public Double getCallFee() {
		return callFee;
	}

	public void setCallFee(Double callFee) {
		this.callFee = callFee;
	}

	public Integer getCallId() {
		return callId;
	}

	public void setCallId(Integer callId) {
		this.callId = callId;
	}

	public Integer getCallTimes() {
		return callTimes;
	}

	public void setCallTimes(Integer callTimes) {
		this.callTimes = callTimes;
	}
}
