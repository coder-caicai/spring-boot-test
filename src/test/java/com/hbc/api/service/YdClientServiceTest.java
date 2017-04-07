package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.hbc.api.Application;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.DataDto;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.util.CommonHttpMethod;
import com.hbc.api.util.MobilePlaceUtil;
import com.hbc.api.util.ResponseValue;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.ReflectionUtils;

import java.util.List;

/**
 * Created by cheng on 16/11/16.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class YdClientServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private YdCallClientService ydCallClientService;

    @Autowired
    private YdCallDetailClientService ydCallDetailClientService;

    @Test
    public void test() throws Exception {
//        ResultDto result = ydCallClientService.login("13716471599","200858",10000);
//        EnumResultStatus result2 = ydCallClientService.login("15945941941","538815",10000);
        ResultDto result = ydCallClientService.sendMsg("18321075426");
        logger.info(JSON.toJSONString(result));
//        logger.info(JSON.toJSONString(result2));
    }

    @Test
    public void testSpider() throws Exception {
       ResultDto result =  ydCallClientService.synchroData("18321075426","846766","123123",10000000);
        logger.info(JSON.toJSONString(result));
    }

    @Test
    public void testList() throws Exception{
//         String url = "http://localhost:8080/aes";
//        HttpPost httpPost = new HttpPost(url);
//        ResponseValue res = ydCallClientService.doPostSSLUID(httpPost,"");
//        logger.info(res.getResponse());
    }
}
