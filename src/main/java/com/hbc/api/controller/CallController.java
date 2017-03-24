package com.hbc.api.controller;

import com.alibaba.fastjson.JSON;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.MobilePlaceDto;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.util.MobilePlaceUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by cheng on 16/7/26.
 */

@Controller
@RequestMapping("/call")
public class CallController {

    @RequestMapping(value = "/simLogin", method = RequestMethod.POST)
    public String synchroData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();

        ResultDto errorDto = new ResultDto();
        errorDto.setMsg("获取手机号运营商和归属地服务异常,请稍后重试!");
        errorDto.setStatus(EnumResultStatus.ERROR);
        String errorResponse = JSON.toJSONString(errorDto);

        if (operator != null && !operator.equals("")) {
            switch (operator) {
                case "lt":
                    return "forward:/ltCall/syncData";
                case "dx":
                    if (mobilePlaceDto.getProvince().contains("广东")) {
                        return "forward:/dxGdCall/syncData";
                    } else {
                        return "forward:/dxCall/syncData";
                    }
                case "yd":
                    return "forward:/ydCall/sendMsg";
                default:
                    return errorResponse;
            }
        } else {
            return errorResponse;
        }
    }

    @RequestMapping(value = "/crawlData", method = RequestMethod.POST)
    public String msgConfirm(HttpServletRequest request) {

        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        String validateCode = request.getAttribute("validateCode") == null ? null : request.getAttribute("validateCode").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/msgConfirm";
            case "dx":
                if ("广东".equals(mobilePlaceDto.getProvince())) {
                    return "forward:/dxGdCall/msgConfirm";
                } else {

                    return "forward:/dxCall/msgConfirm";
                }
            case "yd":
                return "forward:/ydCall/msgConfirm";
        }
        return "";
    }

    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public String getData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/getData";
            case "dx":
                return "forward:/dxCall/getData";
            case "yd":
                return "forward:/ydCall/getData";
        }
        return "";
    }

    @RequestMapping(value = "/getTop10Data", method = RequestMethod.GET)
    public String getTop10Data(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/getTop10Data";
            case "dx":
                return "forward:/dxCall/getTop10Data";
            case "yd":
                return "forward:/ydCall/getTop10Data";
        }
        return "";
    }

    @RequestMapping(value = "/getCallTimesTop10Data", method = RequestMethod.GET)
    public String getCallTimesTop10Data(
            HttpServletRequest request
    ) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/getCallTimesTop10Data";
            case "dx":
                return "forward:/dxCall/getCallTimesTop10Data";
            case "yd":
                return "forward:/ydCall/getCallTimesTop10Data";
        }
        return "";
    }

    @RequestMapping(value = "/getCallDurationTop10Data", method = RequestMethod.GET)
    public String getCallDurationTop10Data(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/getCallDurationTop10Data";
            case "dx":
                return "forward:/dxCall/getCallDurationTop10Data";
            case "yd":
                return "forward:/ydCall/getCallDurationTop10Data";
        }
        return "";
    }

    @RequestMapping(value = "/getNightCallTimesData", method = RequestMethod.GET)
    public String getNightCallTimesData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/getNightCallTimesData";
            case "dx":
                return "forward:/dxCall/getNightCallTimesData";
            case "yd":
                return "forward:/ydCall/getNightCallTimesData";
        }
        return "";
    }

    @RequestMapping(value = "/getNightCallDurationData", method = RequestMethod.GET)
    public String getNightCallDurationData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/getNightCallDurationData";
            case "dx":
                return "forward:/dxCall/getNightCallDurationData";
            case "yd":
                return "forward:/ydCall/getNightCallDurationData";
        }
        return "";
    }

    @RequestMapping(value = "/getRoamingDaysData", method = RequestMethod.GET)
    public String getRoamingDaysData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/getRoamingDaysData";
            case "dx":
                return "forward:/dxCall/getRoamingDaysData";
            case "yd":
                return "forward:/ydCall/getRoamingDaysData";
        }
        return "";
    }

    @RequestMapping(value = "/getSleepingDaysData", method = RequestMethod.GET)
    public String getSleepingDaysData(HttpServletRequest request) {
        String mobile = request.getAttribute("mobile") == null ? null : request.getAttribute("mobile").toString();
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(mobile);
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/getSleepingDaysData";
            case "dx":
                return "forward:/dxCall/getSleepingDaysData";
            case "yd":
                return "forward:/ydCall/getSleepingDaysData";
        }
        return "";
    }

    @RequestMapping(value = "/getPdfReportInfo", method = RequestMethod.POST)
    public String getPdfReportInfo(HttpServletRequest request) {
        MobilePlaceDto mobilePlaceDto = MobilePlaceUtil.getMobilePlace(request.getAttribute("phoneNum").toString());
        String operator = mobilePlaceDto.getOperator();
        switch (operator) {
            case "lt":
                return "forward:/ltCall/getPdfReportInfo";
            case "dx":
                return "forward:/dxCall/getPdfReportInfo";
            case "yd":
                return "forward:/ydCall/getPdfReportInfo";
        }
        return "";
    }
}
