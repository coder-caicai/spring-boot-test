package com.hbc.api.dto;

import java.io.Serializable;

/**
 * 报告-联系人核验
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月2日
 */
public class ContactsInfoDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4415539643640852590L;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 手机号
	 */
	private String phone;
	/**
	 * 通话天数
	 */
	private Integer talkDays;
	/**
	 * 通话天数排名
	 */
	private Integer talkRanking;
	/**
	 * 通话次数
	 */
	private Integer talkTimes;
	/**
	 * 通话时长
	 */
	private Integer talkDuration;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
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
	 * @return the talkRanking
	 */
	public Integer getTalkRanking() {
		return talkRanking;
	}
	/**
	 * @param talkRanking the talkRanking to set
	 */
	public void setTalkRanking(Integer talkRanking) {
		this.talkRanking = talkRanking;
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

}
