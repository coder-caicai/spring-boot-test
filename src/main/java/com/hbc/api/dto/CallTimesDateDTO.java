package com.hbc.api.dto;

/**
 * 通话次数DTO
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年10月8日
 */
public class CallTimesDateDTO {
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 通话次数
	 */
    private Integer callTimes;
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
