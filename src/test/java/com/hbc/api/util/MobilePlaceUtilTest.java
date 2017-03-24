package com.hbc.api.util;

import com.alibaba.fastjson.JSON;
import com.hbc.api.Application;
import com.hbc.api.dto.MobilePlaceDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by cheng on 16/10/31.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class MobilePlaceUtilTest {

    private Logger logger = LoggerFactory.getLogger(getClass());




    @Test
    public void createToken(){
        MobilePlaceDto dto = MobilePlaceUtil.getMobilePlace("17722862060");
        logger.info(JSON.toJSONString(dto));
    }
}
