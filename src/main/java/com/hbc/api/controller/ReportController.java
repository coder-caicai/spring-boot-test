package com.hbc.api.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.DataDto;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * Created by cheng on 16/9/13.
 */

@Controller
@RequestMapping("report")
public class ReportController extends BaseContoller {

    @Autowired
    private ReportService reportService;


    @RequestMapping("getReportPng")
    @ResponseBody
    public String index(HttpServletRequest request) throws IOException, InterruptedException {
        ResultDto dto = new ResultDto();
        String name = request.getAttribute("name") == null ? "" : request.getAttribute("name").toString();
        String mdn = request.getAttribute("mdn") == null ? "" : request.getAttribute("mdn").toString();
        String idCard = request.getAttribute("idCard") == null ? "" : request.getAttribute("idCard").toString();

        String relation1 = request.getAttribute("relation1") == null ? "" : request.getAttribute("relation1").toString();
        String contact1 = request.getAttribute("contact1") == null ? "" : request.getAttribute("contact1").toString();
        String contactMdn1 = request.getAttribute("contactMdn1") == null ? "" : request.getAttribute("contactMdn1").toString();

        String contact2 = request.getAttribute("contact2") == null ? "" : request.getAttribute("contact2").toString();
        String relation2 = request.getAttribute("relation2") == null ? "" : request.getAttribute("relation2").toString();
        String contactMdn2 = request.getAttribute("contactMdn2") == null ? "" : request.getAttribute("contactMdn2").toString();

        String token = request.getAttribute("accessToken") == null ? "": request.getAttribute("accessToken").toString();
        if(StringUtils.isEmpty(name) ||
           StringUtils.isEmpty(mdn) ||
           StringUtils.isEmpty(idCard) ||
           StringUtils.isEmpty(token) ){
                dto.setStatus(EnumResultStatus.ERROR_DATA);
                dto.setMsg("参数不能为空!");
        }else{
            String data = "mdn="+mdn+"&name="+name+"&idCard="+idCard
                    +"&relation1="+relation1+"&contact1="+contact1+"&contactMdn1="+contactMdn1
                    +"&contact2="+contact2+"&relation2="+relation2+"&contactMdn2="+contactMdn2
                    +"&accessToken="+token;
            logger.info("报告参数:" + data);
            String result = reportService.getReport(data);
            dto.setStatus(EnumResultStatus.SUCCESS);
            dto.setMsg("服务调用成功!");
            dto.setData(result);
            sleep(2000);
        }
        return responseStr(JSON.toJSONString(dto));

    }

}
