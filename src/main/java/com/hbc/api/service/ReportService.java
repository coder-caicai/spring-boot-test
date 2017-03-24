package com.hbc.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Created by cheng on 2016/12/15.
 */
@Service
@ConfigurationProperties(prefix = "report")
public class ReportService {

    @Value("${source}")
    private String source;

    @Value("${target}")
    private String target;

    @Value("${url}")
    private String url;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public  String getReport(String data) throws IOException {
        url = url+data;
        logger.info(url);
        Runtime rt = Runtime.getRuntime();
        String name = UUID.randomUUID().toString();
        Process p = rt.exec(new String[]{"phantomjs", source,url,target,name});
//        InputStream is = p.getInputStream();
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        StringBuffer sbf = new StringBuffer();
//        String tmp = "";
//        while((tmp = br.readLine())!=null){
//            sbf.append(tmp);
//        }
        //System.out.println(sbf.toString());
        return name;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
