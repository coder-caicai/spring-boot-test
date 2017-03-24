package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 16/9/8.
 */

//@Transactional
@Service
public class YdCallClientService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private YdCallClientMapper ydCallClientMapper;

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Autowired
    private YdCallDetailClientMapper ydCallDetailClientMapper;


    private static String cid = "+C5zXwvbuUoKb0v7AhOkXRX+qPlj+26JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb";
    //	private static String cid = "uOwckxa/tK0uIjYmgHF9mo5D4R+pWBWDJ0QMPutDUkQJq64pewRTNW2i2TCTpDGoHX4ye1v9/eqz/QJqVhCTMERAhe38vOvbh77ChDh/eZd1NG0c8YzbUadnj4ix0tta";
    private static String clientVersion = "3.5.1";
    private static String xk = "3e7eead938ee438c19236e1b81f1519318534377a910a1e2d31bc263c0370c40e483949f";
    private static String ak = "F4AA34B89513F0D087CA0EF11A3277469DC74905";

    //871008
    private static Integer numEachPage = 200;



    /**
     * 入网时间
     */
    public String getInNetTime(String mobile) {
        String data = "{\"ak\":\"" + ak + "\",\"cid\":\"" + cid + "\",\"ctid\":\"" + cid + "\",\"cv\":\"" + clientVersion + "\",\"en\":\"0\",\"reqBody\":{\"cellNum\":\"" + mobile + "\"},\"sn\":\"EVA-AL10\",\"sp\":\"1080x1812\",\"st\":\"1\",\"sv\":\"6.0\",\"t\":\"\",\"xc\":\"A0001\",\"xk\":\"" + xk + "\"}";
        String url = "https://clientaccess.10086.cn/biz-orange/BN/userInformationService/getUserInformation";
        HttpPost postMethod = new HttpPost(url);
        Map<String, String> map = (Map<String, String>) redisUtil.get(mobile);
        String JSESSIONID = map.get("JSESSIONID");
        String UID = map.get("UID");
        String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
        postMethod.setHeader("Cookie", cookie);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        postMethod.setEntity(myEntity);
        ResponseValue res = doPostSSLUID(postMethod, data);
        String beginTime = (String) JSONObject.parseObject(res.getResponse().trim())
                .getJSONObject("rspBody").getJSONObject("userInfo").get("userBegin");
        logger.info("手机号:"+mobile+",入网时间" + beginTime);
        return beginTime;
    }



    /**
     * 发送短信
     *
     * @param mobile
     * @return
     */
    public ResultDto sendMsg(String mobile) {
        ResultDto dto = new ResultDto();
        try {
            String data = "{\"ak\":\"" + ak + "\",\"cid\":\"" + cid + "\",\"ctid\":\"" + cid + "\",\"cv\":\"" + clientVersion + "\",\"en\":\"0\",\"reqBody\":{\"cellNum\":\"" + mobile + "\"},\"sn\":\"EVA-AL10\",\"sp\":\"1080x1812\",\"st\":\"1\",\"sv\":\"6.0\",\"t\":\"\",\"xc\":\"A0001\",\"xk\":\"" + xk + "\"}";
            boolean flag = getSendSMS(data);
            if (flag) {
                dto.setStatus(EnumResultStatus.SUCCESS);
                dto.setMsg(EnumResultStatus.SUCCESS.getName());
            } else {
                dto.setStatus(EnumResultStatus.ERROR);
                dto.setMsg("短信发送失败!请稍后重试!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dto.setStatus(EnumResultStatus.ERROR);
            dto.setMsg("短信发送失败!请稍后重试!");
        }
        return dto;

    }

    /**
     * 同步数据
     *
     * @param mobile
     * @param msg
     * @return
     */
    public ResultDto synchroData(String mobile, String msg, String pwd, Integer clientId) {
        ResultDto dto = identify(mobile, pwd, msg);
        if (!dto.getStatus().equals(EnumResultStatus.SUCCESS)) {
            return dto;
        }
        List<YdCallClient> entityList = ydCallClientMapper.getListByMobile(mobile);

        List<String> dateList = DateUtil.getPreSixMonth();
        if (entityList == null || entityList.size() == 0) {
            for (String queryDate : dateList) {
                dto = saveBySpider(mobile, pwd, queryDate.substring(0, 4).concat("-").concat(queryDate.substring(4, 6)), clientId);
                if (!dto.getStatus().equals(EnumResultStatus.SUCCESS)) {
                    return dto;
                }
            }
        } else {
            entityList.sort((x, y) -> Integer.valueOf(x.getCallDate()).compareTo(Integer.valueOf(y.getCallDate())));
            YdCallClient lastModel = entityList.get(entityList.size() - 1);
            ydCallClientMapper.delete(lastModel);
            entityList.remove(entityList.size() - 1);

            for (YdCallClient ydCallClient : entityList) {
                if (dateList.indexOf(ydCallClient.getCallDate()) > -1) {
                    dateList.remove(ydCallClient.getCallDate());
                }
            }
            for (String queryDate : dateList) {
                dto = saveBySpider(mobile, pwd, queryDate.substring(0, 4).concat("-").concat(queryDate.substring(4, 6)), clientId);
                if (!dto.getStatus().equals(EnumResultStatus.SUCCESS)) {
                    return dto;
                }
            }
        }
        return dto;
    }


    @Transactional
    private ResultDto saveBySpider(String mobile, String pwd, String month, Integer clientId) {
        ResultDto dto = new ResultDto();
        dto = jsonToList(mobile, month);
        if (dto.getStatus().equals(EnumResultStatus.SUCCESS)) {
            List<YdCallDetailClient> detailResult = (List<YdCallDetailClient>) dto.getData();
            YdCallClient ydCallClient = new YdCallClient();
            ydCallClient.setCallDate(month.replace("-", ""));
            ydCallClient.setMobile(mobile);
            ydCallClient.setPwd(Md5Crypt.md5Crypt(pwd.getBytes()));
            ydCallClient.setClientId(clientId);
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

            httpClientUtil.sendDataToKafka(mobile);
            return dto;
        } else {
            logger.error("获取明细失败!请检查验证码是否过期");
            return dto;
        }
    }
    private ResultDto jsonToList(String mobile, String month) {
        ResultDto dto = new ResultDto();
        List<YdCallDetailClient> resList = new ArrayList<>();
        String year = month.substring(0,4);
        for (int i = 1; i < 20; i++) {
            String result = getDetailData(mobile, month, i);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if(StringUtils.isBlank(result)){
                dto.setStatus(EnumResultStatus.ERROR_PWD);
                dto.setMsg(EnumResultStatus.ERROR_PWD.getName());
                return dto;
            }
            if(jsonObject.getString("retCode").equals("000000")){
                logger.info("爬虫明细:" + result);
                if (StringUtils.isNotBlank(result)) {
                    if (jsonObject.getString("retCode").equals("000000")) {
                        JSONObject rspBody = jsonObject.getJSONObject("rspBody");
                        JSONArray callList = rspBody.getJSONArray("callList");
                        if (callList == null) {
                            break;
                        }
                        callList.forEach(e -> {
                            JSONObject data = JSONObject.parseObject(e.toString());
                            JSONObject tmemRecord = data.getJSONObject("tmemRecord");
                            YdCallDetailClient ydCallDetailClient = new YdCallDetailClient();
                            if(tmemRecord.getString("startTime").length() == 14){
                                ydCallDetailClient.setStartTime(year+"-"+tmemRecord.getString("startTime"));
                            }else{
                                ydCallDetailClient.setStartTime(tmemRecord.getString("startTime"));
                            }
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
                        if (callList.size() < numEachPage) {
                            break;
                        }

                    } else if ("203100".equals(jsonObject.getString("retCode"))) {//该月份无详单
                        dto.setStatus(EnumResultStatus.ERROR);
                        dto.setMsg("该月份无详单");
                        return dto;
                    } else {
                        dto.setStatus(EnumResultStatus.ERROR);
                        dto.setMsg("运营商接口异常!请稍后再试!");
                        return dto;
                    }
                } else {
                    dto.setStatus(EnumResultStatus.ERROR);
                    dto.setMsg("运营商接口异常!请稍后再试!");
                    return dto;
                }
            }else{
                //{"retCode":"410000","retDesc":"尊敬的用户，您已超过30分钟未进行任何操作，为保证账号安全，建议您重新登录","rspBody":null}
                dto.setStatus(EnumResultStatus.ERROR);
                dto.setMsg(jsonObject.getString("retDesc"));
                return dto;
            }
        }
        dto.setStatus(EnumResultStatus.SUCCESS);
        dto.setMsg("调用成功!");
        dto.setData(resList);
        return dto;
    }

    public String getDetailData(String mobile, String month, int pageNo) {
        //查详单
        Map<String, String> map = (Map<String, String>) redisUtil.get(mobile);
        String JSESSIONID = map.get("JSESSIONID");
        String UID = map.get("UID");
        String data = "{\"ak\":\"" + ak + "\",\"cid\":\"" + cid + "\",\"ctid\":\"" + cid + "\",\"cv\":\"" + clientVersion + "\",\"en\":\"0\",\"reqBody\":{\"billMonth\":\"" + month + "\",\"cellNum\":\"" + mobile + "\",\"page\":" + pageNo + ",\"tmemType\":\"02\",\"unit\":" + numEachPage + "},\"sn\":\"EVA-AL10\",\"sp\":\"1080x1812\",\"st\":\"1\",\"sv\":\"6.0\",\"t\":\"cca72cde35f0bcb5f1d1c3119b3eece0\",\"xc\":\"A0001\",\"xk\":\"" + xk + "\"}";
        String url = "https://clientaccess.10086.cn/biz-orange/BN/queryDetail/getDetail";
        HttpPost postMethod = new HttpPost(url);
        String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
        postMethod.setHeader("Cookie", cookie);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        postMethod.setEntity(myEntity);
        ResponseValue res = doPostSSLUID(postMethod, data);
        logger.info("详单明细:" + res.getResponse());
        String body = res.getResponse();
//        if (bodyJson == null || !bodyJson.getString("retCode").equals("000000")) {
//            logger.error(body);
//            return null;
//        }
        return body;
    }


    /**
     * 发送短信验证码
     */
    public Boolean getSendSMS(String data) {
        try {
            String url = "https://clientaccess.10086.cn/biz-orange/LN/uamrandcode/sendMsgLogin";
            HttpPost postMethod = new HttpPost(url);
            StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);//
            postMethod.setEntity(myEntity);
            ResponseValue res = doPostSSLUID(postMethod, data);
            JSONObject resJson = JSON.parseObject(res.getResponse());
            logger.info(res.getResponse());
            if(!resJson.getString("retCode").equals("000000")){
                return false;
            }else{
                return true;
            }
//            for (Cookie c : res.getCookies()) {
//                if (c.getName().equals("JSESSIONID")) {
//                    String JSESSIONID = c.getValue();
//                    logger.info("JSESSIONID:"+JSESSIONID);
//                }
//                if (c.getName().equals("UID")) {
//                    String UID = c.getValue();
//                    logger.info("UID:"+UID);
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 二次身份认证
     */
    public ResultDto identify(String mobile, String pwd, String smsCode) {
        ResultDto dto = new ResultDto();
        try {
            String encryptMobile = YD_RSA_Encrypt.getEntrypt("leadeon" + mobile + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
            String encryptServicePassword = YD_RSA_Encrypt.getEntrypt("leadeon" + pwd + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));

            Map<String, String> map = Maps.newHashMap();
            String data = "{\"ak\":\"" + ak + "\",\"cid\":\"" + cid + "\",\"ctid\":\"" + cid + "\",\"cv\":\"" + clientVersion + "\",\"en\":\"0\",\"reqBody\":{\"businessCode\":\"01\",\"cellNum\":\"" + encryptMobile + "\",\"passwd\":\"" + encryptServicePassword + "\",\"smsPasswd\":\"" + smsCode + "\"},\"sn\":\"EVA-AL10\",\"sp\":\"1080x1812\",\"st\":\"1\",\"sv\":\"6.0\",\"t\":\"cca72cde35f0bcb5f1d1c3119b3eece0\",\"xc\":\"A0001\",\"xk\":\"" + xk + "\"}";
            String JSESSIONID = "";
            String UID = "";

            String url = "https://clientaccess.10086.cn/biz-orange/LN/tempIdentCode/getTmpIdentCode";
            HttpPost postMethod = new HttpPost(url);
            String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
            postMethod.setHeader("Cookie", cookie);
            StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
            postMethod.setEntity(myEntity);
            ResponseValue res = doPostSSLUID(postMethod, data);
            JSONObject resJson = JSON.parseObject(res.getResponse());
            if(!resJson.getString("retCode").equals("000000")){
                dto.setStatus(EnumResultStatus.ERROR);
                dto.setMsg(resJson.getString("retDesc"));
                return dto;
            }
            for (Cookie c : res.getCookies()) {
                if (c.getName().equals("JSESSIONID")) {
                    JSESSIONID = c.getValue();
                    map.put("JSESSIONID", JSESSIONID);
                }
//                if (c.getName().equals("UID")) {
//                    UID = c.getValue();
//                    map.put("UID",UID);
//                }
            }
            dto.setStatus(EnumResultStatus.SUCCESS);
            dto.setMsg("身份验证成功");
            logger.info("身份验证成功!");
            redisUtil.set(mobile, map, Long.valueOf(60 * 5));
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            dto.setStatus(EnumResultStatus.ERROR);
            dto.setMsg("身份验证失败");
            return dto;
        }
    }



    /**
     * 通用post方法
     *
     * @param postRequest
     * @return
     */
    public ResponseValue doPostSSLUID(HttpPost postRequest, String data) {

        ResponseValue response = new ResponseValue();

        postRequest.setHeader("Accept-Encoding", "gzip, deflate");
        postRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        String encrypt = postRequest.getURI() + "_" + data + "_Leadeon/SecurityOrganization";
//        System.out.println(encrypt);
        postRequest.setHeader("xs", string2MD5(encrypt));
        CloseableHttpClient client = createSSLClientDefault();


        HttpClientContext context = HttpClientContext.create();
        try {
            HttpResponse httpResponse = client.execute(postRequest, context);
//            Map<String,String> map = (Map<String, String>) redisUtil.get(mobile);
//            String UID = map.get("UID");
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code == 200 || code == 302) {
                // get response cookies
                CookieStore cookieStore = context.getCookieStore();
                List<Cookie> cookies = cookieStore.getCookies();
//                Header header[] = httpResponse.getHeaders("Set-Cookie");
//                for (int i = 0; i < header.length; i++) {
//                    if (header[i].getValue().contains("UID")) {
//                        UID = header[0].getValue().split("UID=")[1];
//                        UID = UID.split(";")[0];
//                    }
//                }

                if (cookies != null) {
                    response.setCookies(cookies);
                }

                Header[] hs = httpResponse.getAllHeaders();
                for (Header h : hs) {
                    if (h.getName().equals("Location")) {
//                        System.out.println(h.getValue());
                        response.setLocation(h.getValue());
                    }
                }

                HttpEntity httpEntity = httpResponse.getEntity();
                String result = EntityUtils.toString(httpEntity);
                //System.out.println(result);
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

    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
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

    /***
     * MD5加码 生成32位md5码
     */
    public String string2MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


}

