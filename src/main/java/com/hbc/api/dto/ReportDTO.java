package com.hbc.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 报告信息
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月2日
 */
public class ReportDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1891713450868295745L;
	/**
	 * 基本信息核验
	 */
	private BaseInfoDTO baseInfo;
	/**
	 * 联系人信息
	 */
	private List<ContactsInfoDTO> contactsInfo;
	/**
	 * 亲密伙伴Top10信息
	 */
	private List<BestFriendDTO> bestFriends;	
	/**
	 * 朋友圈地域分布信息
	 */
	private List<FriendsCityDTO> friendsCity;	
	/**
	 * 漫游信息
	 */
	private List<RoamInfoDTO> roamInfo;	
	/**
	 * 特殊号码通话	
	 */
	private List<AbnormalInfoDTO	> abnormalInfo;
	/**
	 * 手机静默天数	
	 */
	private Integer sleepDays;
	/**
	 * 夜间通话信息
	 */
	private NightInfoDTO nightInfo;
	/**
	 * 运营商数据分析
	 */
	private List<MonthlyDTO	> monthlyDA;
	/**
	 * 漫游天数	
	 */
	private Integer roamDays;
	/**
	 * @return the baseInfo
	 */
	public BaseInfoDTO getBaseInfo() {
		return baseInfo;
	}
	/**
	 * @param baseInfo the baseInfo to set
	 */
	public void setBaseInfo(BaseInfoDTO baseInfo) {
		this.baseInfo = baseInfo;
	}
	/**
	 * @return the contactsInfo
	 */
	public List<ContactsInfoDTO> getContactsInfo() {
		return contactsInfo;
	}
	/**
	 * @param contactsInfo the contactsInfo to set
	 */
	public void setContactsInfo(List<ContactsInfoDTO> contactsInfo) {
		this.contactsInfo = contactsInfo;
	}
	/**
	 * @return the bestFriends
	 */
	public List<BestFriendDTO> getBestFriends() {
		return bestFriends;
	}
	/**
	 * @param bestFriends the bestFriends to set
	 */
	public void setBestFriends(List<BestFriendDTO> bestFriends) {
		this.bestFriends = bestFriends;
	}
	/**
	 * @return the friendsCity
	 */
	public List<FriendsCityDTO> getFriendsCity() {
		return friendsCity;
	}
	/**
	 * @param friendsCity the friendsCity to set
	 */
	public void setFriendsCity(List<FriendsCityDTO> friendsCity) {
		this.friendsCity = friendsCity;
	}
	/**
	 * @return the roamInfo
	 */
	public List<RoamInfoDTO> getRoamInfo() {
		return roamInfo;
	}
	/**
	 * @param roamInfo the roamInfo to set
	 */
	public void setRoamInfo(List<RoamInfoDTO> roamInfo) {
		this.roamInfo = roamInfo;
	}
	/**
	 * @return the abnormalInfo
	 */
	public List<AbnormalInfoDTO> getAbnormalInfo() {
		return abnormalInfo;
	}
	/**
	 * @param abnormalInfo the abnormalInfo to set
	 */
	public void setAbnormalInfo(List<AbnormalInfoDTO> abnormalInfo) {
		this.abnormalInfo = abnormalInfo;
	}
	/**
	 * @return the sleepDays
	 */
	public Integer getSleepDays() {
		return sleepDays;
	}
	
	public Integer getRoamDays() {
		return roamDays;
	}
	/**
	 * @return the roamDays
	 */
	public void setRoamDays(Integer roamDays) {
		this.roamDays = roamDays;
	}
	/**
	 * @param sleepDays the sleepDays to set
	 */
	public void setSleepDays(Integer sleepDays) {
		this.sleepDays = sleepDays;
	}
	/**
	 * @return the nightInfo
	 */
	public NightInfoDTO getNightInfo() {
		return nightInfo;
	}
	/**
	 * @param nightInfo the nightInfo to set
	 */
	public void setNightInfo(NightInfoDTO nightInfo) {
		this.nightInfo = nightInfo;
	}
	/**
	 * @return the monthlyDA
	 */
	public List<MonthlyDTO> getMonthlyDA() {
		return monthlyDA;
	}
	/**
	 * @param monthlyDA the monthlyDA to set
	 */
	public void setMonthlyDA(List<MonthlyDTO> monthlyDA) {
		this.monthlyDA = monthlyDA;
	}
	
}
