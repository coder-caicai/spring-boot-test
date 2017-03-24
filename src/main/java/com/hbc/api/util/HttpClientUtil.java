package com.hbc.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * HttpClient
 *
 * @author ChengYongfei
 * @version v1.0
 * @createTime 2016年11月3日
 */
@Service
public class HttpClientUtil {

    @Value("${geo.url}")
    public  String geoReceiveUrl;

    @Value("${geo.pwd}")
    public  String pwd;

    protected static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * HttpClient连接SSL
     */
    public void ssl() {
        CloseableHttpClient httpclient = null;
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream instream = new FileInputStream(new File("d:\\tomcat.keystore"));
            try {
                // 加载keyStore d:\\tomcat.keystore
                trustStore.load(instream, "123456".toCharArray());
            } catch (CertificateException e) {
                e.printStackTrace();
            } finally {
                try {
                    instream.close();
                } catch (Exception ignore) {
                }
            }
            // 相信自己的CA和所有自签名的证书
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
            // 只允许使用TLSv1协议
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            // 创建http请求(get方式)
            HttpGet httpget = new HttpGet("https://localhost:8443/myDemo/Ajax/serivceJ.action");
            System.out.println("executing request" + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                if (entity != null) {
                    System.out.println("Response content length: " + entity.getContentLength());
                    System.out.println(EntityUtils.toString(entity));
                    EntityUtils.consume(entity);
                }
            } finally {
                response.close();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * post方式提交表单（模拟用户登录请求）
     */
    public void postForm() {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost("http://localhost:8080/myDemo/Ajax/serivceJ.action");
        // 创建参数队列
        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
        formparams.add(new BasicNameValuePair("username", "admin"));
        formparams.add(new BasicNameValuePair("password", "123456"));
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送 get请求
     */
    public static String get(String address) {
        int status = 0;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String body = null;
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(address);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity, "UTF-8");
                logger.debug(body);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            // 网络错误
            status = 3;
        } finally {
            logger.debug("调用接口状态：" + status);
        }
        return body;
    }

    /**
     * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
     */
    public void post() {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost("http://localhost:8080/myDemo/Ajax/serivceJ.action");
        // 创建参数队列
        List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
        formparams.add(new BasicNameValuePair("type", "house"));
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            System.out.println("executing request " + httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 调用 API，JSON
     *
     * @param parameters
     * @return
     */
    public static String APIpost(String address, String parameters) {
        long startTime = 0L;
        long endTime = 0L;
        int status = 0;
        logger.debug("URL:" + address + " ,parameters:" + parameters);
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(address);
        httppost.setHeader("Content-Type", "application/json");
        //设置过期时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httppost.setConfig(requestConfig);
        String body = null;
        if (parameters != null && !"".equals(parameters.trim())) {
            try {
                HttpEntity se = new StringEntity(parameters, "UTF-8");
                httppost.setEntity(se);
                startTime = System.currentTimeMillis();
                // 设置编码
                HttpResponse response = httpclient.execute(httppost);
                endTime = System.currentTimeMillis();
                status = response.getStatusLine().getStatusCode();
                logger.debug("调用API 花费时间(单位：毫秒)：" + (endTime - startTime));
                // Read the response body
                body = EntityUtils.toString(response.getEntity());
                logger.debug(body);
            } catch (IOException e) {
                logger.error(e.getMessage());
                // 网络错误
                status = 3;
            } finally {
                logger.debug("调用接口状态：" + status);
            }
        }
        return body;
    }


    public static boolean  sendJsonDataByPost(String url, Map<String, Object> data) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost postMethod = new HttpPost(url);
        postMethod.setHeader("Content-Type", "application/json");
        StringEntity myEntity = new StringEntity(JSON.toJSONString(data), ContentType.APPLICATION_JSON);
        postMethod.setEntity(myEntity);
        try {
            logger.info("请求开始:"+url);
            CloseableHttpResponse response = httpclient.execute(postMethod);
            if(response.getStatusLine().getStatusCode() == 200){
                logger.info("数据发送成功!");
                HttpEntity entity = response.getEntity();
                String res = EntityUtils.toString(entity);
                logger.info("请求返回:"+res);
                response.close();
            }else{
                logger.error("数据发送失败!");
                return false;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static String  sendDataByPost(String url, Map<String, String> data) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost postMethod = new HttpPost(url);
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        for(String key :  data.keySet()){
            params.add(new BasicNameValuePair(key,data.get(key)));
        }
        try {
            postMethod.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            logger.info("请求开始:"+url);
            CloseableHttpResponse response = httpclient.execute(postMethod);
            if(response.getStatusLine().getStatusCode() == 200){
                logger.info("数据发送kafka成功!");
                HttpEntity entity = response.getEntity();
                String res = EntityUtils.toString(entity);
                logger.info("请求kafka返回:"+res);
                response.close();
                return res;
            }else{
                logger.error("数据kafka发送失败!");
                return "";
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

    public String sendDataToKafka(String mobile){
        Map<String,String> map = Maps.newHashMap();
        String time = System.currentTimeMillis()+"";
        String md5 = Md5Util.string2MD5(pwd+"_"+time);
        map.put("phone",mobile);
        map.put("user","pachong");
        map.put("time",time);
        map.put("md5",md5);
        map.put("month",DateUtil.sdfYYYY_MM.format(new Date()));
        return sendDataByPost(geoReceiveUrl,map);
    }
}
