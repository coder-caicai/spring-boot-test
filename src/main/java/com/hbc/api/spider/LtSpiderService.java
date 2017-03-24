package com.hbc.api.spider;


import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cheng on 16/6/30.
 */
public class LtSpiderService {

    public static String  spider(String mobile,String pwd,String startDate,String endDate) throws IOException {

        Map<String, String> cookieMap = new HashMap<>();
        Connection con = null;

        String indexUrl = "https://uac.10010.com/portal/Service/CheckNeedVerify?"
                + "callback=jQuery17206171322869938429_1462934682905&userName=" + mobile + "&pwdType=01&_="
                + System.currentTimeMillis();

        con = Jsoup.connect(indexUrl);
        con.header("APIAuthorize-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
        con.referrer("https://uac.10010.com/portal/homeLogin");

        Response indexResponse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                .execute();
        cookieMap.putAll(indexResponse.cookies());


        String loginUrl = "https://uac.10010.com/portal/Service/MallLogin?"
                + "callback=jQuery17206171322869938429_1462934682906&req_time=" + System.currentTimeMillis()
                + "&redirectURL=http%3A%2F%2Fwww.10010.com&userName=" + mobile + "&password=" + pwd + "&pwdType=01"
                + "&productType=01&redirectType=01&rememberMe=1&_=" + (System.currentTimeMillis() + 3);

        con = Jsoup.connect(loginUrl);
        con.referrer("https://uac.10010.com/portal/homeLogin");
        Response loginResponse = con.timeout(30000).method(Connection.Method.GET).ignoreContentType(true).followRedirects(true)
                .execute();
        cookieMap.putAll(loginResponse.cookies());

        String checkUrl = "http://iservice.10010.com/e3/static/check/checklogin/?_=" + System.currentTimeMillis();

        con = Jsoup.connect(checkUrl);
        con.referrer("http://iservice.10010.com/e3/query/call_dan.html?menuId=000100030001");
        con.header("Host", "iservice.10010.com");
        con.header("x-requested-with", "XMLHttpRequest");

        Response checkResponse = con.timeout(30000).method(Connection.Method.POST).cookies(cookieMap).ignoreContentType(true).followRedirects(true)
                .execute();
        cookieMap.putAll(checkResponse.cookies());

        String detailurl = "http://iservice.10010.com/e3/static/query/callDetail?_=" + System.currentTimeMillis() + "&menuid=000100030001";
        con = Jsoup.connect(detailurl);
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("pageNo", 1+"");
        dataMap.put("pageSize", String.valueOf(Integer.MAX_VALUE));
        dataMap.put("beginDate", startDate);
        dataMap.put("endDate",  endDate);
        Response detailResponse = con.timeout(30000).method(Connection.Method.POST).data(dataMap).cookies(cookieMap).ignoreContentType(true).followRedirects(true)
                .execute();
        return detailResponse.body();
    }

}
