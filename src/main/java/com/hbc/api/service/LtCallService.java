package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.mapper.LtCallDetailMapper;
import com.hbc.api.mapper.LtCallMapper;
import com.hbc.api.model.LtCall;
import com.hbc.api.model.LtCallDetail;
import com.hbc.api.model.MobileInfo;
import com.hbc.api.util.DateUtil;
import com.hbc.api.util.FileUtils;
import com.hbc.api.util.HttpClientUtil;
import com.hbc.api.util.RedisUtil;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 16/7/25.
 */
@Service
public class LtCallService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private LtCallMapper ltCallMapper;

    @Autowired
    private LtCallDetailMapper ltCallDetailMapper;

    @Autowired
    private HttpClientUtil httpClientUtil;

    @Autowired
    private MobileInfoService mobileInfoService;

    @Autowired
    private RedisUtil redisUtil;


    private Map<String, String> cookieMap = new HashMap<>();



    /**
     * @throws IOException
     */
    public ResultDto login(String mobile, String pwd, Integer clientId,String path,String validateCode) throws IOException, ParseException {
        ResultDto loginResult;
        if(StringUtils.isNotBlank(validateCode)){
            loginResult = msgConfirm(mobile,pwd,validateCode);
        }else{
            loginResult  = preSpider(mobile, pwd,path);
        }
        if (!loginResult.getStatus().equals(EnumResultStatus.SUCCESS)) {
            return loginResult;
        }

        try {
            saveTimeLength(mobile);
        }catch (Exception e){
            logger.error("获取在网时长失败!");
            logger.error(e.getMessage());
        }
        redisUtil.set(mobile+"_pwd",pwd,Long.valueOf(60*5));
        redisUtil.set(mobile+"_clientId",clientId,Long.valueOf(60*5));
        redisUtil.set(mobile+"_isLogin",true,Long.valueOf(60*5));
        return sendMsg(mobile);
    }


    public ResultDto synchroData (String mobile){
        ResultDto dto = new ResultDto();
        String pwd =(String) redisUtil.get(mobile+"_pwd");
        Integer clientId = (Integer) redisUtil.get(mobile+"_clientId");
//      先检查数据库中是否已经存在该用户数据
        List<LtCall> entityList = ltCallMapper.getListByMobile(mobile);
        List<String> dateList = DateUtil.getPreSixMonth();
        if (entityList == null || entityList.size() == 0) {
            for (String queryDate : dateList) {
                String startDate = DateUtil.dateConvert(DateUtil.getFirstDayInMonth(queryDate));
                String endDate = DateUtil.dateConvert(DateUtil.getEndDayInMonth(queryDate));
                boolean flag = saveBySpider(mobile, pwd, startDate, endDate, clientId);
                if (!flag) {
                    dto.setStatus(EnumResultStatus.ERROR);
                    dto.setMsg(startDate + "-" + endDate + "期间的详单出现异常请稍后再试");
                    return dto;
                }
            }
        } else {
            entityList.sort((x,y) -> Integer.valueOf(y.getCallDate()).compareTo(Integer.valueOf(x.getCallDate())));
            LtCall lastModel = entityList.get(entityList.size()-1);
            ltCallMapper.delete(lastModel);
            entityList.remove(entityList.size()-1);

            for (LtCall ltCall : entityList) {
                if (dateList.indexOf(ltCall.getCallDate()) > -1) {
                    dateList.remove(ltCall.getCallDate());
                }
            }
            for (String queryDate : dateList) {
                String startDate = DateUtil.dateConvert(DateUtil.getFirstDayInMonth(queryDate));
                String endDate = DateUtil.dateConvert(DateUtil.getEndDayInMonth(queryDate));
                boolean flag = saveBySpider(mobile, pwd, startDate, endDate, clientId);
                if (!flag) {
                    dto.setStatus(EnumResultStatus.ERROR);
                    dto.setMsg(startDate + "-" + endDate + "期间的详单出现异常请稍后再试");
                    return dto;
                }
            }
        }
        dto.setStatus(EnumResultStatus.SUCCESS);
        dto.setMsg("服务调用成功");
        httpClientUtil.sendDataToKafka(mobile);
        return dto;
    }

    @Async
    private boolean saveBySpider(String mobile, String pwd, String startDate, String endDate, Integer clientId) {
        try {
            String result = spider(startDate, endDate);
            logger.info(result);
            if (result != null) {
                List<LtCallDetail> list = jsonToEntity(result);
                if (list != null && list.size() > 0) {
                    LtCall ltCall = new LtCall();
                    ltCall.setMobile(mobile);
                    ltCall.setCallDate(startDate.substring(0, 7).replace("-", ""));
                    ltCall.setPwd(Md5Crypt.md5Crypt(pwd.getBytes()));
                    ltCall.setClientId(clientId);
                    if(DateUtil.getCurrentDate().equals(startDate.replace("-","").substring(0,6))){
                        ltCall.setCost(null);
                    }else{
                        ltCall.setCost(getAllCost(startDate.replace("-","").substring(0,6)));
                    }
                    ltCallMapper.insert(ltCall);
                    for (LtCallDetail ltCallDetail : list) {
                        ltCallDetail.setCall_id(ltCall.getId());
                    }
                    ltCallDetailMapper.insertList(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }


    private List<LtCallDetail> jsonToEntity(String json) {
        List<LtCallDetail> resultList = new ArrayList<>();
        Map<String, Map<String, List<Map<String, String>>>> map = (Map<String, Map<String, List<Map<String, String>>>>) JSON.parse(json);
        if (map != null) {
//            alltotalfee
            if (map.get("pageMap") != null) {
                if (map.get("pageMap").get("result") != null) {
                    List<Map<String, String>> list = map.get("pageMap").get("result");
                    for (Map<String, String> resultMap : list) {
                        LtCallDetail ltCallDetail = new LtCallDetail();
                        ltCallDetail.setCalldate(resultMap.get("calldate"));
                        ltCallDetail.setCalllonghour(resultMap.get("calllonghour"));
                        ltCallDetail.setCalltime(resultMap.get("calltime"));
                        ltCallDetail.setCalltype(resultMap.get("calltype"));
                        ltCallDetail.setHomearea(resultMap.get("homearea"));
                        ltCallDetail.setLandtype(resultMap.get("landtype"));
                        ltCallDetail.setOthernum(resultMap.get("othernum"));
                        ltCallDetail.setTotalfee(Double.valueOf(resultMap.get("totalfee")));
                        resultList.add(ltCallDetail);
                    }
                    return resultList;
                }
            }
        }
        return null;
    }

    private Double getAllCost(String date)  {
        try {
            Connection con = null;
            String url = "http://iservice.10010.com/e3/static/query/queryHistoryBill?_=" + System.currentTimeMillis() +
                    "&accessURL=http://iservice.10010.com/e4/skip.html?menuCode=000100020001&menuCode=000100020001&&menuId=000100020001&menuid=000100020001";
            con = Jsoup.connect(url);
            con.header("APIAuthorize-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
            con.referrer("http://iservice.10010.com/e4/query/basic/history_list.html?menuId=000100020001");
            Map<String, String> data = Maps.newHashMap();
            data.put("querytype", "0001");
            data.put("querycode", "0001");
            data.put("billdate", date);
            data.put("flag", "2");
            Connection.Response response = con.timeout(30000)
                    .method(Connection.Method.POST)
                    .data(data)
                    .cookies(cookieMap)
                    .ignoreContentType(true)
                    .followRedirects(true)
                    .execute();
            logger.info("response:" + response.body());
            JSONObject result = JSON.parseObject(response.body());
            if (result != null) {
                if (result.getBoolean("issuccess")) {
                    return result.getJSONObject("result").getDouble("allfee");
                }
            }
        }catch (Exception e){
            logger.error("获取月消费金额失败!");
            logger.error(e.getMessage());
        }
        return null;
    }





    private ResultDto preSpider(String mobile, String pwd,String path) throws IOException {
        ResultDto dto = new ResultDto();
        Connection con = null;
        String indexUrl = "https://uac.10010.com/portal/Service/CheckNeedVerify?"
                + "callback=jQuery17206171322869938429_1462934682905&userName=" + mobile + "&pwdType=01&_="
                + System.currentTimeMillis();
        con = Jsoup.connect(indexUrl);
        con.header("APIAuthorize-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
        con.referrer("https://uac.10010.com/portal/homeLogin");
        Connection.Response indexResponse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                .execute();
        logger.info("indexResponse:" + indexResponse.body());
        cookieMap.putAll(indexResponse.cookies());
        String loginUrl = "";
        loginUrl = "https://uac.10010.com/portal/Service/MallLogin?"
                + "callback=jQuery17206171322869938429_1462934682906&req_time=" + System.currentTimeMillis()
                + "&redirectURL=http%3A%2F%2Fwww.10010.com&userName=" + mobile + "&password=" + pwd + "&pwdType=01"
                + "&productType=01&redirectType=01&rememberMe=1&_=" + (System.currentTimeMillis() + 3);
        con = Jsoup.connect(loginUrl);
        con.referrer("https://uac.10010.com/portal/homeLogin");
        Connection.Response loginResponse = con.timeout(30000).
                method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                .execute();
        logger.info("联通账号登录结果:" + loginResponse.body());
        String msg = loginResponse.body();
        msg = msg.substring(msg.indexOf("(") + 1, msg.lastIndexOf(")"));
        JSONObject msgJson = JSONObject.parseObject(msg);
        dto.setMsg(msgJson.getString("msg"));
        if (loginResponse.body().contains("resultCode:\"0000\"")) {
            cookieMap.putAll(loginResponse.cookies());
            String checkUrl = "http://iservice.10010.com/e3/static/check/checklogin/?_=" + System.currentTimeMillis();
            con = Jsoup.connect(checkUrl);
            con.referrer("http://iservice.10010.com/e3/query/call_dan.html?menuId=000100030001");
            con.header("Host", "iservice.10010.com");
            con.header("x-requested-with", "XMLHttpRequest");
            Connection.Response checkResponse = con.timeout(30000).method(Connection.Method.POST).cookies(cookieMap).ignoreContentType(true).followRedirects(true)
                    .execute();
            cookieMap.putAll(checkResponse.cookies());
            dto.setStatus(EnumResultStatus.SUCCESS);
        } else if (loginResponse.body().contains("resultCode:\"7009\"")) {
            dto.setStatus(EnumResultStatus.SUCCESS);
        } else if (loginResponse.body().contains("resultCode:\"7217\"")) {
            dto.setStatus(EnumResultStatus.ERROR_BUSY);
        } else if (loginResponse.body().contains("resultCode:\"7007\"") || loginResponse.body().contains("resultCode:\"7001\"")) {//图片验证码
            if (msgJson.getString("needvode").equals("0")) {
                String imgUrl = "http://uac.10010.com/portal/Service/CreateImage?t=" + System.currentTimeMillis();
                con = Jsoup.connect(imgUrl);
                con.referrer("https://uac.10010.com/portal/homeLogin");
                Connection.Response imgRespnse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                        .execute();
                logger.info("联通账号登录图片验证码cookie:"+imgRespnse.cookie("uacverifykey"));
                redisUtil.set(mobile+"_uacverifykey",imgRespnse.cookie("uacverifykey"),Long.valueOf(60*2));
                String imgPath = FileUtils.bytesToFile(path, imgRespnse.bodyAsBytes());
                dto.setStatus(EnumResultStatus.SUCCESS_IMG);
                dto.setMsg(EnumResultStatus.SUCCESS_IMG.getName());
                dto.setData(imgPath);
                return dto;
            } else {
                dto.setStatus(EnumResultStatus.ERROR_PWD);
            }
        } else {
            dto.setStatus(EnumResultStatus.ERROR);
        }
        return dto;
    }

    public ResultDto msgConfirm(String mobile,String pwd,String validateCode) throws IOException {
        ResultDto dto = new ResultDto();
        Connection con = null;
        String checkUrl = "https://uac.10010.com/portal/Service/CtaIdyChk?callback=jQuery17206213608039186365_"+System.currentTimeMillis()+"&verifyCode="+validateCode+"&verifyType=1&_="+System.currentTimeMillis();
        con = Jsoup.connect(checkUrl);
        con.referrer("https://uac.10010.com/portal/homeLogin");
        Connection.Response checkResponse = con.timeout(30000).
                method(Connection.Method.GET).cookie("uacverifykey",redisUtil.get(mobile+"_uacverifykey").toString()).ignoreContentType(true).followRedirects(true)
                .execute();
        logger.info("联通账号验证码结果:" + checkResponse.body());
        if (!checkResponse.body().contains("true")) {
            dto.setStatus(EnumResultStatus.ERROR_IMG);
            dto.setMsg(EnumResultStatus.ERROR_IMG.getName());
            return dto;
        }

        String loginUrl = "https://uac.10010.com/portal/Service/MallLogin?" +
                "callback=jQuery17206363506601136983_"+System.currentTimeMillis()+"&req_time=" + System.currentTimeMillis() +
                "&redirectURL=http%3A%2F%2Fwww.10010.com&userName="+mobile+"&password="+pwd+"&pwdType=01" +
                "&productType=01&verifyCode="+validateCode+"&uvc="+redisUtil.get(mobile+"_uacverifykey").toString()+"&redirectType=01&rememberMe=1&_="+(System.currentTimeMillis() + 3);
        con = Jsoup.connect(loginUrl);
        con.referrer("https://uac.10010.com/portal/homeLogin");
        cookieMap.put("uacverifykey",redisUtil.get(mobile+"_uacverifykey").toString());
        logger.info(redisUtil.get(mobile+"_uacverifykey").toString());
        Connection.Response loginResponse = con.timeout(30000).method(Connection.Method.GET).cookies(cookieMap).ignoreContentType(true).followRedirects(true)
                .execute();
        logger.info("联通登录结果:"+loginResponse.body());
        cookieMap.putAll(loginResponse.cookies());
        if(loginResponse.body().contains("resultCode:\"7004\"")) {
            dto.setStatus(EnumResultStatus.ERROR_PWD);
            dto.setMsg("登录密码出错已达上限,3小时后重试");
            return dto;
        } else if (!loginResponse.body().contains("resultCode:\"0000\"")) {
            dto.setStatus(EnumResultStatus.ERROR_PWD);
            dto.setMsg(EnumResultStatus.ERROR_PWD.getName());
            return dto;
        }else{
            dto.setStatus(EnumResultStatus.SUCCESS);
            dto.setMsg(EnumResultStatus.SUCCESS.getName());
            return dto;
        }
    }


    public ResultDto sendMsg(String mobile) throws IOException {
        ResultDto dto = new ResultDto();
        Connection con = null;
        String detailurl = "http://iservice.10010.com/e3/static/query/sendRandomCode?_="+System.currentTimeMillis()+"&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001";
        con = Jsoup.connect(detailurl);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("menuId","000100030001");
        Connection.Response detailResponse = con.timeout(30000).method(Connection.Method.POST).data(dataMap).cookies(cookieMap).ignoreContentType(true).followRedirects(true)
                .execute();
        String body = detailResponse.body();
        logger.info(body);
        if(StringUtils.isNotBlank(body)){
            JSONObject jsonObject = JSON.parseObject(body);
            if(jsonObject.getBoolean("issuccess")){
                dto.setStatus(EnumResultStatus.SUCCESS_MSG);
                dto.setMsg("短信验证码已经发送你的手机!");
                redisUtil.set(mobile+"_cookie",cookieMap,Long.valueOf(60*5));
                return dto;
            }else{
                dto.setStatus(EnumResultStatus.ERROR);
                dto.setMsg("短信验证码发送失败!");
            }
        }
        dto.setStatus(EnumResultStatus.ERROR);
        dto.setMsg("短信验证码发送失败!");
        return dto;
    }



    public ResultDto msgConfirm(String mobile,String msgCode) throws IOException {
        ResultDto dto = new ResultDto();
        Connection con = null;
        String url = "http://iservice.10010.com/e3/static/query/verificationSubmit?_="+System.currentTimeMillis()+"&accessURL=http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001&menuid=000100030001";
//        con.referrer("http://iservice.10010.com/e4/query/bill/call_dan-iframe.html?menuCode=000100030001");
        con = Jsoup.connect(url);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("menuId","000100030001");
        dataMap.put("inputcode",msgCode);
        cookieMap = (Map<String,String>) redisUtil.get(mobile+"_cookie");
        if(cookieMap == null){
            dto.setStatus(EnumResultStatus.ERROR_MSG_EXPIRED);
            dto.setMsg(EnumResultStatus.ERROR_MSG_EXPIRED.getName());
            return dto;
        }
        logger.info(JSON.toJSONString(cookieMap));
        Connection.Response detailResponse = con.timeout(30000).method(Connection.Method.POST).data(dataMap).cookies(cookieMap).ignoreContentType(true).followRedirects(true)
                .execute();
        String body = detailResponse.body();
        logger.info(body);
        if(StringUtils.isNotBlank(body)){
            JSONObject jsonObject = JSON.parseObject(body);
            if(jsonObject.getString("flag").equals("00")){
                return synchroData(mobile);
            }else{
                dto.setStatus(EnumResultStatus.ERROR);
                dto.setMsg("短信验证码验证失败,请重新获取!");
                return  dto;
            }
        }
        dto.setStatus(EnumResultStatus.ERROR);
        dto.setMsg("短信验证码验证失败,请重新获取!");
        return dto;
    }


    private String spider(String startDate, String endDate) throws IOException {
        Connection con = null;
        String detailurl = "http://iservice.10010.com/e3/static/query/callDetail?_=" + System.currentTimeMillis() + "&menuid=000100030001";
        con = Jsoup.connect(detailurl);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("pageNo", 1 + "");
        dataMap.put("pageSize", String.valueOf(Integer.MAX_VALUE));
        dataMap.put("beginDate", startDate);
        dataMap.put("endDate", endDate);
        Connection.Response detailResponse = con.timeout(30000).method(Connection.Method.POST).data(dataMap).cookies(cookieMap).ignoreContentType(true).followRedirects(true)
                .execute();
        logger.info(detailResponse.body());
        return detailResponse.body();
    }

    @Async
    private boolean saveTimeLength(String mobile) throws IOException {
        MobileInfo mobileInfo = mobileInfoService.getByMobile(mobile);
        if(mobileInfo == null){
            String result =  startJoinNetTime();
            if(StringUtils.isNotBlank(result)){
                mobileInfoService.save(mobile,"lt",result,null,null);
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }

    @Async
    private String startJoinNetTime() throws IOException {
        String date = null;
        Connection con = null;
        String url = "http://iservice.10010.com/e3/static/query/searchPerInfoUser/";
        con = Jsoup.connect(url);
        con.referrer("http://iservice.10010.com/e4/query/basic/personal_xx_iframe.html");
        Map<String,String> dataMap = Maps.newHashMap();
        dataMap.put("_",System.currentTimeMillis()+"");
        Connection.Response detailResponse = con.timeout(30000).method(Connection.Method.POST).data(dataMap).cookies(cookieMap).ignoreContentType(true).followRedirects(true)
                .execute();
        String result = detailResponse.body();
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSON.parseObject(result);
            if(jsonObject !=null){
                JSONObject userInfo =jsonObject.getJSONObject("userInfo");
                if(userInfo !=null){
                    String dateTime = userInfo.getString("openDate");
                    if(StringUtils.isNotBlank(dateTime)){
                        date = dateTime;
                    }
                }
            }
        }
        logger.info("入网时间:"+date);
        return date;
    }

}
