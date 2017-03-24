package com.hbc.api.dto;

import java.io.Serializable;

/**
 * 报告-基本信息核验
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月2日
 */
public class BaseInfoDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2618080858765886904L;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 入网时间
	 */
	private String openTime;
	/**
	 * 身份证
	 */
	private String idCard;
	/**
	 * 手机号
	 */
	private String phone;
	/**
	 * 会员等级
	 */
	private String memberLevel;
	/**
	 * 实名认证
	 */
	private String realName;
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
	 * @return the openTime
	 */
	public String getOpenTime() {
		return openTime;
	}
	/**
	 * @param openTime the openTime to set
	 */
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	/**
	 * @return the idCard
	 */
	public String getIdCard() {
		return idCard;
	}
	/**
	 * @param idCard the idCard to set
	 */
	public void setIdCard(String idCard) {
		this.idCard = idCard;
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
	 * @return the memberLevel
	 */
	public String getMemberLevel() {
		return memberLevel;
	}
	/**
	 * @param memberLevel the memberLevel to set
	 */
	public void setMemberLevel(String memberLevel) {
		this.memberLevel = memberLevel;
	}
	/**
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}
	/**
	 * @param realName the realName to set
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}
}
