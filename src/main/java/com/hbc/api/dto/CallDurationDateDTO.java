package com.hbc.api.dto;

/**
 * 通话时长DTO
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年10月8日
 */
public class CallDurationDateDTO {

	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 通话时长
	 */
    private Integer callDuration;
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
}
