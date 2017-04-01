package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.hbc.api.mapper.DxCallClientMapper;
import com.hbc.api.mapper.DxCallDetailClientMapper;
import com.hbc.api.model.DxCallClient;
import com.hbc.api.model.DxCallDetailClient;
import com.hbc.api.model.MobileInfo;
import com.hbc.api.util.DESedeCoder;
import com.hbc.api.util.DateUtil;
import com.hbc.api.util.HttpClientUtil;
import com.hbc.api.util.RedisUtil;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ccz
 * @since 2016-07-22 11:09
 */
@Service
public class DxCallClientService {

    @Autowired
    private DxCallClientMapper dxCallClientMapper;

    @Autowired
    private DxCallDetailClientMapper dxCallDetailClientMapper;

    @Autowired
    private DxCallDetailClientService dxCallDetailClientService;

    @Autowired
    private MobileInfoService mobileInfoService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private HttpClientUtil httpClientUtil;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

    private static String url = "http://cservice.client.189.cn:8004/map/clientXML/?encrypted=true";

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 登录
     *
     * @param mobile
     * @param pwd
     * @return
     * @throws Exception
     */
    public boolean login(String mobile, String pwd, Integer clientId) throws Exception {
        redisUtil.remove(mobile);
        try {
            saveTimeLength(mobile);
        } catch (Exception e) {
            logger.error("调用验真在网时长失败!");
            logger.error(e.getMessage());
        }
        String data = "<Request>"
                + "<Content>"
                + "<FieldData>"
                + "<PswType>01</PswType>"
                + "<DeviceToken>01dc409d5f6d2f78acf6e4964d25998cdc1a9c7b95f3a852dcabbea446ad192d</DeviceToken>"
                + "<AccountType>c2000004</AccountType>"
                + "<PhoneNbr>" + mobile + "</PhoneNbr>"
                + "<PhonePsw>" + pwd + "</PhonePsw>"
                + "</FieldData>"
                + "<Attach>iPhone</Attach>"
                + "</Content>"
                + "<HeaderInfos>"
                + "<Source>120002</Source>"
                + "<UserLoginName>" + mobile + "</UserLoginName>"
                + "<Code>loginInfo</Code>"
                + "<Token></Token>"
                + "<Timestamp>" + sdf.format(new Date()) + "</Timestamp>"
                + "<ClientType>#5.5.0#channel50#iPhone 6 Plus#</ClientType>"
                + "<SourcePassword>TiqmIZ</SourcePassword>"
                + "</HeaderInfos>"
                + "</Request>";
        String result = httpUtil(data);
        logger.info("#########login:" + result);
        SAXReader saxReader = new SAXReader();
        Reader reader = new StringReader(result);
        Document document = saxReader.read(reader);
        Element responseElement = document.getRootElement();
        Map<String, String> map = new HashMap<>();
        for (Iterator i = responseElement.elementIterator(); i.hasNext(); ) {
            Element HeaderInfos = (Element) i.next();
            map.put(HeaderInfos.getName(), HeaderInfos.getText());
            for (Iterator j = HeaderInfos.elementIterator(); j.hasNext(); ) {
                Element ResponseData = (Element) j.next();
                map.put(ResponseData.getName(), ResponseData.getText());
                for (Iterator k = ResponseData.elementIterator(); k.hasNext(); ) {
                    Element Data = (Element) k.next();
                    map.put(Data.getName(), Data.getText());
                    for (Iterator h = Data.elementIterator(); h.hasNext(); ) {
                        Element init = (Element) h.next();
                        map.put(init.getName(), init.getText());
                    }
                }
            }
        }
        logger.info(map.get("ResultCode"));
        if (map == null || !map.get("ResultCode").equals("0000")) {
            return false;
        } else {
            map.put("pwd", pwd);
            map.put("clientId", clientId + "");
            String token = map.get("Token");
//            msg(mobile, token);
            redisUtil.set(mobile, map, Long.valueOf(60 * 10));
            try {
                Map<String, Double> mmap = getCostByMonth(mobile);
                redisUtil.set(mobile+"_cost",mmap,Long.valueOf(60*10));
                logger.info(JSON.toJSONString(mmap));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return true;
        }
    }

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @param token
     * @throws Exception
     */
    private void msg(String mobile, String token) throws Exception {
        String data = "<Request>"
                + "<Content>"
                + "<FieldData>"
                + "<PhoneNbr>" + mobile + "</PhoneNbr>"
                + "</FieldData>"
                + "<Attach>iPhone</Attach>"
                + "</Content>"
                + "<HeaderInfos>"
                + "<Source>120002</Source>"
                + "<UserLoginName>" + mobile + "</UserLoginName>"
                + "<Code>getRandomV2</Code>"
                + "<Token>" + token + "</Token>"
                + "<Timestamp>" + sdf.format(new Date()) + "</Timestamp>"
                + "<ClientType>#5.5.0#channel50#iPhone 6 Plus#</ClientType>"
                + "<SourcePassword>TiqmIZ</SourcePassword>"
                + "</HeaderInfos>"
                + "</Request>";
        String result = httpUtil(data);
        logger.info("#######msg:" + result);
    }

    /**
     * 数据抓取
     *
     * @param mobile
     * @param msg
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private List<DxCallDetailClient> spiderDetail(String mobile, String msg, String startDate, String endDate) throws Exception {
        Map<String, String> redisMap = (Map<String, String>) redisUtil.get(mobile);
        String token = redisMap.get("Token");
        String data = "<Request>"
                + "<Content>"
                + "<FieldData>"
                + "<PhoneNum>" + mobile + "</PhoneNum>"
                + "<Type>1</Type>"
                + "<StartTime>" + startDate + "</StartTime>"
                + "<EndTime>" + endDate + "</EndTime>"
                + "<Random>" + msg + "</Random>"
                + "</FieldData>"
                + "<Attach>iPhone</Attach>"
                + "</Content>"
                + "<HeaderInfos>"
                + "<Source>120002</Source>"
                + "<UserLoginName>" + mobile + "</UserLoginName>"
                + "<Code>jfyBillDetail</Code>"
                + "<Token>" + token + "</Token>"
                + "<Timestamp>" + sdf.format(new Date()) + "</Timestamp>"
                + "<ClientType>#5.5.0#channel50#iPhone 6 Plus#</ClientType>"
                + "<SourcePassword>TiqmIZ</SourcePassword>"
                + "</HeaderInfos>"
                + "</Request>";
        String result = httpUtil(data);
        logger.info("#####detail:" + result);
        SAXReader saxReader = new SAXReader();
        Reader reader = new StringReader(result);
        Document document = saxReader.read(reader);
        Element Response = document.getRootElement();
        List<Element> Responses = Response.elements();
        Element ResponseData = Responses.get(1);
        List<Element> ResponseDatas = ResponseData.elements();
        if (ResponseDatas.size() < 4) {
            return null;
        }
        Element Data = ResponseDatas.get(3);
        List<Element> ItemsList = Data.elements();
        if (ItemsList != null && ItemsList.size() > 0) {
            Element Items = ItemsList.get(0);
            List<Element> itemList = Items.elements();
            List<DxCallDetailClient> resultList = new ArrayList<>();
            for (Element element : itemList) {
                DxCallDetailClient dxCallDetailClient = new DxCallDetailClient();
                dxCallDetailClient.setCallArea(element.elementText("CallArea"));
                dxCallDetailClient.setCallType(element.elementText("CallType"));
                dxCallDetailClient.setCallFee(element.elementText("CallFee").equals("null") ? 0 : Double.valueOf(element.elementText("CallFee")));
                dxCallDetailClient.setCallMobile(element.elementText("CallMobile"));
                dxCallDetailClient.setCallStyle(element.elementText("CallStyle").equals("null") ? "0" : "1");
                dxCallDetailClient.setCallTime(element.elementText("CallTime"));
                dxCallDetailClient.setCallTimeCost(element.elementText("CallTimeCost"));
                resultList.add(dxCallDetailClient);
            }
            return resultList;
        }
        return null;
    }

    /**
     * 同步数据
     *
     * @param mobile
     * @param msg
     * @return
     * @throws Exception
     */
    public boolean synchroData(String mobile, String msg) throws Exception {
        //先检查数据库中是否已经存在该用户数据
        List<DxCallClient> entityList = dxCallClientMapper.getListByMobile(mobile);
        Map<String, Double> rmap = Maps.newHashMap();
        List<String> dateList = DateUtil.getPreSixMonth();
        if (entityList == null || entityList.size() == 0) {
            for (String queryDate : dateList) {
                saveBySpider(mobile, msg, DateUtil.getFirstDayInMonth(queryDate), DateUtil.getEndDayInMonth(queryDate), rmap.get(queryDate));
            }
        } else {
            entityList.sort((x, y) -> Integer.valueOf(x.getCallDate()).compareTo(Integer.valueOf(y.getCallDate())));
            dxCallClientMapper.delete(entityList.get(entityList.size() - 1));
            entityList.remove(entityList.size() - 1);

            for (DxCallClient dxCallClient : entityList) {
                if (dateList.indexOf(dxCallClient.getCallDate()) > -1) {
                    dateList.remove(dxCallClient.getCallDate());
                }
            }
            for (String queryDate : dateList) {
                saveBySpider(mobile, msg, DateUtil.getFirstDayInMonth(queryDate), DateUtil.getEndDayInMonth(queryDate), rmap.get(queryDate));
            }

        }
        httpClientUtil.sendDataToKafka(mobile);
        return true;
    }

    /**
     * 月消费金额
     *
     * @param mobile
     * @return
     * @throws Exception
     */
//    private Map<String, Double> getCostByMonth(String mobile) throws Exception {
//        Map<String, String> redisMap = (Map<String, String>) redisUtil.get(mobile);
//        Map<String,Double> resultMap = Maps.newHashMap();
//        Connection con = null;
//        String token = redisMap.get("Token");
////        String tokenUrl = "http://cservice.client.189.cn:8004/map/clientXML/?encrypted=true";
//
//        Calendar cal = Calendar.getInstance();
//        String end = DateUtil.sdfYYYY_MM.format(cal.getTime());
//        cal.add(Calendar.MONTH, -5);
//        String begin = DateUtil.sdfYYYY_MM.format(cal.getTime());
//        String url = "http://content.kefu.189.cn/tykfh5/services/dispatch.jsp?&dispatchUrl=ClientUni/clientuni/services/fee/periodFee?" +
//                "reqParam={\"token\":\""+token+"\",\"fromDate\":\""+begin.replace("-","")+"\",\"toDate\":\""+end.replace("-","")+"\",\"reqTime\":\""+System.currentTimeMillis()+"\"}";
//         con = Jsoup.connect(url);
//        con.referrer("http://content.kefu.189.cn/tykfh5/modules/businessHandling/his4TelFee/index.html?ReqParam=%7B%22mobile%22%3A%2217762271294%22%2C%22province%22%3A%22%E9%99%95%E8%A5%BF%22%2C%22channel%22%3A%22channel50%22%2C%22city%22%3A%22%E8%A5%BF%E5%AE%89%22%2C%22userName%22%3A%22%E5%90%B4%2A%22%2C%22userLevel%22%3A%22%22%2C%22appId%22%3A%22huango%22%2C%22token%22%3A%223b272cf358c9a2eb0f501fc4e9f21c82%22%2C%22isLogin%22%3A%220%22%2C%22userType%22%3A%22%22%2C%22loginType%22%3A%22%22%7D");
//        Response response = con.method(Method.GET).ignoreContentType(true).timeout(30000).execute();
//        logger.info(response.body());
//        if(StringUtils.isNotBlank(response.body())){
//            JSONArray array = JSON.parseArray(response.body());
//            array.forEach( e -> {
//                JSONObject json =(JSONObject) JSON.toJSON(e);
//                resultMap.put(json.getString("date"),json.getDouble("fee"));
//            });
//            return resultMap;
//        }
//        return null;
//    }

    private Map<String,Double> getCostByMonth(String mobile) throws Exception {
        Map<String, String> redisMap = (Map<String, String>) redisUtil.get(mobile);
        Map<String,Double> resultMap = Maps.newHashMap();
        String token = redisMap.get("Token");
        String data = "<Request>"
                + "<Content>"
                + "<FieldData>"
                + "<PhoneNum>" + mobile + "</PhoneNum>"
                + "<Type>1</Type>"
                + "<ShopId>20004</ShopId>"
                + "</FieldData>"
                + "<Attach>iPhone</Attach>"
                + "</Content>"
                + "<HeaderInfos>"
                + "<Source>120002</Source>"
                + "<UserLoginName>" + mobile + "</UserLoginName>"
                + "<Code>jfyBillOf6Month</Code>"
                + "<Token>" + token + "</Token>"
                + "<Timestamp>" + sdf.format(new Date()) + "</Timestamp>"
                + "<ClientType>#5.7.0#channel50#iPhone 6 Plus#</ClientType>"
                + "<SourcePassword>TiqmIZ</SourcePassword>"
                + "</HeaderInfos>"
                + "</Request>";
        String result = httpUtil(data);
        logger.info("#####月消费金额:" + result);
        if(StringUtils.isNotBlank(result)){
            SAXReader saxReader = new SAXReader();
            Reader reader = new StringReader(result);
            Document document = saxReader.read(reader);
            Element response = document.getRootElement();
            Element HeaderInfos = response.element("HeaderInfos");
            Element code = HeaderInfos.element("Code");
            List<String> months = DateUtil.getPreSixMonth();
            if(code.getData().equals("0000")){
                Element ResponseData = response.element("ResponseData");
                Element Data = ResponseData.element("Data");
                if(Data != null){
                    List<Element> list = Data.elements();
                    logger.info(list.get(0).getData().toString());
                    logger.info(list.get(0).getName());
                    list.forEach(e -> {
                        if(e.getName().equals("BillAmount1")){
                            resultMap.put(months.get(0), StringUtils.isBlank(e.getData().toString())?0:Double.valueOf(e.getData().toString()));
                        }
                        if(e.getName().equals("BillAmount2")){
                            resultMap.put(months.get(1),StringUtils.isBlank(e.getData().toString())?0:Double.valueOf(e.getData().toString()));
                        }
                        if(e.getName().equals("BillAmount3")){
                            resultMap.put(months.get(2),StringUtils.isBlank(e.getData().toString())?0:Double.valueOf(e.getData().toString()));
                        }
                        if(e.getName().equals("BillAmount4")){
                            resultMap.put(months.get(3),StringUtils.isBlank(e.getData().toString())?0:Double.valueOf(e.getData().toString()));
                        }
                        if(e.getName().equals("BillAmount5")){
                            resultMap.put(months.get(4),StringUtils.isBlank(e.getData().toString())?0:Double.valueOf(e.getData().toString()));
                        }
                        if(e.getName().equals("BillAmount6")){
                            resultMap.put(months.get(5),StringUtils.isBlank(e.getData().toString())?0:Double.valueOf(e.getData().toString()));
                        }
                    });
                }else{
                    return null;
                }
                return resultMap;
            }
        }
        return null;
    }
    private void saveBySpider(String mobile, String msg, String startDate, String endDate, Double cost) throws Exception {
        List<DxCallDetailClient> dxCallDetailClients = spiderDetail(mobile, msg, startDate, endDate);
        if (dxCallDetailClients != null && dxCallDetailClients.size() > 0) {
            Map<String, String> redisMap = (Map<String, String>) redisUtil.get(mobile);
            Integer clientId = Integer.parseInt(redisMap.get("clientId"));
            DxCallClient dxCallClient = new DxCallClient();
            dxCallClient.setCallDate(startDate.substring(0, 6));
            dxCallClient.setMobile(mobile);
            dxCallClient.setPwd(Md5Crypt.md5Crypt(redisMap.get("pwd").getBytes()));
            dxCallClient.setProvince(redisMap.get("ProvinceName") + "-" + redisMap.get("CityName"));
            dxCallClient.setClientId(clientId);
            Map<String,Double> costMap = (Map<String,Double> )redisUtil.get(mobile+"_cost");
            if(costMap !=null){
                dxCallClient.setCost(costMap.get(startDate.replace("-","").substring(0,6)));
            }else{
                dxCallClient.setCost(cost);
            }
            dxCallClientMapper.insert(dxCallClient);
            Integer dxCallClientId = dxCallClient.getId();
            for (DxCallDetailClient dxCallDetailClient : dxCallDetailClients) {
                dxCallDetailClient.setCallId(dxCallClientId);
            }
            dxCallDetailClientMapper.insertList(dxCallDetailClients);
        }
    }

    /**
     * http post 请求工具
     *
     * @param data
     * @return
     * @throws Exception
     */
    private String httpUtil(String data) {
        try {
            data = DESedeCoder.doEncryptData(data);
            HttpClient httpclient = new HttpClient();
            PostMethod post = new PostMethod(url);
            String info = null;
            RequestEntity entity = new StringRequestEntity(data, "text/xml", "iso-8859-1");
            post.setRequestEntity(entity);
            httpclient.executeMethod(post);
            int code = post.getStatusCode();
            if (code == HttpStatus.SC_OK)
                info = new String(post.getResponseBodyAsString());
            String result = DESedeCoder.doDecryptData(info);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Async
    private boolean saveTimeLength(String mobile) throws Exception {
        MobileInfo mobileInfo = mobileInfoService.getByMobile(mobile);
        String result = dxCallDetailClientService.getTimeLength(mobile);
        if (mobileInfo == null) {
            if (StringUtils.isNotBlank(result)) {
                mobileInfoService.save(mobile, "dx", result, null, null);
                return true;
            } else {
                return false;
            }
        } else {
            mobileInfo.setTime_length(result);
            mobileInfoService.update(mobileInfo);
            return true;
        }
    }
}
