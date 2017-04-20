package com.hbc.api.controller;

import com.alibaba.fastjson.JSON;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.conf.LoadTIspNumberInfo;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.model.TIspNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by cheng on 16/7/26.
 */

@Controller
@RequestMapping("/call")
public class CallController {

    @Autowired
    public LoadTIspNumberInfo loadTIspNumberInfo;

    @RequestMapping(value = "/simLogin", method = RequestMethod.POST)
    public String synchroData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        TIspNumber tIspNumber = loadTIspNumberInfo.getByPhone(mobile);
        ResultDto errorDto = new ResultDto();
        if(tIspNumber == null){
            errorDto.setMsg("获取手机号运营商和归属地服务异常,请稍后重试!");
            errorDto.setStatus(EnumResultStatus.ERROR);
            String errorResponse = JSON.toJSONString(errorDto);
            return errorResponse;
        }
        String isp = "";
        if(tIspNumber.getIsp().contains("联通")){
            isp = "联通";
        }else if(tIspNumber.getIsp().contains("电信")){
            isp = "电信";
        }else if(tIspNumber.getIsp().contains("移动")){
            isp = "移动";
        }

        switch (isp) {
            case "联通":
                return "forward:/ltCall/syncData";
            case "电信":
                if (tIspNumber.getProvince().equals("广东")) {
                    return "forward:/dxGdCall/syncData";
                } else {
                    return "forward:/dxCall/syncData";
                }
            case "移动":
                return "forward:/ydCall/sendMsg";
            default:
                errorDto.setMsg("获取手机号运营商和归属地服务异常,请稍后重试!");
                errorDto.setStatus(EnumResultStatus.ERROR);
                String errorResponse = JSON.toJSONString(errorDto);
                return errorResponse;
        }
    }

    @RequestMapping(value = "/crawlData", method = RequestMethod.POST)
    public String msgConfirm(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        String validateCode = request.getAttribute("validateCode") == null ? null : request.getAttribute("validateCode").toString();
        TIspNumber tIspNumber = loadTIspNumberInfo.getByPhone(mobile);
        ResultDto errorDto = new ResultDto();
        if(tIspNumber == null){
            errorDto.setMsg("获取手机号运营商和归属地服务异常,请稍后重试!");
            errorDto.setStatus(EnumResultStatus.ERROR);
            String errorResponse = JSON.toJSONString(errorDto);
            return errorResponse;
        }
        String isp = "";
        if(tIspNumber.getIsp().contains("联通")){
            isp = "联通";
        }else if(tIspNumber.getIsp().contains("电信")){
            isp = "电信";
        }else if(tIspNumber.getIsp().contains("移动")){
            isp = "移动";
        }
        switch (isp) {
            case "联通":
                return "forward:/ltCall/msgConfirm";
            case "电信":
                if ("广东".equals(tIspNumber.getProvince())) {
                    return "forward:/dxGdCall/msgConfirm";
                } else {
                    return "forward:/dxCall/msgConfirm";
                }
            case "移动":
                return "forward:/ydCall/msgConfirm";
            default:
                errorDto.setMsg("获取手机号运营商和归属地服务异常,请稍后重试!");
                errorDto.setStatus(EnumResultStatus.ERROR);
                String errorResponse = JSON.toJSONString(errorDto);
                return errorResponse;
        }
    }

}
