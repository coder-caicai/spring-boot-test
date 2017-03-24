package com.hbc.api.dto;

import java.io.Serializable;

/**
 * 报告-朋友圈地域
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月2日
 */
public class FriendsCityDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5384142680026588866L;
	/**
	 * 地区
	 */
	private String city;
	/**
	 * 号码数量
	 */
	private Integer number;
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
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the number
	 */
	public Integer getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(Integer number) {
		this.number = number;
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
