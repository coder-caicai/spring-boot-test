package com.hbc.api.dto;

import java.io.Serializable;

/**
 * 通话次数和通话时长
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月3日
 */
public class CallTimesAndDurationDTO implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8874253510865889056L;
	/**
	 * 通话时长
	 */
    private Integer callDuration;
	/**
	 * 通话次数
	 */
    private Integer callTimes;
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
}
