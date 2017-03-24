package com.hbc.api.dto;

import java.io.Serializable;

/**
 * 报告-月度统计信息
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月2日
 */
public class MonthlyDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4230024569719317792L;
	/**
	 * 月份
	 */
	private String month;
	/**
	 * 主叫次数
	 */
	private Integer callTimes;
	/**
	 * 主叫时长
	 */
	private Integer callDuration;
	/**
	 * 被叫次数
	 */
	private Integer calledTimes;
	/**
	 * 被叫时长
	 */
	private Integer calledDuration;
	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}
	/**
	 * @param month the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}
	/**
	 * @return the callTimes
	 */
	public Integer getCallTimes() {
		return callTimes;
	}
	/**
	 * @param callTimes the callTimes to set
	 */
	public void setCallTimes(Integer callTimes) {
		this.callTimes = callTimes;
	}
	/**
	 * @return the callDuration
	 */
	public Integer getCallDuration() {
		return callDuration;
	}
	/**
	 * @param callDuration the callDuration to set
	 */
	public void setCallDuration(Integer callDuration) {
		this.callDuration = callDuration;
	}
	/**
	 * @return the calledTimes
	 */
	public Integer getCalledTimes() {
		return calledTimes;
	}
	/**
	 * @param calledTimes the calledTimes to set
	 */
	public void setCalledTimes(Integer calledTimes) {
		this.calledTimes = calledTimes;
	}
	/**
	 * @return the calledDuration
	 */
	public Integer getCalledDuration() {
		return calledDuration;
	}
	/**
	 * @param calledDuration the calledDuration to set
	 */
	public void setCalledDuration(Integer calledDuration) {
		this.calledDuration = calledDuration;
	}


}
