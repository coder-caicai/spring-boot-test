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
import com.alibaba.fastjson.JSONObject;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.CallDurationDateDTO;
import com.hbc.api.dto.CallTimesDateDTO;
import com.hbc.api.dto.DataDto;
import com.hbc.api.dto.ReportDTO;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.dto.TopDataDto;
import com.hbc.api.service.YdCallDetailClientService;
import com.hbc.api.service.YdCallClientService;
import com.hbc.api.util.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.enums.Enum;
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
@RequestMapping("/ydCall")
public class YdCallController extends BaseContoller {


    @Autowired
    private YdCallDetailClientService ydCallDetailClientService;

    @Autowired
    private YdCallClientService ydCallService;

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(value = "sendMsg", method = RequestMethod.POST)
    @ResponseBody
    public String sendMsg(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        String pwd = request.getAttribute("pwd") == null ? null : request.getAttribute("pwd").toString();
        redisUtil.set(mobile+"_pwd",pwd,Long.valueOf(60*2));
        logger.info("请求参数:mobile:" + mobile);
        ResultDto resultDto = new ResultDto();
        try {
            resultDto = ydCallService.sendMsg(mobile.trim());
            resultDto.setStatus(EnumResultStatus.SUCCESS_MSG);
            resultDto.setMsg(EnumResultStatus.SUCCESS_MSG.getName());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("服务异常:" + e.getMessage());
            resultDto.setMsg("请求出错!");
            resultDto.setStatus(EnumResultStatus.ERROR);
        }
        logger.info("接口返回数据:" + JSONObject.toJSONString(resultDto));
        return responseStr(JSON.toJSONString(resultDto));
    }


    @RequestMapping(value = "msgConfirm", method = RequestMethod.POST)
    @ResponseBody
    public String msgConfirm(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        String pwd = request.getAttribute("pwd") == null ? null : request.getAttribute("pwd").toString();
        ResultDto resultDto = new ResultDto();
        if(StringUtils.isBlank(pwd)){
            Object _pwd = redisUtil.get(mobile+"_pwd");
            if(_pwd == null){
                resultDto.setStatus(EnumResultStatus.ERROR_MSG_EXPIRED);
                resultDto.setMsg(EnumResultStatus.ERROR_MSG_EXPIRED.getName());
                return responseStr(JSON.toJSONString(resultDto));
            }else{
                pwd = _pwd.toString();
            }
        }
        String validateCode = request.getAttribute("validateCode")==null?null:request.getAttribute("validateCode").toString();
        Integer clientId = request.getAttribute("clientId") == null ? null : Integer.parseInt(request.getAttribute("clientId").toString());
        if (StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(pwd)) {
            try {
                resultDto = ydCallService.synchroData(mobile.trim(),validateCode.trim(), pwd.trim(), clientId);
                resultDto.setData(null);
                //调用成功清除缓存密码
                redisUtil.remove(mobile+"_pwd");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else {
            resultDto.setMsg("手机号,密码错误!");
            resultDto.setStatus(EnumResultStatus.ERROR);
        }
        return responseStr(JSON.toJSONString(resultDto));
    }



    @RequestMapping(value = "getData")
    @ResponseBody
    public String getData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        List<DataDto> list = ydCallDetailClientService.getListByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(list);
        return responseStr(JSON.toJSONString(resultDto));
    }

    @RequestMapping(value = "getTop10Data")
    @ResponseBody
    public String getTop10Data(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        ResultDto dto = new ResultDto();
        List<TopDataDto> list = ydCallDetailClientService.getTop10ByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(list);
        return responseStr(JSON.toJSONString(resultDto));
    }

    /**
     * 获取通话总次数top10
     */
    @RequestMapping(value = "getCallTimesTop10Data")
    @ResponseBody
    public String getCallTimesTop10Data(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        List<CallTimesDateDTO> list = ydCallDetailClientService.getCallTimesTop10ByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(list);
        return responseStr(JSON.toJSONString(resultDto));
    }

    /**
     * 获取通话总时长top10
     */
    @RequestMapping(value = "getCallDurationTop10Data")
    @ResponseBody
    public String getCallDurationTop10Data(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        List<CallDurationDateDTO> list = ydCallDetailClientService.getCallDurationTop10ByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(list);
        return responseStr(JSON.toJSONString(resultDto));
    }

    /**
     * 获取夜间通话总次数
     */
    @RequestMapping(value = "getNightCallTimesData")
    @ResponseBody
    public String getNightCallTimesData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        CallTimesDateDTO result = ydCallDetailClientService.getNightCallTimesByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(result);
        return responseStr(JSON.toJSONString(resultDto));
    }

    /**
     * 获取夜间通话总时长
     */
    @RequestMapping(value = "getNightCallDurationData")
    @ResponseBody
    public String getNightCallDurationData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        CallDurationDateDTO result = ydCallDetailClientService.getNightCallDurationByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(result);
        return responseStr(JSON.toJSONString(resultDto));
    }

    /**
     * 漫游天数，过去6个月存在漫游通话记录的天数
     */
    @RequestMapping(value = "getRoamingDaysData")
    @ResponseBody
    public String getRoamingDaysData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        Integer result = ydCallDetailClientService.getRoamingDaysByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(result);
        return responseStr(JSON.toJSONString(resultDto));
    }

    /**
     * 手机静默情况   无通话记录的天数
     */
    @RequestMapping(value = "getSleepingDaysData")
    @ResponseBody
    public String getSleepingDaysData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        Integer result = ydCallDetailClientService.getSleepingDaysByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(result);
        return responseStr(JSON.toJSONString(resultDto));
    }
}
