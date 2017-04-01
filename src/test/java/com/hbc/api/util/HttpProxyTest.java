package com.hbc.api.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hbc.api.Application;
import com.hbc.api.dto.MobilePlaceDto;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class HttpProxyTest {

    private Logger logger = LoggerFactory.getLogger(getClass());




    @Test
    public void test(){
        String url = "http://www.baidu.com";
        HttpGet getMethod = new HttpGet(url);
        ResponseValue res = CommonHttpMethod.doGet(getMethod);
        logger.info(res.getResponse());
    }



    @Test
    public void testPost() throws IOException {

        String url = "https://www.baidu.com/";
        HttpPost httpPost = new HttpPost(url);
//        String data = "{\"mobile\":\"15618672909\",\"realName\":\"郭智超\",\"idCard\":\"222222222222222200\"}";
//        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);//
//        httpPost.setEntity(myEntity);
        ResponseValue res = CommonHttpMethod.doPostSSL(httpPost);
        logger.info(res.getResponse());
//        Connection con = Jsoup.connect("http://www.baidu.com");
//        con.header("Content-Type", "application/json;charset=UTF-8");
//        con.header("APIAuthorize-Agent",
//                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
//
//        Map<String,String> map = Maps.newHashMap();
//        map.put("mobile","15618672909");
//        map.put("realName","郭智超");
//        map.put("idCard","222222222222222200");
//        Connection.Response response = con.data(map).timeout(30000).ignoreContentType(true).method(Connection.Method.POST)
//                .execute();
//        ExecutorService executor = Executors.newCachedThreadPool();
//        for (int i = 0; i < 50; i++) {
//            executor.execute(()->{
//                try {
//                    test2();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
////        executor.shutdown(); // This will make the executor accept no new threads and finish all existing threads in the queue
//
//        executor.shutdown(); // This will make the executor accept no new threads and finish all existing threads in the queue
//        while (!executor.isTerminated()) { // Wait until all threads are finish,and also you can use "executor.awaitTermination();" to wait
//        }
//        System.out.println("Finished all threads");


    }

    public void test2() throws IOException {
        String url = "http://api.antifraud.hbc315.com/common/blackList?access_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjb21wYW55SWQiOjEwLCJjbGllbnRJZCI6MTYwMDAwMSwiaXBBZGRycyI6IioiLCJleHBpcmUiOjE0ODQyNzI4MDAwMDB9.23LNtipoq9Sc9A8feGHmXeRojq7EHfWa-_wNvDgZphQ";
        CloseableHttpClient httpClient = HttpClients.custom().build();
        HttpPost method = new HttpPost(url);
        method.setHeader("Connection", "close");
        method.setHeader("Cookie", "special-cookie=value");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mobile", "15618672909");
        jsonObject.put("realName", "郭智超");
        jsonObject.put("idCard", "222222222222222200");
        StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");// 解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        method.setEntity(entity);
        HttpResponse response = httpClient.execute(method);
        // 请求结束，返回结果
        String resData = EntityUtils.toString(response.getEntity());
        System.out.println(resData);
        response.getEntity().getContent().close();
        method.releaseConnection();
        httpClient.close();
    }


    private  String getProxyIp(){
        Connection con = null;
        String httpProxyUrl = "http://dps.kuaidaili.com/api/getdps/?orderid=999033723725968&num=1&ut=1&sep=1";
        con = Jsoup.connect(httpProxyUrl);
        try {
            Connection.Response response = con.timeout(30000).method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .followRedirects(true)
                    .execute();
            logger.info("请求ip代理响应:"+response.body());
            if(response.body().contains("ERROR")){
                return  null;
            }
            if(response.statusCode() == 200){
                return response.body();
            }else{
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("获得代理ip出错!");
        }
        return null;
    }
}
