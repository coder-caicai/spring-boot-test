package com.hbc.api.dto;

import java.io.Serializable;

/**
 * 报告-亲密伙伴
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月2日
 */
public class BestFriendDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8675249822465447690L;
	/**
	 * 对方号码
	 */
	private String phone;
	/**
	 * 号码归属地
	 */
	private String city;
	/**
	 * 通话天数
	 */
	private Integer talkDays;
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
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
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
	 * @return the talkDays
	 */
	public Integer getTalkDays() {
		return talkDays;
	}
	/**
	 * @param talkDays the talkDays to set
	 */
	public void setTalkDays(Integer talkDays) {
		this.talkDays = talkDays;
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
