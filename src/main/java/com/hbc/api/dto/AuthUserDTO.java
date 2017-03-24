package com.hbc.api.dto;

/**
 * 用来保存认证用户信息
 * @author lichunhua
 *
 */
public class AuthUserDTO {
	 
	//要认证的用户名
	private String clientId;
	//要认证的用户密码
	private String clientSecret;	
	//用户 companyId
	private Integer companyId;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	
}
