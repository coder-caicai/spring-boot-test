package com.hbc.api.dto;

import java.io.Serializable;

import com.alibaba.druid.sql.visitor.functions.If;

/**
 * 报告-夜间通话信息
 * @version v1.0
 * @author ChengYongfei
 * @createTime 2016年11月2日
 */
public class NightInfoDTO implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3735672947864173631L;
	/**
	 * 通话次数
	 */
	private String talkTimes;
	/**
	 * 通话时长
	 */
	private String talkDuration;
	/**
	 * 主叫次数
	 */
	private String callTimes;
	/**
	 * 被叫次数
	 */
	private String calledTimes;
	/**
	 * @return the talkTimes
	 */
	public String getTalkTimes() {
		int calltimes=0;
		int calledtimes=0;
		if (this.callTimes!=null &&!"NULL".equals(this.callTimes.toUpperCase())) {
			calltimes=Integer.valueOf(this.callTimes);
		}
		if (this.calledTimes!=null &&!"NULL".equals(this.calledTimes.toUpperCase())) {
			calledtimes=Integer.valueOf(this.calledTimes);
		}
		this.talkTimes=String.valueOf(calltimes+calledtimes);
		return this.talkTimes ;
	}
	/**
	 * @param talkTimes the talkTimes to set
	 */
	public void setTalkTimes(String talkTimes) {
		this.talkTimes = talkTimes;
	}
	/**
	 * @return the talkDuration
	 */
	public String getTalkDuration() {
		return talkDuration==null?"0":talkDuration;
	}
	/**
	 * @param talkDuration the talkDuration to set
	 */
	public void setTalkDuration(String talkDuration) {
		this.talkDuration = talkDuration;
	}
	/**
	 * @return the callTimes
	 */
	public String getCallTimes() {
		return callTimes;
	}
	/**
	 * @param callTimes the callTimes to set
	 */
	public void setCallTimes(String callTimes) {
		this.callTimes = callTimes;
	}
	/**
	 * @return the calledTimes
	 */
	public String getCalledTimes() {
		return calledTimes;
	}
	/**
	 * @param calledTimes the calledTimes to set
	 */
	public void setCalledTimes(String calledTimes) {
		this.calledTimes = calledTimes;
	}
}
