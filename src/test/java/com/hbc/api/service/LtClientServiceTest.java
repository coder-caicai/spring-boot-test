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
        ResultDto result = ltCallService.synchroData("18513068661","090801",10000);
        logger.info(JSON.toJSONString(result));
    }

    @Test
    public void testSpider() throws Exception {
//       EnumResultStatus result =  ydCallClientService.synchroData("","790364");
//        logger.info(result.getName());
    }

    @Test
    public void testList() throws Exception {
        List<DataDto> list = ltCallDetailService.getCallDetail("18513068661");
        logger.info(JSON.toJSONString(list));
    }
}
