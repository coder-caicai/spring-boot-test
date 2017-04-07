package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.hbc.api.Application;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.DataDto;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.util.MobilePlaceUtil;
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
public class LtClientServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LtCallService  ltCallService;

    @Autowired
    private LtCallDetailService ltCallDetailService;

    @Test
    public void test() throws Exception {
        ResultDto result = ltCallService.login("13161014656","265974",10000,null,null);
        logger.info(JSON.toJSONString(result));
    }

    @Test
    public void testSpider() throws Exception {
        ResultDto dto = ltCallService.msgConfirm("13161014656","048351");
        logger.info(JSON.toJSONString(dto));
    }

    @Test
    public void testList() throws Exception {
        List<DataDto> list = ltCallDetailService.getCallDetail("18513068661");
        logger.info(JSON.toJSONString(list));
    }
}
