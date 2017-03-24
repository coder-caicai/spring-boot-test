package com.hbc.api.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hbc.api.dto.MobilePlaceDto;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.Thread.sleep;

/**
 * Created by cheng on 16/6/29.
 */
public class MobilePlaceUtil {

    private static Logger logger = LoggerFactory.getLogger(MobilePlaceUtil.class);

//    public static MobilePlaceDto reGetIpDetail(String mobile){
//        String url = "http://sj.apidata.cn/?mobile="+mobile;
//        Connection con = Jsoup.connect(url);
//        try {
//            Connection.Response response = con.timeout(30000).ignoreContentType(true).method(Connection.Method.GET).execute();
//            logger.error(response.body());
//            System.out.println(response.body());
//            Map<String,Map<String,String>> objMap = (  Map<String,Map<String,String>>) JSON.parse(response.body());
//            String province = objMap.get("data").get("province");
//            String isp = objMap.get("data").get("isp");
//            String telString = objMap.get("data").get("mobile");
//            MobilePlaceDto dto = new MobilePlaceDto();
//
//            dto.setTelString(telString);
//            dto.setProvince(province);
//            dto.setCarrier(isp);
//            String carrier = PinYinUtil.getPinYinHeadChar(dto.getCarrier());
//            String str = carrier.substring(carrier.length()-2);
//            dto.setOperator(str);
//            System.out.println(JSON.toJSONString(dto));
//            return dto;
//
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        return null;
//    }

//    public  static MobilePlaceDto  getMobilePlace(String mobile){
//        if(StringUtils.isBlank(mobile)){
//            return  null;
//        }
//        int count = 0;
//        while (count<10){
//            MobilePlaceDto dto = reGetIpDetail(mobile);
//            if(dto != null){
//                return dto;
//            }else{
//                try {
//                    sleep(1000);
//                    logger.error("淘宝ip公用api调用失败!正在重新调用,已经调用了"+count+"次!");
//                } catch (InterruptedException e) {
//                    logger.error("淘宝ip公用api调用失败!正在重新调用,已经调用了"+count+"次!");
//                }
//            }
//            count ++;
//        }
//        return null;
//    }


    /**
     * api return data sample:
     * {"id":1821115,"prefix":"182","province":"北京","city":"北京","isp":"移动","code":"010","zip":"100000","types":"中国移动 GSM","phone":"18211155401"}
     * @param mobile
     * @return
     */
    public  static MobilePlaceDto  getMobilePlace(String mobile){
        String url = "http://localbase.hbc315.com/tel/search?phone="+mobile;
        Connection con = Jsoup.connect(url);
        MobilePlaceDto dto = new MobilePlaceDto();
        try {
            Connection.Response response = con.timeout(30000).ignoreContentType(true).method(Connection.Method.GET).execute();
            //Map<String,String> objMap = (  Map<String,String>) JSON.parse(response.body());
            JSONObject jsonObj = JSON.parseObject(response.body());
            String province = jsonObj.getString("province");
            String isp = jsonObj.getString("isp");
            String telString = jsonObj.getString("phone");

            dto.setTelString(telString);
            dto.setProvince(province);
            dto.setCarrier(isp);
            String carrier = PinYinUtil.getPinYinHeadChar(dto.getCarrier());
            String str = carrier.substring(carrier.length()-2);
            dto.setOperator(str);
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return dto;
    }



}
