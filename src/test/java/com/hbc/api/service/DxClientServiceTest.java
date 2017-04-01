package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.hbc.api.Application;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.DataDto;
import com.hbc.api.util.MobilePlaceUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.util.List;

/**
 * Created by cheng on 16/11/16.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class DxClientServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DxGdCallClientService dxGdCallClientService;

    @Autowired
    private DxCallDetailClientService dxCallDetailClientService;

    @Autowired
    private DxCallClientService dxCallClientService;

    @Test
    public void test() throws Exception {
//        17722862060  989977
//        18928320007 110726

        Boolean result = dxCallClientService.login("18918355776","135790",10000);
        logger.info(result+"");
    }

    @Test
    public void testSpider() throws Exception {
//       EnumResultStatus result =  dxGdCallClientService.synchroData("18928320007","7FK5");
//        logger.info(result.getName());
    }

    @Test
    public void testList() throws Exception {
        List<DataDto> list = dxCallDetailClientService.getListByMobile("18911206086");
        logger.info(JSON.toJSONString(list));
    }
}
