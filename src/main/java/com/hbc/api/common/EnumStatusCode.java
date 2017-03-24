package com.hbc.api.common;

public enum EnumStatusCode {
	  OK(100,"请求成功"), 
	  
	  TOKEN_INVALID(201,"access_token无效"), 
	  TOKEN_EXPIRED(202,"access_token过期"),
	  PARAM_INVALID(203,"请求参数错误"), 
	  USER_INVALID(204,"无效的用户"), 
	  ERROR_ADDRESS(205,"无效请求"), 
	
	  INTERNAL_ERROR(501,"服务器内部错误"), 
	  NO_RULES(502,"未配置规则"), 
	  BANLANCE_NOT_ENOUGH(503,"账户余额不足"), 
	  NO_DATA(504,"未查到数据"), 
	  DATASOURCE_ABNORMAL(505,"数据源异常，请稍候重试"), 

	  TASK_IN_QUEUE(601,"任务排队中"), 
	  TASK_IN_PROGRESS(602,"任务执行中");

	
	// 定义私有变量

    private int _code;
    private String _name;

    // 构造函数，枚举类型只能为私有

    private EnumStatusCode(int code, String name) {
        this._code = code;
        this._name = name;
    }

	public int getCode() {
		return _code;
	}
	
	public String getName() {
		return _name;
	}
}  