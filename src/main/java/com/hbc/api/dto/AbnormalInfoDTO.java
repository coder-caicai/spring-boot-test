package com.hbc.api.dto;

import java.io.Serializable;

/**
 * 报告-特殊号码信息
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月2日
 */
public class AbnormalInfoDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2913491687717701169L;
	/**
	 * 特殊呼叫
	 */
	private String abnormalType;
	/**
	 * 通话次数
	 */
	private Integer talkTimes;
	/**
	 * 通话时长
	 */
	private Integer talkDuration;
	/**
	 * 主叫次数
	 */
	private Integer callTimes;
	/**
	 * 被叫次数
	 */
	private Integer calledTimes;
	/**
	 * @return the abnormalType
	 */
	public String getAbnormalType() {
		return abnormalType;
	}
	/**
	 * @param abnormalType the abnormalType to set
	 */
	public void setAbnormalType(String abnormalType) {
		this.abnormalType = abnormalType;
	}
	/**
	 * @return the talkTimes
	 */
	public Integer getTalkTimes() {
		return talkTimes;
	}
	/**
	 * @param talkTimes the talkTimes to set
	 */
	public void setTalkTimes(Integer talkTimes) {
		this.talkTimes = talkTimes;
	}
	/**
	 * @return the talkDuration
	 */
	public Integer getTalkDuration() {
		return talkDuration;
	}
	/**
	 * @param talkDuration the talkDuration to set
	 */
	public void setTalkDuration(Integer talkDuration) {
		this.talkDuration = talkDuration;
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
}
