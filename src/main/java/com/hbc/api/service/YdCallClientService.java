package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.mapper.YdCallClientMapper;
import com.hbc.api.mapper.YdCallDetailClientMapper;
import com.hbc.api.model.MobileInfo;
import com.hbc.api.model.YdCallClient;
import com.hbc.api.model.YdCallDetailClient;
import com.hbc.api.util.*;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
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
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cheng on 16/9/8.
 */

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
    private MobileInfoService mobileInfoService;

    @Autowired
    private YdCallDetailClientMapper ydCallDetailClientMapper;

    @Autowired
    private MailUtil mailUtil;

//    private String httpProxyUrl = "http://tvp.daxiangdaili.com/ip/?tid=558054280595921&num=1&protocol=https&filter=on&category=2&delay=5&sortby=time";
//    private String httpProxyUrl = "http://dev.kuaidaili.com/api/getproxy/?orderid=938964833156023&num=1&b_pcchrome=1&b_pcie=1&b_pcff=1&b_android=1&b_iphone=1&protocol=2&method=2&an_an=1&an_ha=1&sp2=1&dedup=1&sep=1";
    private String httpProxyUrl = "http://www.xdaili.cn/ipagent/privateProxy/applyStaticProxy?count=1&spiderId=fdc2dbbfce564c03aa87e687318070eb&returnType=1";
    private static String cid = "M/xTDFNIp5LYrmsByvpx8kLnL85EZ4TBsurrbeao+m5bTacj2bfMrsApQlMklevyDtqf7KMqsQSDAS+Ah3Xs2sCDkuVhr90hPWvpPk19dDI=";
    private static String ctid = "M/xTDFNIp5LYrmsByvpx8kLnL85EZ4TBsurrbeao+m5bTacj2bfMrsApQlMklevyDtqf7KMqsQSDAS+Ah3Xs2sCDkuVhr90hPWvpPk19dDI=";
    private static String cv = "3.6.0";
    private static String sn = "H60-L01";
    private static String sp = "1080x1776";
    private static String st = "1";
    private static String sv = "4.4.2";
    private static String t = "90659cc7b540787a97937e3d4486f2d6";
    private static String xc = "A2100";
    private static String xk = "2e521becd88f08827f3ca40523b26ee2becd07159f40aaebc7d65a3b7a7f9886430496d6";
    private static String ak = "F4AA34B89513F0D087CA0EF11A3277469DC74905";
    private static String clientVersion = "3.6.0";

    private static Integer numEachPage = 200;
    private String UID = "";

    private Map<String, Double> costMap = Maps.newHashMap();

    /**
     * 入网时间
     */
    public Map<String,String> getInNetTime(String mobile) {
        Map<String,String> resultMap = Maps.newHashMap();
        String data = "{\"ak\":\"" + ak + "\",\"cid\":\"" + cid + "\",\"ctid\":\"" + ctid + "\",\"cv\":\"" + cv + "\",\"en\":\"0\",\"reqBody\":{\"cellNum\":\"" + mobile + "\"},\"sn\":\"" + sn + "\",\"sp\":\"" + sp + "\",\"st\":\"1\",\"sv\":\"" + sv + "\",\"t\":\"" + t + "\",\"xc\":\"" + xc + "\",\"xk\":\"" + xk + "\"}";
        String url = "https://clientaccess.10086.cn/biz-orange/BN/userInformationService/getUserInformation";
        HttpPost postMethod = new HttpPost(url);
        Map<String, String> map = (Map<String, String>) redisUtil.get(mobile);
        String JSESSIONID = map.get("JSESSIONID");
        String UID = (String) redisUtil.get(mobile + "_UID");
        String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
        postMethod.setHeader("Cookie", cookie);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        postMethod.setEntity(myEntity);
        ResponseValue res = doPostSSLUID(postMethod, data, mobile);
        logger.info(res.getResponse());
        JSONObject result = JSONObject.parseObject(res.getResponse().trim());
        if (result.getString("retCode").equals("000000")) {
            JSONObject userInfo = result.getJSONObject("rspBody").getJSONObject("userInfo");
            String beginTime = userInfo.getString("userBegin");
            String userName = userInfo.getString("userName");
            String address = userInfo.getString("userAdd");
            logger.info("手机号:" + mobile + ",入网时间" + beginTime);
            resultMap.put("beginTime",beginTime);
            resultMap.put("userName",userName);
            resultMap.put("address",address);
            return resultMap;
        } else {
            return null;
        }
    }

    /**
     * 月消费金额
     * @param mobile
     * @param startMonth
     * @param endMonth
     * @return
     */
    private boolean getAllCost(String mobile, String startMonth, String endMonth) {
        try {
            String url = "https://clientaccess.10086.cn/biz-orange/BN/historyBillsService/getHistoryBills";
            String cdata = "{\"ak\":\"" + ak + "\",\"cid\":\"" + cid + "\",\"ctid\":\"" + ctid + "\",\"cv\":\"" + cv + "\",\"en\":\"0\",\"reqBody\":{\"cellNum\":\"" + mobile + "\"},\"sn\":\"" + sn + "\",\"sp\":\"" + sp + "\",\"st\":\"1\",\"sv\":\"" + sv + "\",\"t\":\"" + t + "\",\"xc\":\"" + xc + "\",\"xk\":\"" + xk + "\"}";
            String data = "{\"ak\":\"" + ak + "\",\"cid\":\"" + cid + "\",\"ctid\":\"" + ctid + "\",\"cv\":\"" + cv + "\",\"en\":\"0\",\"reqBody\":{\"bgnMonth\":\"" + startMonth + "\",\"cellNum\":\"" + mobile + "\",\"endMonth\":\"" + endMonth + "\"},\"sn\":\"" + sn + "\",\"sp\":\"" + sp + "\",\"st\":\"1\",\"sv\":\"" + sv + "\",\"t\":\"" + t + "\",\"xc\":\"" + xc + "\",\"xk\":\"" + xk + "\"}";
            logger.info("data:" + data);
            HttpPost postMethod = new HttpPost(url);
            Map<String, String> map = (Map<String, String>) redisUtil.get(mobile);
            String JSESSIONID = map.get("JSESSIONID");
            String UID = (String) redisUtil.get(mobile + "_UID");
            String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
            postMethod.setHeader("Cookie", cookie);
            StringEntity myEntity = new StringEntity(cdata, ContentType.APPLICATION_JSON);
            postMethod.setEntity(myEntity);
            ResponseValue res = doPostSSLUID(postMethod, cdata, mobile);
            Thread.sleep(1000);
            logger.info(res.getResponse());
            JSONObject result = JSONObject.parseObject(res.getResponse().trim());
            if (result.getString("retCode").equals("000000")) {
                JSONArray array = result.getJSONObject("rspBody").getJSONArray("historyBillInfo");
                Double totalBill = array.getJSONObject(0).getDouble("totalBill");
                costMap.put(DateUtil.getCurrentDate(), totalBill);
            }

            myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
            postMethod.setEntity(myEntity);
            ResponseValue res2 = doPostSSLUID(postMethod, data, mobile);
            logger.info(res2.getResponse());
            result = JSONObject.parseObject(res2.getResponse().trim());
            if (result.getString("retCode").equals("000000")) {
                JSONArray array = result.getJSONObject("rspBody").getJSONArray("historyBillInfo");
                array.forEach(e -> {
                    JSONObject jsonObject = (JSONObject) e;
                    Double totalBill = jsonObject.getDouble("totalBill");
                    String billCycleDate = jsonObject.getString("billCycleStartDate").replace("-", "").substring(0, 6);
                    costMap.put(billCycleDate, totalBill);
                });
            }
        } catch (Exception e) {
            logger.error("获取月消费异常");
            logger.error(e.getMessage());
            return false;
        }
        return true;

    }

    @Async
    public boolean saveTimeLength(String mobile) {
        MobileInfo mobileInfo = mobileInfoService.getByMobile(mobile);
        if (mobileInfo == null) {
            Map<String,String> result = getInNetTime(mobile);
            if (result != null) {
                mobileInfoService.save(mobile, "yd", result.get("beginTime"),result.get("userName"),result.get("address"));
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
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
            boolean flag = getSendSMS(mobile, data);
            if (flag) {
                dto.setStatus(EnumResultStatus.SUCCESS);
                dto.setMsg(EnumResultStatus.SUCCESS.getName());
            } else {
                dto.setStatus(EnumResultStatus.ERROR);
                dto.setMsg("短信发送失败!请重试!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dto.setStatus(EnumResultStatus.ERROR);
            dto.setMsg("短信发送失败!请重试!");
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
    public ResultDto synchroData(String mobile, String msg, String pwd, Integer clientId) throws InterruptedException {
        ResultDto dto = identify(mobile, pwd, msg);
        if (!dto.getStatus().equals(EnumResultStatus.SUCCESS)) {
            return dto;
        }
        try {
            saveTimeLength(mobile);
        } catch (Exception e) {
            logger.error("获取在网时长失败!");
            logger.error(e.getMessage());
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String end = DateUtil.sdfYYYY_MM.format(cal.getTime());
        cal.add(Calendar.MONTH, -4);
        String begin = DateUtil.sdfYYYY_MM.format(cal.getTime());
        getAllCost(mobile, begin, end);

        List<YdCallClient> entityList = ydCallClientMapper.getListByMobile(mobile);
        List<String> dateList = DateUtil.getPreSixMonth();
        if (entityList == null || entityList.size() == 0) {
            for (String queryDate : dateList) {
                dto = saveBySpider(mobile, pwd, queryDate.substring(0, 4).concat("-").concat(queryDate.substring(4, 6)), clientId);
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
            }
        }
        dto.setStatus(EnumResultStatus.SUCCESS);
        dto.setMsg(EnumResultStatus.SUCCESS.getName());
        httpClientUtil.sendDataToKafka(mobile);
        return dto;
    }

    @Transactional
    private ResultDto saveBySpider(String mobile, String pwd, String month, Integer clientId) throws InterruptedException {
        ResultDto dto = new ResultDto();
        dto = jsonToList(mobile, month);
        if (dto.getStatus().equals(EnumResultStatus.SUCCESS)) {
            if(dto.getData() != null) {
                List<YdCallDetailClient> detailResult = (List<YdCallDetailClient>) dto.getData();
                if (detailResult != null && detailResult.size() > 0) {
                    YdCallClient ydCallClient = new YdCallClient();
                    ydCallClient.setCallDate(month.replace("-", ""));
                    ydCallClient.setMobile(mobile);
                    ydCallClient.setPwd(Md5Crypt.md5Crypt(pwd.getBytes()));
                    ydCallClient.setClientId(clientId);
                    ydCallClient.setCost(costMap.get(month.replace("-", "")));
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
                    dto.setStatus(EnumResultStatus.SUCCESS);
                    dto.setMsg(month + "月份无详单数据!");
                    logger.info(mobile + "该手机号," + month + "月份无详单数据!");
                    return dto;
                }
            }else{
                dto.setStatus(EnumResultStatus.SUCCESS);
                dto.setMsg("该月无详单;");
                return dto;
            }
        } else {
            logger.error("获取明细失败!请检查验证码是否过期");
            dto.setStatus(EnumResultStatus.ERROR);
            dto.setMsg("获取明细失败!");
            return dto;
        }
    }

    private ResultDto jsonToList(String mobile, String month) throws InterruptedException {
        ResultDto dto = new ResultDto();
        List<YdCallDetailClient> resList = new ArrayList<>();
        String year = month.substring(0, 4);
        String result = getDetailData(mobile, month, 1);
        logger.info("爬虫明细:" + month + "月,第" + 1 + "页" + result);
        JSONArray array = new JSONArray();
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.getString("retCode").equals("000000")) {
                JSONObject rspBody = jsonObject.getJSONObject("rspBody");
                JSONArray callList = rspBody.getJSONArray("callList");
                Integer totalCount = rspBody.getInteger("totalCount");
                if (totalCount == 0) {
                    dto.setStatus(EnumResultStatus.SUCCESS);
                    dto.setMsg("该月无详单数据");
                    return dto;
                }
                Integer totalPage = totalCount / numEachPage;
                Integer yu = totalCount / numEachPage;
                if (totalPage == 0) {
                    array.addAll(callList);
                } else {
                    if (yu > 0) {
                        totalPage = totalPage + 1;
                    }
                }
                if (totalPage >= 2) {
                    for (int i = 2; i <= totalPage; i++) {
                        result = getDetailData(mobile, month, i);
                        logger.info("爬虫明细:" + month + "月,第" + i + "页" + result);
                        jsonObject = JSONObject.parseObject(result);
                        if (jsonObject !=null &&jsonObject.getString("retCode").equals("000000")) {
                            rspBody = jsonObject.getJSONObject("rspBody");
                            callList = rspBody.getJSONArray("callList");
                            array.addAll(callList);
                        }
                    }
                }
                array.forEach(e -> {
                    JSONObject data = JSONObject.parseObject(e.toString());
                    JSONObject tmemRecord = data.getJSONObject("tmemRecord");
                    YdCallDetailClient ydCallDetailClient = new YdCallDetailClient();
                    if (tmemRecord.getString("startTime").length() == 14) {
                        ydCallDetailClient.setStartTime(year + "-" + tmemRecord.getString("startTime"));
                    } else {
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
                dto.setStatus(EnumResultStatus.SUCCESS);
                dto.setData(resList);
                dto.setMsg(EnumResultStatus.SUCCESS.getName());
                return dto;
            } else if ("203100".equals(jsonObject.getString("retCode"))) {//该月份无详单
                dto.setStatus(EnumResultStatus.SUCCESS);
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
        }
        return dto;
    }

    public String getDetailData(String mobile, String month, int pageNo) {
        //查详单
        Map<String, String> map = (Map<String, String>) redisUtil.get(mobile);
        String JSESSIONID = map.get("JSESSIONID");
        String UID = redisUtil.get(mobile + "_UID").toString();
        String data = "{\"ak\":\"" + ak + "\",\"cid\":\"" + cid + "\",\"ctid\":\"" + cid + "\",\"cv\":\"" + clientVersion + "\",\"en\":\"0\",\"reqBody\":{\"billMonth\":\"" + month + "\",\"cellNum\":\"" + mobile + "\",\"page\":" + pageNo + ",\"tmemType\":\"02\",\"unit\":" + numEachPage + "},\"sn\":\"EVA-AL10\",\"sp\":\"1080x1812\",\"st\":\"1\",\"sv\":\"6.0\",\"t\":\"cca72cde35f0bcb5f1d1c3119b3eece0\",\"xc\":\"A0001\",\"xk\":\"" + xk + "\"}";
        String url = "https://clientaccess.10086.cn/biz-orange/BN/queryDetail/getDetail";
        HttpPost postMethod = new HttpPost(url);
        String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
        postMethod.setHeader("Cookie", cookie);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        postMethod.setEntity(myEntity);
        ResponseValue res = doPostSSLUID(postMethod, data, mobile);
        String body = res.getResponse();
        return body;
    }


    /**
     * 发送短信验证码
     */
    public Boolean getSendSMS(String mobile, String data) {
        try {
            String url = "https://clientaccess.10086.cn/biz-orange/LN/uamrandcode/sendMsgLogin";
            HttpPost postMethod = new HttpPost(url);
            StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);//
            postMethod.setEntity(myEntity);
            ResponseValue res = doPostSSLUID(postMethod, data, mobile);
            for (Cookie c : res.getCookies()) {
                if (c.getName().equals("JSESSIONID")) {
                    String JSESSIONID = c.getValue();
                    logger.info("JSESSIONID:" + JSESSIONID);
                }
                if (c.getName().equals("UID")) {
                    String UID = c.getValue();
                    logger.info("UID:" + UID);
                }
            }
            return true;
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
            ResponseValue res = doPostSSLUID(postMethod, data, mobile);
            logger.info("请求参数:mobile:" + mobile + ",pwd:" + pwd + ",smsCode:" + smsCode);
            String body = res.getResponse();

            if(StringUtils.isBlank(body)){
                if(redisUtil.exists(mobile+"_count")){
                    int count = (int) redisUtil.get(mobile+"_count");
                    count +=1;
                    if(count == 10){
                        mailUtil.sendIpEmail();
                    }else{
                        redisUtil.set(mobile+"_count",count,Long.valueOf(60*10));
                    }
                }else{
                    redisUtil.set(mobile+"_count",1,Long.valueOf(60*10));
                }
            }else{
                redisUtil.remove(mobile+"_count");
            }

            logger.info("移动身份验证返回:" + body);
            if(StringUtils.isNotBlank(body)){
                JSONObject object = JSON.parseObject(body);
                if (!object.getString("retCode").equals("000000")) {
                    dto.setStatus(EnumResultStatus.ERROR);
                    dto.setMsg(object.getString("retDesc"));
                    logger.info("身份验证失败!");
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
            }else{
                dto.setStatus(EnumResultStatus.ERROR);
                dto.setMsg("移动运营商身份核验失败!请重新");
            }
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
    public ResponseValue doPostSSLUID(HttpPost postRequest, String data, String mobile) {
        ResponseValue response = new ResponseValue();
        postRequest.setHeader("Accept-Encoding", "gzip, deflate");
        postRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        String encrypt = postRequest.getURI() + "_" + data + "_Leadeon/SecurityOrganization";
        postRequest.setHeader("xs", string2MD5(encrypt));
        CloseableHttpClient client = createSSLClientDefault();
        HttpClientContext context = HttpClientContext.create();
//        String iptxt= getProxyIp().split("\\r\\n")[0];
//        logger.info(iptxt);
//        if(StringUtils.isNotBlank(iptxt)){
//            // 依次是代理地址，代理端口号，协议类型
//            String[] ipp = iptxt.split(":");
//            String ip = ipp[0];
//            String prot = ipp[1];
//            HttpHost proxy = new HttpHost(ip,Integer.parseInt(prot),"http");
//            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//            postRequest.setConfig(config);
//        }
        try {
            HttpResponse httpResponse = client.execute(postRequest, context);
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code == 200 || code == 302) {
                // get response cookies
                CookieStore cookieStore = context.getCookieStore();
                List<Cookie> cookies = cookieStore.getCookies();
                Header header[] = httpResponse.getHeaders("Set-Cookie");
                for (int i = 0; i < header.length; i++) {
                    if (header[i].getValue().contains("UID")) {
                        UID = header[0].getValue().split("UID=")[1];
                        UID = UID.split(";")[0];
                        logger.info("UID:" + UID);
                    }
                }

                if (cookies != null) {
                    response.setCookies(cookies);
                }

                Header[] hs = httpResponse.getAllHeaders();
                for (Header h : hs) {
                    if (h.getName().equals("Location")) {
                        response.setLocation(h.getValue());
                    }
                }

                HttpEntity httpEntity = httpResponse.getEntity();
                String result = EntityUtils.toString(httpEntity);
                response.setResponse(result);
                String obj = (String) redisUtil.get(mobile + "_UID");
                if (StringUtils.isBlank(obj)) {
                    redisUtil.set(mobile + "_UID", UID, Long.valueOf(60 * 5));
                }

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


    private String getProxyIp(){
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Connection con = null;
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

