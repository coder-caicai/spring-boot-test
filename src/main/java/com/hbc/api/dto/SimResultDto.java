package com.hbc.api.dto;

import com.hbc.api.common.EnumResultStatus;

/**
 * Created by cheng on 16/12/1.
 */
public class SimResultDto {

    private EnumResultStatus status;

    private String msg;

    public EnumResultStatus getStatus() {
        return status;
    }

    public void setStatus(EnumResultStatus status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
