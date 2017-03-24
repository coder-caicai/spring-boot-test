package com.hbc.api.dto;

import java.io.Serializable;

/**
 * 报告-漫游信息
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月2日
 */
public class RoamInfoDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2502040077310549722L;
	/**
	 * 漫游城市
	 */
	private String city;
	/**
	 * 漫游天数
	 */
	private Integer dayNum;
	/**
	 * 占总漫游天数比例
	 */
	private String proportion;
	/**
	 * 漫游总天数
	 */
	private Integer allNum;
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
	 * @return the dayNum
	 */
	public Integer getDayNum() {
		return dayNum;
	}
	/**
	 * @param dayNum the dayNum to set
	 */
	public void setDayNum(Integer dayNum) {
		this.dayNum = dayNum;
	}
	/**
	 * @return the proportion
	 */
	public String getProportion() {
		return proportion;
	}
	/**
	 * @param proportion the proportion to set
	 */
	public void setProportion(String proportion) {
		this.proportion = proportion;
	}
	/**
	 * @return the allNum
	 */
	public Integer getAllNum() {
		return allNum;
	}
	/**
	 * @param allNum the allNum to set
	 */
	public void setAllNum(Integer allNum) {
		this.allNum = allNum;
	}
}
