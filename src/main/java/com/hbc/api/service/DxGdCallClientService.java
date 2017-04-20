package com.hbc.api.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.DataDto;
import com.hbc.api.mapper.DxCallClientMapper;
import com.hbc.api.mapper.DxCallDetailClientMapper;
import com.hbc.api.model.DxCallClient;
import com.hbc.api.model.DxCallDetailClient;
import com.hbc.api.model.MobileInfo;
import com.hbc.api.util.DateUtil;
import com.hbc.api.util.FileUtils;
import com.hbc.api.util.HttpClientUtil;
import com.hbc.api.util.RedisUtil;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ccz
 * @since 2016-07-22 11:09
 */
@Service
public class DxGdCallClientService {

    @Autowired
    private DxCallClientMapper dxCallClientMapper;

    @Autowired
    private DxCallDetailClientMapper dxCallDetailClientMapper;

    @Autowired
    private MobileInfoService mobileInfoService;

    @Autowired
    private DxCallDetailClientService dxCallDetailClientService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private HttpClientUtil httpClientUtil;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

    private Logger logger = LoggerFactory.getLogger(getClass());


    private static String imei = "860308022570432";
    //	private static String mobile = "18928320007";
//	private static String pwd = "110726";
//    private static String mobile = "17722862060";
//    private static String pwd = "989977";
    private static String termcode = "MI2";
//    private static String qrytime = "201606";

    private String mobile;

    private String sessionkey;

    private String pwd;

    private String ssn;

