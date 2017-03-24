package com.hbc.api.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hbc.api.Application;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cheng on 16/10/31.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class HttpClientTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Test
    public void test(){
        String url = "http://114.113.66.37/xiaojinku/api/sendDataToKafka";
        Map<String,String> map = Maps.newHashMap();
        map.put("user","pachong");
        map.put("time","1449125933");
        map.put("md5","3a337a8d6a3f0471c6e7f41f34da324f");
        map.put("mobile","18600774142");
        map.put("month","201702");
//        String result = httpClientUtil.sendDataByPost(url,map);
        String result = httpClientUtil.sendDataToKafka("18211155401");
        logger.info(result+"");
    }

}
