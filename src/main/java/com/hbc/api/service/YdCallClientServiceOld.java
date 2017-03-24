package com.hbc.api.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.mapper.YdCallClientMapper;
import com.hbc.api.mapper.YdCallDetailClientMapper;
import com.hbc.api.model.YdCallClient;
import com.hbc.api.model.YdCallDetailClient;
import com.hbc.api.util.*;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cheng on 16/9/8.
 */

//@Transactional
@Service
public class YdCallClientServiceOld {

    private Logger logger = LoggerFactory.getLogger(getClass());

    //    private  String month = "2016-08";
    // numEachPage最大是200
    private int numEachPage = 200;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private YdCallClientMapper ydCallClientMapper;

    @Autowired
    private YdCallDetailClientMapper ydCallDetailClientMapper;


//    private String cid = "C9zXwvbuUoKb0v7AhOkXRX";

    /**
     * 登录
     *
     * @param mobile
     * @param passWord
     * @return
     */
    public ResultDto simLogin(String mobile, String passWord, Integer clientId) {
        redisUtil.remove(mobile);
        ResultDto dto = new ResultDto();
        try {
            String JSESSIONID = "";
            String UID = "";
            String cid = UUID.randomUUID().toString();
            //加密参数
            String EncryptMobile = YD_RSA_Encrypt.getEntrypt("leadeon" + mobile + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
            String EncryptServicePassword = YD_RSA_Encrypt.getEntrypt("leadeon" + passWord + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
            String data = "{\"cid\":\"+" + cid + "+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+" + cid + "+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"3\",\"reqBody\":{\"ccPasswd\":\""
                    + EncryptServicePassword + "\",\"cellNum\":\"" + EncryptMobile
                    + "\",\"sendSmsFlag\":\"1\"},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"\"}";
            //登录
            String url = "https://clientaccess.10086.cn/biz-orange/LN/uamlogin/login";
            logger.info("爬虫登录开始:" + url);
            HttpPost postMethod = new HttpPost(url);
//            long a = System.currentTimeMillis();
//            HttpHost proxy = new HttpHost("223.13.67.92", 9797, "https");
//            long b = System.currentTimeMillis();
//            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//            postMethod.setConfig(config);
//            logger.info("代理耗时:" + (b-a));
            StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);//
            postMethod.setEntity(myEntity);
            ResponseValue res = doPostSSLUID(postMethod, mobile);
            logger.info(res.getResponse());
            if (res != null) {
                JSONObject resJson = JSONObject.parseObject(res.getResponse());
                dto.setMsg(resJson.getString("retDesc"));
                if (resJson.getString("retCode").equals("000000")) {
                    dto.setStatus(EnumResultStatus.SUCCESS_MSG);
                }else if(resJson.getString("retCode").equals("110001")){
                    dto.setStatus(EnumResultStatus.ERROR_BUSY);
                    return dto;
                }else{
                    dto.setStatus(EnumResultStatus.ERROR_PWD);
                    return dto;
                }
            }
            logger.info(res.getResponse());
            for (Cookie c : res.getCookies()) {
                if (c.getName().equals("JSESSIONID")) {
                    JSESSIONID = c.getValue();
                }
                if (c.getName().equals("UID")) {
                    UID = c.getValue();
                }
            }
            logger.info("爬虫登录结束:" + url);
            data = "{\"cid\":\"+" + cid + "+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+" + cid + "+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"0\",\"reqBody\":{\"cellNum\":\""
                    + mobile + "\"},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"\"}";
            //发送短信验证码
            String msgUrl = "https://clientaccess.10086.cn/biz-orange/LN/uamrandcode/sendMsgLogin";
            logger.info("发送短信验证码开始:" + msgUrl);
            HttpPost msgPostMethod = new HttpPost(msgUrl);
            String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
            msgPostMethod.setHeader("Cookie", cookie);
            StringEntity myMsgEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
            msgPostMethod.setEntity(myMsgEntity);
            ResponseValue resMsg = CommonHttpMethod.doPostSSL(msgPostMethod);
            JSONObject result = JSONObject.parseObject(resMsg.getResponse());
            logger.info("发送短信验证码结束:" + msgUrl);
            if (result.getString("retDesc").equals("SUCCESS")) {
                JSONObject reData = JSONObject.parseObject(redisUtil.get(mobile).toString());
                reData.put("JSESSIONID", JSESSIONID);
                reData.put("pwd", passWord);
                reData.put("cid", cid);
                reData.put("clientId",clientId);
                redisUtil.set(mobile, reData, Long.valueOf(60 * 10));
                dto.setMsg("发送短信验证码成功!");
                return dto;
            } else {
                dto.setMsg("发送短信验证码失败!请稍后重试");
                dto.setStatus(EnumResultStatus.ERROR);
                return dto;
            }
        } catch (Exception e) {
            for(int i =0 ;i<e.getStackTrace().length;i++) {
                logger.error(e.getStackTrace()[i].toString());
            }
            logger.error(e.getMessage());
            dto.setMsg("发送短信验证码出现异常!请稍后重试");
            dto.setStatus(EnumResultStatus.ERROR);
            return dto;
        }
    }


    /**
     * 增加登录重试机制
     * @param mobile
     * @param passWord
     * @param clientId
     * @return
     */
    public ResultDto login(String mobile, String passWord,Integer clientId) {
        try {
            for (int i = 0; i < 20; i++) {
                ResultDto result = simLogin(mobile, passWord, clientId);
                if (result.getStatus().equals(EnumResultStatus.ERROR_BUSY)) {
                    Thread.sleep(300);
                    logger.info("移动api返回系统繁忙!开启重试调用次数:"+i);
                } else {
                    return result;
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        logger.error("调用移动api登录出现异常");
        return null;

    }



    /**
     * 同步数据
     *
     * @param mobile
     * @param msg
     * @return
     */
    public ResultDto synchroData(String mobile, String msg) {
        ResultDto dto = checkMsg(mobile, msg);
        if (!dto.getStatus().equals(EnumResultStatus.SUCCESS)) {
            return dto;
        }
        List<YdCallClient> entityList = ydCallClientMapper.getListByMobile(mobile);
        List<String> dateList = DateUtil.getPreSixMonth();
        if (entityList == null || entityList.size() == 0) {
            for (String queryDate : dateList) {
                dto = saveBySpider(mobile, msg, queryDate.substring(0, 4).concat("-").concat(queryDate.substring(4, 6)));
                if(!dto.getStatus().equals(EnumResultStatus.SUCCESS)){
                    return dto;
                }
            }
        } else {
            for (YdCallClient ydCallClient : entityList) {
                if (dateList.indexOf(ydCallClient.getCallDate()) > -1) {
                    dateList.remove(ydCallClient.getCallDate());
                }
            }
            for (String queryDate : dateList) {
                dto = saveBySpider(mobile, msg, queryDate.substring(0, 4).concat("-").concat(queryDate.substring(4, 6)));
                if(!dto.getStatus().equals(EnumResultStatus.SUCCESS)){
                    return dto;
                }
            }
        }
        return dto;
    }

    /**
     * 保存爬虫数据
     *
     * @param
     * @throws IOException
     * @throws ParseException
     */
    @Transactional
    private ResultDto saveBySpider(String mobile, String msg, String month) {
        ResultDto dto = new ResultDto();
        dto = jsonToList(mobile, msg, month);
        if (dto.getStatus().equals(EnumResultStatus.SUCCESS) ) {
            List<YdCallDetailClient> detailResult =(List<YdCallDetailClient>) dto.getData();
            Map<String, String> redisMap = (Map<String, String>) redisUtil.get(mobile);
            YdCallClient ydCallClient = new YdCallClient();
            ydCallClient.setCallDate(month.replace("-", ""));
            ydCallClient.setMobile(mobile);
            ydCallClient.setPwd(Md5Crypt.md5Crypt(redisMap.get("pwd").getBytes()));
            JSONObject reData = (JSONObject) redisUtil.get(mobile);
            ydCallClient.setClientId(reData.getInteger("clientId"));
            logger.info("主表数据开始插入");
            ydCallClientMapper.insert(ydCallClient);
            logger.info("主表数结束插入");
            Integer ydCallClientId = ydCallClient.getId();
            for (YdCallDetailClient ydCallDetailClient : detailResult) {
                ydCallDetailClient.setCallId(ydCallClientId);
            }
            logger.info("明细数据开始插入,数据大小:" + detailResult.size());
            ydCallDetailClientMapper.insertList(detailResult);
            logger.info("明细数据结束插入,数据大小:" + detailResult.size());
            dto.setStatus(EnumResultStatus.SUCCESS);
            dto.setMsg("数据保存成功!");
            return dto;
        } else {
            logger.error("获取明细失败!请检查验证码是否过期");
            return dto;
        }
    }

    private ResultDto jsonToList(String mobile, String msg, String month) {
        ResultDto dto = new ResultDto();
        List<YdCallDetailClient> resList = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            String result = reGetDetailData(mobile, month, i);
            logger.info(result);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getString("retCode").equals("000000")) {
                    JSONObject rspBody = jsonObject.getJSONObject("rspBody");
                    JSONArray callList = rspBody.getJSONArray("callList");
                    callList.forEach(e -> {
                        JSONObject data = JSONObject.parseObject(e.toString());
                        JSONObject tmemRecord = data.getJSONObject("tmemRecord");
                        YdCallDetailClient ydCallDetailClient = new YdCallDetailClient();
                        ydCallDetailClient.setStartTime(tmemRecord.getString("startTime"));
                        ydCallDetailClient.setCommPlac(tmemRecord.getString("commPlac"));
                        ydCallDetailClient.setCommFee(tmemRecord.getDouble("commFee"));
                        ydCallDetailClient.setCommMode(tmemRecord.getString("commMode").equals("主叫") ? "0" : "1");
                        ydCallDetailClient.setCommTime(tmemRecord.getString("commTime"));
                        ydCallDetailClient.setCommTimeH5(tmemRecord.getString("commTimeH5"));
                        ydCallDetailClient.setCommType(tmemRecord.getString("commType").equals("本地通话") ? "0" : "1");
                        ydCallDetailClient.setEachOtherNm(tmemRecord.getString("eachOtherNm"));
                        ydCallDetailClient.setMealFavorable(tmemRecord.getString("mealFavorable"));
                        resList.add(ydCallDetailClient);
                    });
                    if (callList.size() < 200) {
                        break;
                    }
                } else if("203100".equals(jsonObject.getString("retCode"))) {//该月份无详单
                    dto.setStatus(EnumResultStatus.ERROR);
                    dto.setMsg("该月份无详单");
                    return dto;
                }else{
                    dto.setStatus(EnumResultStatus.ERROR);
                    dto.setMsg("运营商接口异常!请稍后再试!");
                    return dto;
                }
            } else {
                dto.setStatus(EnumResultStatus.ERROR);
                dto.setMsg("运营商接口异常!请稍后再试!");
                return dto;
            }

        }
        dto.setStatus(EnumResultStatus.SUCCESS);
        dto.setMsg("调用成功!");
        dto.setData(resList);
        return dto;
    }


    private ResultDto checkMsg(String mobile, String msg) {

        ResultDto dto = new ResultDto();
        //验证短信验证码
        JSONObject reData = JSONObject.parseObject(redisUtil.get(mobile).toString());
        String JSESSIONID = reData.getString("JSESSIONID");
        String pwd = reData.getString("pwd");
        String UID = reData.getString("UID");
        String cid = reData.getString("cid");
        String url = "https://clientaccess.10086.cn/biz-orange/LN/tempIdentCode/getTmpIdentCode";
        HttpPost postMethod = new HttpPost(url);
        String EncryptMobile = YD_RSA_Encrypt.getEntrypt("leadeon" + mobile + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
        String EncryptServicePassword = YD_RSA_Encrypt.getEntrypt("leadeon" + pwd + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
        String data = "{\"cid\":\"+" + cid + "+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+" + cid + "+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"0\",\"reqBody\":{\"businessCode\":\"01\",\"cellNum\":\"" + EncryptMobile + "\",\"passwd\":\"" + EncryptServicePassword + "\",\"smsPasswd\":\"" + msg + "\"},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"\"}";
        String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
        postMethod.setHeader("Cookie", cookie);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        postMethod.setEntity(myEntity);
        ResponseValue res = CommonHttpMethod.doPostSSL(postMethod);
        logger.info(res.getResponse());
        //{"retCode":"000000","retDesc":"SUCCESS"}
        JSONObject body = JSONObject.parseObject(res.getResponse());
        dto.setMsg(body.getString("retDesc"));
        if (body.getString("retCode").equals("000000")) {
            for (Cookie c : res.getCookies()) {
                if (c.getName().equals("JSESSIONID")) {
                    JSESSIONID = c.getValue();
                }
                if (c.getName().equals("UID")) {
                    UID = c.getValue();
                }
            }
            JSONObject JSON = (JSONObject) redisUtil.get(mobile);
            JSONObject newResData = new JSONObject();
            newResData.put("JSESSIONID", JSESSIONID);
            newResData.put("UID", UID);
            newResData.put("pwd", pwd);
            newResData.put("cid", cid);
            newResData.put("clientId",JSON.getInteger("clientId"));
            redisUtil.set(mobile, newResData,Long.valueOf(60*10));
            dto.setStatus(EnumResultStatus.SUCCESS);
            return dto;
        } else {
            dto.setStatus(EnumResultStatus.ERROR_MSG);
            return dto;
        }

    }


    private String reGetDetailData(String mobile, String month, int pageNo) {
        try {
            for (int i = 0; i < 20; i++) {
                String result = getDetailData(mobile, month, pageNo);
                if (StringUtils.isNotBlank(result)) {
                    return result;
                } else {
                    Thread.sleep(500);
                    logger.error("重试机制:" + i);
                }
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    private String getDetailData(String mobile, String month, int pageNo) {
        try {
            //验证短信验证码
            JSONObject reData = JSONObject.parseObject(redisUtil.get(mobile).toString());
            String JSESSIONID = reData.getString("JSESSIONID");
            String pwd = reData.getString("pwd");
            String UID = reData.getString("UID");
            String cid = reData.getString("cid");
            //获取通话明细
            String detailUrl = "https://clientaccess.10086.cn/biz-orange/BN/queryDetail/getDetail";
            logger.info("爬虫获取明细开始:" + detailUrl);
            HttpPost detailPostMethod = new HttpPost(detailUrl);
            String data = "{\"ak\":\"F4AA34B89513F0D087CA0EF11A3277469DC74905\",\"cid\":\"+" + cid + "+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+" + cid + "+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"0\",\"reqBody\":{\"billMonth\":\"" + month + "\",\"cellNum\":\"" + mobile + "\",\"page\":" + pageNo + ",\"tmemType\":\"02\",\"unit\":" + numEachPage + "},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"\"}";
            String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
            detailPostMethod.setHeader("Cookie", cookie);
            StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
            detailPostMethod.setEntity(myEntity);
            ResponseValue res = CommonHttpMethod.doPostSSL(detailPostMethod);
            logger.debug("爬虫获取明细结束:" + res.getResponse());
            String body = res.getResponse();
            JSONObject bodyJson = JSONObject.parseObject(body);
            if (bodyJson == null || bodyJson.getString("retCode").equals("400001")) {
                return null;
            }
            return body;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * 通用post方法
     *
     * @param postRequest
     * @return
     */
    private ResponseValue doPostSSLUID(HttpPost postRequest, String mobile) {
        ResponseValue response = new ResponseValue();
        postRequest.setHeader("Accept-Encoding", "gzip, deflate");
        postRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        CloseableHttpClient client = createSSLClientDefault();
        HttpClientContext context = HttpClientContext.create();
        try {
            HttpResponse httpResponse = client.execute(postRequest, context);
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code == 200 || code == 302) {
                // get response cookies
                CookieStore cookieStore = context.getCookieStore();
                List<Cookie> cookies = cookieStore.getCookies();

                Header header[] = httpResponse.getHeaders("Set-Cookie");
                String UID = "";
                for (int i = 0; i < header.length; i++) {
                    if (header[i].getValue().contains("UID")) {
                        UID = header[0].getValue().split("UID=")[1];
                        UID = UID.split(";")[0];
                    }
                }
                JSONObject reData = new JSONObject();
                reData.put("UID", UID);
                redisUtil.set(mobile, reData.toJSONString(), Long.valueOf(60 * 5));
                if (cookies != null) {
                    response.setCookies(cookies);
                }

                Header[] hs = httpResponse.getAllHeaders();
                for (Header h : hs) {
                    if (h.getName().equals("Location")) {
//						System.out.println(h.getValue());
                        response.setLocation(h.getValue());
                    }
                }

                HttpEntity httpEntity = httpResponse.getEntity();
                String result = EntityUtils.toString(httpEntity);
                // System.out.println(result);
                response.setResponse(result);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    private CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

}
