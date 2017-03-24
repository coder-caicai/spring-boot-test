package com.hbc.api.model;

import javax.persistence.*;

public class YdCallDetailClient {

	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String startTime; //通话时间

	private String commPlac;//通话地点

	private String commMode;//0:主叫 1:被叫

	private String eachOtherNm; //对方号码

	private String commTime;//通话时长 23秒

	private String commTimeH5;//通话时长 00:00:23

	private String commType;//0:本地通话 1:国内长途

	private String mealFavorable;//手机套餐类型

	private Double commFee;//通话费用

	private Integer callId;

	@Transient
	private Integer callTimes;//统计次数

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getCommPlac() {
		return commPlac;
	}

	public void setCommPlac(String commPlac) {
		this.commPlac = commPlac;
	}

	public String getCommMode() {
		return commMode;
	}

	public void setCommMode(String commMode) {
		this.commMode = commMode;
	}

	public String getEachOtherNm() {
		return eachOtherNm;
	}

	public void setEachOtherNm(String eachOtherNm) {
		this.eachOtherNm = eachOtherNm;
	}

	public String getCommTime() {
		return commTime;
	}

	public void setCommTime(String commTime) {
		this.commTime = commTime;
	}

	public String getCommTimeH5() {
		return commTimeH5;
	}

	public void setCommTimeH5(String commTimeH5) {
		this.commTimeH5 = commTimeH5;
	}

	public String getCommType() {
		return commType;
	}

	public void setCommType(String commType) {
		this.commType = commType;
	}

	public String getMealFavorable() {
		return mealFavorable;
	}

	public void setMealFavorable(String mealFavorable) {
		this.mealFavorable = mealFavorable;
	}

	public Double getCommFee() {
		return commFee;
	}

	public void setCommFee(Double commFee) {
		this.commFee = commFee;
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
