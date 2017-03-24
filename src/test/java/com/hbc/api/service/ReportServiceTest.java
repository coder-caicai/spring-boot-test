package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.hbc.api.Application;
import com.hbc.api.dto.DataDto;
import com.hbc.api.dto.ResultDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * Created by cheng on 16/11/16.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class ReportServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReportService reportService;

    @Test
    public void test() throws Exception {
        String data = "mdn=18610687468&name=wangxiaobo&idCard=411303198807090035&relation1=&contact1=&contactMdn1=&contact2=&relation2=&contactMdn2=&accessToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjb21wYW55Ijoi5aWH55GeIiwiY2xpZW50SWQiOjIwMDAwMSwiZXhwaXJlIjoxNDgyNTQ4NDAwMDAwfQ.m_l23nYPsWHojMtk5pueZ2Io8yiher1nhcpCmcNoPZQmdn=18610687468&name=wangxiaobo&idCard=411303198807090035&relation1=5&contact1=111&contactMdn1=13122312332&contact2=222&relation2=5&contactMdn2=13324234232&accessToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjb21wYW55Ijoi5aWH55GeIiwiY2xpZW50SWQiOjIwMDAwMSwiZXhwaXJlIjoxNDgyNTQ4NDAwMDAwfQ.m_l23nYPsWHojMtk5pueZ2Io8yiher1nhcpCmcNoPZQmdn=18610687468&name=wangxiaobo&idCard=411303198807090035&relation1=5&contact1=111&contactMdn1=13122312332&contact2=222&relation2=5&contactMdn2=13324234232&accessToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjb21wYW55Ijoi5aWH55GeIiwiY2xpZW50SWQiOjIwMDAwMSwiZXhwaXJlIjoxNDgyNTQ4NDAwMDAwfQ.m_l23nYPsWHojMtk5pueZ2Io8yiher1nhcpCmcNoPZQmdn=18610687468&name=wangxiaobo&idCard=411303198807090035&relation1=5&contact1=111&contactMdn1=13122312332&contact2=222&relation2=5&contactMdn2=13324234232&accessToken=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjb21wYW55Ijoi5aWH55GeIiwiY2xpZW50SWQiOjIwMDAwMSwiZXhwaXJlIjoxNDgyNTQ4NDAwMDAwfQ.m_l23nYPsWHojMtk5pueZ2Io8yiher1nhcpCmcNoPZQ";
        String result = reportService.getReport(data);
        logger.info(JSON.toJSONString(result));
    }

}
