/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.hbc.api.controller;

import com.alibaba.fastjson.JSON;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.CallDurationDateDTO;
import com.hbc.api.dto.CallTimesDateDTO;
import com.hbc.api.dto.DataDto;
import com.hbc.api.dto.ReportDTO;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.dto.TopDataDto;
import com.hbc.api.model.DxCallDetailClient;
import com.hbc.api.service.DxCallClientService;
import com.hbc.api.service.DxCallDetailClientService;
import com.hbc.api.util.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ccz
 * @since 2015-12-19 11:10
 */
@Controller
@RequestMapping("/dxCall")
public class DxCallController extends BaseContoller {


    @Autowired
    private DxCallDetailClientService dxCallDetailClientService;

    @Autowired
    private DxCallClientService dxCallClientService;

    @Autowired
    private RedisUtil redisUtil;


    @RequestMapping(value = "syncData",method = RequestMethod.POST)
    @ResponseBody
    public String synchroData( HttpServletRequest request) {
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
    	String pwd= request.getAttribute("pwd")==null?null:request.getAttribute("pwd").toString();
        Integer clientId = Integer.parseInt(request.getAttribute("clientId").toString());
        ResultDto resultDto = new ResultDto();
        if(StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(pwd)){
            try {
                boolean result = dxCallClientService.login(mobile.trim(),pwd.trim(),clientId);
                if(result){
                    resultDto.setStatus(EnumResultStatus.SUCCESS_MSG);
                    resultDto.setMsg(EnumResultStatus.SUCCESS_MSG.getName());
                    redisUtil.set("time_"+mobile,mobile,Long.valueOf(60*2));
                }else{
                    resultDto.setMsg("手机号,密码错误!");
                    resultDto.setStatus(EnumResultStatus.ERROR);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                resultDto.setMsg("手机号,密码错误!");
                resultDto.setStatus(EnumResultStatus.ERROR);
            }
        }else{
            resultDto.setMsg("手机号,密码错误!");
            resultDto.setStatus(EnumResultStatus.ERROR);
        }
        return responseStr(JSON.toJSONString(resultDto));
    }

    @RequestMapping(value = "msgConfirm" ,method = RequestMethod.POST)
    @ResponseBody
    public String msgConfirm(HttpServletRequest request) {
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
    	String validateCode = request.getAttribute("validateCode")==null?null:request.getAttribute("validateCode").toString();
        ResultDto resultDto = new ResultDto();
        try {
            if(!redisUtil.exists("time_"+mobile.trim())){
                resultDto.setMsg("验证码过期!");
                resultDto.setStatus(EnumResultStatus.ERROR_LONG_TIME);
                return  responseStr(JSON.toJSONString(resultDto));
            }
            boolean result = dxCallClientService.synchroData(mobile.trim(),validateCode.trim());
            if(result){

                resultDto.setStatus(EnumResultStatus.SUCCESS);
                resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
            }else{
                resultDto.setMsg("验证码错误!");
                resultDto.setStatus(EnumResultStatus.ERROR_MSG);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            resultDto.setMsg("验证码错误!");
            resultDto.setStatus(EnumResultStatus.ERROR_MSG);
        }
        return responseStr(JSON.toJSONString(resultDto));
    }

}
