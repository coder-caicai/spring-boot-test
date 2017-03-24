package com.hbc.api.common;

import org.aspectj.lang.reflect.DeclareErrorOrWarning;

/**
 * Created by cheng on 16/7/25.
 */
public enum EnumResultStatus {

    ERROR("0", "服务调用失败!"),
    SUCCESS("1", "服务调用成功!"),
    SUCCESS_MSG("2","服务调用成功并且已发验证码到您的手机!"),
    SUCCESS_IMG("3","请输入图片验证码!"),
    ERROR_MSG("4","短信验证码错误!"),
    ERROR_MSG_EXPIRED("9","短信验证码过期!"),
    ERROR_IMG("5","图片验证码错误!"),
    ERROR_LONG_TIME("6","图片验证码过期!"),
    ERROR_TOKEN("7","token错误!"),
    ERROR_TOKEN_EXPIRED("8","token过期!"),
    ERROR_DATA("9","参数错误!"),
    ERROR_PWD("10","密码错误!"),
    ERROR_BUSY("11","系统繁忙,请稍后再试");

    private String code;

    private String name;

    EnumResultStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public static EnumResultStatus getEnum(String value)
    {
        for(EnumResultStatus e : EnumResultStatus.values())
        {
            if(value.equals(e.getCode()))
            {
                return e;
            }
        }
        return null;
    }
}
