package com.hbc.api.dto;

import com.hbc.api.common.EnumResultStatus;

/**
 * Created by cheng on 16/7/25.
 */
public class ResultDto {

    private EnumResultStatus status;

    private String msg;

    private Object data;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public EnumResultStatus getStatus() {
        return status;
    }

    public void setStatus(EnumResultStatus status) {
        this.status = status;
    }
}
