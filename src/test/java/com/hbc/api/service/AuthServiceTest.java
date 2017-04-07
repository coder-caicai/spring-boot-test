package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.hbc.api.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cheng on 16/11/16.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class AuthServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthService authService;

    @Test
    public void test() throws Exception {
        Boolean result = authService.createAuth("test","bj_ccz@sina.com");
        logger.info(JSON.toJSONString(result));
    }

    @Test
    public void login() throws Exception {
        Map result = authService.login("200031","11$GXJIroqPNXUhomMXddMfjuvSgj7.XpO5ScQ3/DgV43Ne0b62WZg7m");
        logger.info(JSON.toJSONString(result));
    }

}
