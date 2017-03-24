package com.hbc.api.controller;

import com.hbc.api.dto.ResultDto;
import com.hbc.api.util.AesUtil;
import com.hbc.api.util.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cheng on 16/7/25.
 */
public class BaseContoller {

    public Logger logger = LoggerFactory.getLogger(getClass());

    public String responseStr(String json){
        try {
            return EncryptUtils.aesEncrypt(json);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

}
