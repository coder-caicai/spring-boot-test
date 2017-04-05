package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.DataDto;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.mapper.LtCallDetailMapper;
import com.hbc.api.mapper.LtCallMapper;
import com.hbc.api.model.LtCall;
import com.hbc.api.model.LtCallDetail;
import com.hbc.api.util.DateUtil;
import com.hbc.api.util.FileUtils;
import com.hbc.api.util.HttpClientUtil;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
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


    private Map<String, String> cookieMap = new HashMap<>();


    public ResultDto synchroData(String mobile, String pwd, Integer clientId) throws IOException, ParseException {

        return spiderByDate(mobile, pwd, clientId);
    }


    /**
     * @throws IOException
     */
    private ResultDto spiderByDate(String mobile, String pwd, Integer clientId) throws IOException, ParseException {
        //先检查数据库中是否已经存在该用户数据

        List<LtCall> entityList = ltCallMapper.getListByMobile(mobile);
        List<String> dateList = DateUtil.getPreSixMonth();

        ResultDto loginResult = rePreSpider(mobile, pwd);
        if (!loginResult.getStatus().equals(EnumResultStatus.SUCCESS)) {
            return loginResult;
        }

        if (entityList == null || entityList.size() == 0) {
            for (String queryDate : dateList) {
                String startDate = DateUtil.dateConvert(DateUtil.getFirstDayInMonth(queryDate));
                String endDate = DateUtil.dateConvert(DateUtil.getEndDayInMonth(queryDate));
                boolean flag = saveBySpider(mobile, pwd, startDate, endDate, clientId);
                if (!flag) {
                    loginResult.setStatus(EnumResultStatus.ERROR);
                    loginResult.setMsg(startDate + "-" + endDate + "期间的详单出现异常请稍后再试");
                    return loginResult;
                }
            }
        } else {
            entityList.sort((x,y) -> Integer.valueOf(x.getCallDate()).compareTo(Integer.valueOf(y.getCallDate())));
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
                    loginResult.setStatus(EnumResultStatus.ERROR);
                    loginResult.setMsg(startDate + "-" + endDate + "期间的详单出现异常请稍后再试");
                    return loginResult;
                }
            }
        }
        loginResult.setStatus(EnumResultStatus.SUCCESS);
        loginResult.setMsg("服务调用成功");
        return loginResult;
    }

    @Transactional
    private boolean saveBySpider(String mobile, String pwd, String startDate, String endDate, Integer clientId) {

        try {
            String result = spider(startDate, endDate);
            logger.info("手机号:"+mobile+",详单:"+startDate+"-"+endDate+":"+result);
            if (result != null) {
                List<LtCallDetail> list = jsonToEntity(result);
                if (list != null && list.size() > 0) {
                    LtCall ltCall = new LtCall();
                    ltCall.setMobile(mobile);
                    ltCall.setCallDate(startDate.substring(0, 7).replace("-", ""));
                    ltCall.setPwd(Md5Crypt.md5Crypt(pwd.getBytes()));
                    ltCall.setClientId(clientId);
                    ltCallMapper.insert(ltCall);
                    for (LtCallDetail ltCallDetail : list) {
                        ltCallDetail.setCall_id(ltCall.getId());
                    }
                    ltCallDetailMapper.insertList(list);
                    httpClientUtil.sendDataToKafka(mobile);
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
        Map<String, String> isSuccessMap = (Map<String, String>) JSON.parse(json);
        Map<String, Map<String, List<Map<String, String>>>> map = (Map<String, Map<String, List<Map<String, String>>>>) JSON.parse(json);
        if (map != null) {
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


    private ResultDto rePreSpider(String mobile, String pwd) {
        for (int i = 0; i < 20; i++) {
            try {
                ResultDto reuslt = preSpider(mobile, pwd);
                if (reuslt.getStatus().equals(EnumResultStatus.ERROR_BUSY)) {
                    Thread.sleep(400);
                    logger.error("重试机制:" + i);
                } else {
                    return reuslt;
                }

            } catch (Exception e) {
                logger.error("重试机制出现异常");
                logger.error(e.getMessage());
            }
        }
        return null;
    }


    private ResultDto preSpider(String mobile, String pwd) throws IOException {
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
        //TODO
        String imgCode = null;
        if(StringUtils.isEmpty(imgCode)){
            loginUrl = "https://uac.10010.com/portal/Service/MallLogin?"
                    + "callback=jQuery17206171322869938429_1462934682906&req_time=" + System.currentTimeMillis()
                    + "&redirectURL=http%3A%2F%2Fwww.10010.com&userName=" + mobile + "&password=" + pwd + "&pwdType=01"
                    + "&productType=01&redirectType=01&rememberMe=1&_=" + (System.currentTimeMillis() + 3);
        }else{
            loginUrl = "https://uac.10010.com/portal/Service/MallLogin?" +
                    "callback=jQuery17206363506601136983_1482222225078&req_time=" + System.currentTimeMillis() +
                    "&redirectURL=http%3A%2F%2Fwww.10010.com&userName="+mobile+"&password="+pwd+"&pwdType=01" +
                    "&productType=01&verifyCode="+imgCode+"&uvc=ife3221f1c0072f28099e85347952beb91ajkf&redirectType=03&rememberMe=1&_="+(System.currentTimeMillis() + 3);
        }

        con = Jsoup.connect(loginUrl);
        con.referrer("https://uac.10010.com/portal/homeLogin");
        Connection.Response loginResponse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
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
                dto.setStatus(EnumResultStatus.SUCCESS_IMG);
                String imgUrl = "http://uac.10010.com/portal/Service/CreateImage?t=" + System.currentTimeMillis();
                con = Jsoup.connect(imgUrl);
                con.referrer("https://uac.10010.com/portal/homeLogin");
                Connection.Response imgRespnse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                        .execute();
                InputStream is = new ByteArrayInputStream(imgRespnse.bodyAsBytes());
//                String imgPath = FileUtils.bytesToFile(path, imgRespnse.bodyAsBytes());
//                dto.setData(imgPath);
//                File file = new File("/Users/cheng/image.jpg");
//                OutputStream os = new FileOutputStream(file);
//                int bytesRead = 0;
//                byte[] buffer = new byte[8192];
//                while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
//                    os.write(buffer, 0, bytesRead);
//                }
//                os.close();
//                is.close();
            } else {
                dto.setStatus(EnumResultStatus.ERROR_PWD);
            }
        } else {
            dto.setStatus(EnumResultStatus.ERROR);
        }
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
//        logger.info(detailResponse.body());
        return detailResponse.body();
    }


}
