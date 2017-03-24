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
import com.hbc.api.service.DxCallClientService;
import com.hbc.api.service.DxCallDetailClientService;
import com.hbc.api.service.DxGdCallClientService;
import com.hbc.api.util.RedisUtil;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/dxGdCall")
public class DxGdCallController extends BaseContoller {


    @Autowired
    private DxCallDetailClientService dxCallDetailClientService;

    @Autowired
    private DxGdCallClientService dxGdCallClientService;

    @Autowired
    private RedisUtil redisUtil;


    @RequestMapping(value = "syncData" ,method = RequestMethod.POST)
    @ResponseBody
    public Object synchroData(HttpServletRequest request) {
        Integer clientId = Integer.parseInt(request.getAttribute("clientId").toString());
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
    	String pwd= request.getAttribute("pwd")==null?null:request.getAttribute("pwd").toString();
        ResultDto resultDto = new ResultDto();
        if(StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(pwd)){

            try {
                String path = request.getSession().getServletContext().getRealPath("/");
                String imgPath = dxGdCallClientService.login(mobile.trim(),pwd.trim(),path,clientId);
                if(StringUtils.isBlank(imgPath)){
                    resultDto.setMsg("手机号,密码错误!");
                    resultDto.setStatus(EnumResultStatus.ERROR);
                }else{
                    resultDto.setStatus(EnumResultStatus.SUCCESS_IMG);
                    resultDto.setMsg(EnumResultStatus.SUCCESS_IMG.getName());
                    resultDto.setData(imgPath);
                    redisUtil.set("time_"+mobile,mobile,Long.valueOf(60*2));
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

    @RequestMapping(value = "msgConfirm",method = RequestMethod.POST)
    @ResponseBody
    public Object msgConfirm(HttpServletRequest request) {
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
    	String validateCode = request.getAttribute("validateCode")==null?null:request.getAttribute("validateCode").toString();
        ResultDto resultDto = new ResultDto();
        try {
            if(!redisUtil.exists("time_"+mobile)){
                resultDto.setMsg("验证码过期!");
                resultDto.setStatus(EnumResultStatus.ERROR_LONG_TIME);
                return  responseStr(JSON.toJSONString(resultDto));
            }
            EnumResultStatus  result =dxGdCallClientService.synchroData(mobile.trim(),validateCode.trim());
            resultDto.setStatus(result);
            resultDto.setMsg(result.getName());
        } catch (Exception e) {
            logger.error(e.getMessage());
            resultDto.setMsg("验证码错误!");
            resultDto.setStatus(EnumResultStatus.ERROR_IMG);
        }
        return responseStr(JSON.toJSONString(resultDto));
    }

    @RequestMapping(value = "getData")
    @ResponseBody
    public String getData(HttpServletRequest request) {
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
       // ResultDto dto = new ResultDto();
        List<DataDto> list = dxCallDetailClientService.getListByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(list);
        return responseStr(JSON.toJSONString(resultDto));
    }

    @RequestMapping(value = "getTop10Data")
    @ResponseBody
    public String getTop10Data(HttpServletRequest request) {
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        //ResultDto dto = new ResultDto();
        List<TopDataDto> list = dxCallDetailClientService.getTop10ByMobile(mobile.trim());
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
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        List<CallTimesDateDTO> list = dxCallDetailClientService.getCallTimesTop10ByMobile(mobile.trim());
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
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        List<CallDurationDateDTO> list = dxCallDetailClientService.getCallDurationTop10ByMobile(mobile.trim());
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
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        CallTimesDateDTO result = dxCallDetailClientService.getNightCallTimesByMobile(mobile.trim());
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
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        CallDurationDateDTO result = dxCallDetailClientService.getNightCallDurationByMobile(mobile.trim());
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
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        Integer result = dxCallDetailClientService.getRoamingDaysByMobile(mobile.trim());
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
    	String mobile = request.getAttribute("mobile")==null?null:request.getAttribute("mobile").toString();
        ResultDto resultDto = new ResultDto();
        Integer result = dxCallDetailClientService.getSleepingDaysByMobile(mobile.trim());
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
        resultDto.setData(result);
        return responseStr(JSON.toJSONString(resultDto));
    }
    

    /**
     * 手获取pdf数据报告
     */
	@RequestMapping(value = "getPdfReportInfo")
	@ResponseBody
	public String getPdfReportInfo(HttpServletRequest request){
		ResultDto resultDto = new ResultDto();
		String name =  request.getAttribute("name")==null?null:request.getAttribute("name").toString();
		String phoneNum =  request.getAttribute("phoneNum")==null?null:request.getAttribute("phoneNum").toString();
		String idCard =  request.getAttribute("idCard")==null?null:request.getAttribute("idCard").toString();
		String contact1 =  request.getAttribute("contact1")==null?null:request.getAttribute("contact1").toString();
		String contactNum1 =  request.getAttribute("contactNum1")==null?null:request.getAttribute("contactNum1").toString();
		String contact2 =  request.getAttribute("contact2")==null?null:request.getAttribute("contact2").toString();
		String contactNum2 =  request.getAttribute("contactNum2")==null?null:request.getAttribute("contactNum2").toString();
		ReportDTO reportDto = dxCallDetailClientService.getReportInfo(name,phoneNum,idCard,contact1,contactNum1,contact2,contactNum2);
        resultDto.setStatus(EnumResultStatus.SUCCESS);
        if (reportDto!=null) {
        	resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
		}else {
			resultDto.setMsg("无数据信息！");
		}
        resultDto.setData(reportDto);
		return  responseStr(JSON.toJSONString(resultDto));
	}
}