    /**
     * 登录
     *
     * @param mobile
     * @param pwd
     * @return
     * @throws Exception
     */
    public String login(String mobile, String pwd,String path,Integer clientId) {
        redisUtil.remove(mobile);
        try {
            saveTimeLength(mobile);
        }catch (Exception e){
            logger.error("调用验证在网时长失败!手机号:"+mobile);
        }
//        String domain = "http://61.140.99.28:8080/MOService/api?v=2.1&name=jbAClientSp&category=android&imsi=" + mobile + "&paramStr=";
        try{
            Map<String, String> cookieMap = new HashMap<>();
            Connection con = null;
            String md5 = g("androidjbAClientSp2.1v:2!2F8H2d&]");
            Long m = System.currentTimeMillis();
            String sn = a(imei + mobile);
            String para = mobile + m + "3" + "jbAClientSp" + "2.1" + "v:2!2F8H2d&]" + "1" + "android" + "MI2" + sn + "";
            String sig = g(para);
            String paramStr = "ip=10.0.2.15&method=user.clientLogin2&esn=&termcode=MI2&timestamp=" + m + "&channel=3&v=2.1&format=1&sig=" + sig + "&loginaccount=" + mobile + "&loginpwd=" + pwd + "&logintype=2&appversion=3.1.8.1&isAutoLogin=5&bestAppId=DQzfuapa3bB0x7+CO0jc7L3/einh7G0MoV4NyurItPY=&sn=" + sn + "&validateCode=&mareaCode=";
            paramStr = a(paramStr, md5);
            String indexUrl = "http://61.140.99.28:8080/MOService/api?v=2.1&name=jbAClientSp&category=android&imsi=" + mobile + "&paramStr=" + paramStr;
            con = Jsoup.connect(indexUrl);
            con.header("APIAuthorize-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
            Response indexResponse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                    .execute();
            logger.info(indexResponse.body());
            String result = b(indexResponse.body(), md5);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if("12".equals(jsonObject.getString("errorcode"))){
                //获取图片验证码
                String imgUrl = "http://61.140.99.28:8080/MOService/validateCode?imsi=" + mobile;
                con = Jsoup.connect(imgUrl);
                con.header("APIAuthorize-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
                Response imgResponse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                        .execute();
                Connection.Response imageRs = con.cookies(cookieMap).ignoreContentType(true).followRedirects(true).method(Connection.Method.GET).execute();// 获取响应
                InputStream is = new ByteArrayInputStream(imageRs.bodyAsBytes());
                String imgPath =  FileUtils.bytesToFile(path,imageRs.bodyAsBytes());

//                InputStream is = new ByteArrayInputStream(imageRs.bodyAsBytes());
//                File file = new File("/Users/cheng/image.jpg");
//                OutputStream os = new FileOutputStream(file);
//                int bytesRead = 0;
//                byte[] buffer = new byte[8192];
//                while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
//                    os.write(buffer, 0, bytesRead);
//                }
//                os.close();
//                is.close();


                Map<String,String> map = new HashMap<>();
                map.put("pwd",pwd);
                map.put("clientId",clientId+"");
                redisUtil.set(mobile,map,Long.valueOf(60*10));
                return imgPath;
//                return "";
            }else{

                return null;
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }



    /**
     * 数据抓取
     *
     * @param
     * @return
     * @throws Exception
     */
    private String preSpiderDetail(String phoneNum, String img) {
        mobile = phoneNum;
        String domain = "http://61.140.99.28:8080/MOService/api?v=2.1&name=jbAClientSp&category=android&imsi=" + mobile + "&paramStr=";
        Map<String,String> map = (Map<String, String>) redisUtil.get(mobile);
        pwd = map.get("pwd");

        if(StringUtils.isBlank(pwd)){
            return "01";
        }
        try {
            String md5 = g("androidjbAClientSp2.1v:2!2F8H2d&]");
            String sn = a(imei + mobile + img);
            long time = System.currentTimeMillis();
            String para = mobile + time + "3" + "jbAClientSp" + "2.1" + "v:2!2F8H2d&]" + "1" + "android" + "MI2" + sn + img;
            String sig = g(para);
            String paramStr = "ip=10.111.25.68&method=user.clientLogin2&esn=&termcode=MI2&timestamp=" + time + "&channel=3&v=2.1&format=1&sig=" + sig + "&loginaccount=" + mobile + "&loginpwd=" + pwd + "&logintype=2&appversion=3.1.8.1&isAutoLogin=5&bestAppId=IiRF42ycq7YGBENQ0Jum1weoBqQIm8sxl7NllCPqrkQ=&sn=" + sn + "&validateCode=" + img + "&mareaCrWe=";
            paramStr = a(paramStr, md5);
            String loginUrl = domain + paramStr;
            Connection con = Jsoup.connect(loginUrl);
            con.header("APIAuthorize-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
            Response loginResponse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                    .execute();
            String loginResult = b(loginResponse.body(), md5);
            logger.info(loginResult);
            JSONObject loginJsonObject = JSONObject.parseObject(loginResult);
            if("01".equals(loginJsonObject.getString("errorcode"))) {//密码错误
                return "01";
            }else if("12".equals(loginJsonObject.getString("errorcode"))){//验证码错误
                return "12";
            }else if("00".equals(loginJsonObject.getString("errorcode"))){//登录成功
                sessionkey = loginJsonObject.getJSONObject("response").getString("sessionkey");
                ssn = sn;
                return "00";
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return "01";
    }


    private List<DxCallDetailClient> spiderDetail(String queryDate)  {
        //取详单
        String paramString3 = "";
        String md5 = g("androidjbAClientSp2.1v:2!2F8H2d&]");
        String domain = "http://61.140.99.28:8080/MOService/api?v=2.1&name=jbAClientSp&category=android&imsi=" + mobile + "&paramStr=";
        long localLong = System.currentTimeMillis();
        String sig = g("jbAClientSp" + mobile + "android" + termcode + sessionkey + localLong + paramString3 + "3" + "2.1" + "1" + "v:2!2F8H2d&]");
        String paramStr = "method=detail.queryTdetail&esn=&termcode=MI2&sessionkey=" + sessionkey + "&timestamp=" + localLong + "&password=" + pwd + "&sn=" + ssn + "&listingSign=1&qrytime=" + queryDate + "&currentPage=1&pageSize=10000&channel=3&format=1&sig=" + sig;
        paramStr = a(paramStr, md5);
        String detailUrl = domain + paramStr;
        Connection con = Jsoup.connect(detailUrl);
        con.header("APIAuthorize-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
        Response detailResponse = null;
        try {
            detailResponse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        String detailResult = b(detailResponse.body(), md5);
        logger.info("爬取明细结果:"+detailResult);
        JSONObject detailJsonObject = JSONObject.parseObject(detailResult);
        List<DxCallDetailClient> resultList = new ArrayList<>();
        if (detailJsonObject.getString("errorcode").equals("00")) {
            JSONObject response = detailJsonObject.getJSONObject("response");
            JSONArray item = response.getJSONArray("item");
            for (int i = 0; i < item.size(); i++) {
                JSONObject json = item.getJSONObject(i);
                DxCallDetailClient dxCallDetailClient = new DxCallDetailClient();
                dxCallDetailClient.setCallArea("无");
                dxCallDetailClient.setCallType(json.getString("callType").equals("主叫") ? "0" : "1");
                dxCallDetailClient.setCallFee(json.getDouble("totalfee") == null ? 0 : json.getDouble("totalfee"));
                dxCallDetailClient.setCallMobile(json.getString("oppPhone"));
                dxCallDetailClient.setCallStyle("2");
                dxCallDetailClient.setCallTime(json.getString("callTime"));
                dxCallDetailClient.setCallTimeCost(json.getString("callDuration"));
                resultList.add(dxCallDetailClient);
            }
        }
        return resultList;
    }




    /**
     * 同步数据
     *
     * @param mobile
     * @param msg
     * @return
     * @throws Exception
     */
    public EnumResultStatus synchroData(String mobile, String msg) throws Exception {
        //先检查数据库中是否已经存在该用户数据
        String preResult = preSpiderDetail(mobile, msg);
        if("01".equals(preResult)){
            return EnumResultStatus.ERROR_PWD;
        }else if("12".equals(preResult)){
            return EnumResultStatus.SUCCESS_IMG;
        }else if("00".equals(preResult)){
            List<DxCallClient> entityList = dxCallClientMapper.getListByMobile(mobile);
            List<String> dateList = DateUtil.getPreSixMonth();
            if (entityList == null || entityList.size() == 0) {
                for (String queryDate : dateList) {
                    saveBySpider(queryDate);
                }
            } else {
                entityList.sort((x,y) -> Integer.valueOf(x.getCallDate()).compareTo(Integer.valueOf(y.getCallDate())));
                DxCallClient lastModel = entityList.get(entityList.size()-1);
                dxCallClientMapper.delete(lastModel);
                entityList.remove(entityList.size()-1);

                for (DxCallClient dxCallClient : entityList) {
                    if (dateList.indexOf(dxCallClient.getCallDate()) > -1) {
                        dateList.remove(dxCallClient.getCallDate());
                    }
                }
                for (String queryDate : dateList) {
                    saveBySpider(queryDate);
                }

            }
        }
        httpClientUtil.sendDataToKafka(mobile);
        return EnumResultStatus.SUCCESS;
    }

    /**
     * 保存爬虫数据
     *
     * @param
     * @throws IOException
     * @throws ParseException
     */

    @Async
    private void saveBySpider(String queryDate) {
        Map<String,String> map = (Map<String, String>) redisUtil.get(mobile);
        Integer clientId = Integer.parseInt(map.get("clientId"));

        List<DxCallDetailClient> dxCallDetailClients = spiderDetail(queryDate);
        if (dxCallDetailClients != null && dxCallDetailClients.size()>0) {
            DxCallClient dxCallClient = new DxCallClient();
            dxCallClient.setCallDate(queryDate);
            dxCallClient.setMobile(mobile);
            dxCallClient.setPwd(Md5Crypt.md5Crypt(pwd.getBytes()));
            dxCallClient.setProvince("广东");
            dxCallClient.setClientId(clientId);

            Integer id = dxCallClientMapper.insert(dxCallClient);
            Integer dxCallClientId = dxCallClient.getId();
            for (DxCallDetailClient dxCallDetailClient : dxCallDetailClients) {
                dxCallDetailClient.setCallId(dxCallClientId);
            }
            dxCallDetailClientMapper.insertList(dxCallDetailClients);
//            sendData(mobile,dxCallDetailClientService.detailListToDataDto(dxCallDetailClients));
        }
    }


    //-------------以下方法名暂不修改 ,方便逆向查找问题-------------------------------------------------------
    public static final String a(String paramString) {
        BigInteger param = new BigInteger(paramString.getBytes());
        return new BigInteger("02013302259969").xor(param).toString(16);
    }
    public static String g(String paramString) {
        if ((paramString == null) || (paramString.length() == 0)) {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }
        StringBuffer localStringBuffer = new StringBuffer();
        int m = 0;
        while (true) {
            try {
                MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
                localMessageDigest.update(paramString.getBytes());
                byte[] md5Digest = localMessageDigest.digest();
                if (m < md5Digest.length) {
                    if ((md5Digest[m] & 0xFF) < 16)
                        localStringBuffer.append("0" + Integer.toHexString(md5Digest[m] & 0xFF));
                    else
                        localStringBuffer.append(Integer.toHexString(md5Digest[m] & 0xFF));
                } else {
                    return localStringBuffer.toString().toUpperCase();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            m += 1;
        }

    }

    // 加密
    public static final String a(String paramString1, String paramString2) {
        BigInteger bigInteger = new BigInteger(paramString1.getBytes());
        return new BigInteger(paramString2.hashCode() + "").xor(bigInteger).toString(16);
    }

    // 解密
    public static final String b(String paramString1, String paramString2) {
        BigInteger bigInteger = new BigInteger(paramString2.hashCode() + "");
        try {
            paramString1 = new String(new BigInteger(paramString1, 16).xor(bigInteger).toByteArray());
            return paramString1;
        } catch (Exception e) {
        }
        return "";
    }

    // 解密
    public static final String b(String paramString2) {
        BigInteger bigInteger = new BigInteger("02013302259969".hashCode() + "");
        try {
            return  new String(new BigInteger(paramString2, 16).xor(bigInteger).toByteArray());
        } catch (Exception e) {
        }
        return "";
    }

    @Async
    private boolean saveTimeLength(String mobile){
        MobileInfo mobileInfo = mobileInfoService.getByMobile(mobile);
        if(mobileInfo == null){
            String result =  dxCallDetailClientService.getTimeLength(mobile);
            if(StringUtils.isNotBlank(result)){
                mobileInfoService.save(mobile,"dx",result,null,null);
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }


}