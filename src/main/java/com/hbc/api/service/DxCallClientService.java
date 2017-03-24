package com.hbc.api.service;

import com.google.common.collect.Maps;
import com.hbc.api.dto.DataDto;
import com.hbc.api.mapper.DxCallClientMapper;
import com.hbc.api.mapper.DxCallDetailClientMapper;
import com.hbc.api.model.DxCallClient;
import com.hbc.api.model.DxCallDetailClient;
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
import org.apache.tomcat.util.security.MD5Encoder;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
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
    public boolean login(String mobile, String pwd,Integer clientId) throws Exception {
        redisUtil.remove(mobile);
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
            map.put("clientId",clientId+"");
            String token = map.get("Token");
            msg(mobile, token);

            redisUtil.set(mobile, map, Long.valueOf(60 * 2));
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
        List<String> dateList = DateUtil.getPreSixMonth();
        if (entityList == null || entityList.size() == 0) {
            for (String queryDate : dateList) {
                saveBySpider(mobile, msg, DateUtil.getFirstDayInMonth(queryDate), DateUtil.getEndDayInMonth(queryDate));
            }
        } else {

            entityList.sort((x,y) -> Integer.valueOf(x.getCallDate()).compareTo(Integer.valueOf(y.getCallDate())));
            dxCallClientMapper.delete(entityList.get(entityList.size()-1));
            entityList.remove(entityList.size()-1);

            for (DxCallClient dxCallClient : entityList) {
                if (dateList.indexOf(dxCallClient.getCallDate()) > -1) {
                    dateList.remove(dxCallClient.getCallDate());
                }
            }
            for (String queryDate : dateList) {
                saveBySpider(mobile, msg, DateUtil.getFirstDayInMonth(queryDate), DateUtil.getEndDayInMonth(queryDate));
            }

        }
        return true;
    }

    /**
     * 保存爬虫数据
     *
     * @param
     * @throws IOException
     * @throws ParseException
     */
    @Async
    @Transactional
    private void saveBySpider(String mobile, String msg, String startDate, String endDate) throws Exception {

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
            dxCallClientMapper.insert(dxCallClient);
            Integer dxCallClientId = dxCallClient.getId();
            for (DxCallDetailClient dxCallDetailClient : dxCallDetailClients) {
                dxCallDetailClient.setCallId(dxCallClientId);
            }
            dxCallDetailClientMapper.insertList(dxCallDetailClients);
            httpClientUtil.sendDataToKafka(mobile);
        }
    }

    /**
     * http post 请求工具
     *
     * @param data
     * @return
     * @throws Exception
     */
    private String httpUtil(String data) throws Exception {
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
    }


}
