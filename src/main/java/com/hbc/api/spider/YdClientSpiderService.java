package com.hbc.api.spider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;

import java.util.Scanner;
import javax.net.ssl.SSLContext;

import com.hbc.api.util.CommonHttpMethod;
import com.hbc.api.util.ResponseValue;
import com.hbc.api.util.YD_RSA_Encrypt;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by cheng on 16/9/8.
 */
public class YdClientSpiderService {

    private static String mobile = "18211155401";
    private static String EncryptMobile = "";
    private static String EncryptServicePassword = "081305";
    private static String JSESSIONID = "";
    private static String UID = "";
    private static String month = "2016-08";
    // numEachPage最大是200
    private static int numEachPage = 200;
    // 当前page页码
    private static int pageNO = 1;
    private static String smsCode = "670565";

    public static String getSMSCode(String mobile, String servicePassword) {
        EncryptMobile = YD_RSA_Encrypt
                .getEntrypt("leadeon" + mobile + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
        EncryptServicePassword = YD_RSA_Encrypt
                .getEntrypt("leadeon" + servicePassword + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
        String data = "{\"cid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"3\",\"reqBody\":{\"ccPasswd\":\""
                + EncryptServicePassword + "\",\"cellNum\":\"" + EncryptMobile
                + "\",\"sendSmsFlag\":\"1\"},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"\"}";
        login(data);
        data = "{\"cid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"0\",\"reqBody\":{\"cellNum\":\"13701346824\"},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"962b5407ae093cad36206ace578a2504\"}";
        test(data);
        return "";
    }

    /**
     * 登录
     */
    public static void login(String data) {
        String url = "https://clientaccess.10086.cn/biz-orange/LN/uamlogin/login";
        HttpPost postMethod = new HttpPost(url);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);//
        postMethod.setEntity(myEntity);
        ResponseValue res = doPostSSLUID(postMethod);
        for (Cookie c : res.getCookies()) {
            if (c.getName().equals("JSESSIONID")) {
                JSESSIONID = c.getValue();
                System.out.println("JSESSIONID: " + JSESSIONID);
            }
            if (c.getName().equals("UID")) {
                UID = c.getValue();
                System.out.println("UID: " + UID);
            }
        }

        System.out.println(res.getResponse());
    }

    /**
     * 登录
     */
    public static void test(String data) {
        String url = "https://clientaccess.10086.cn/biz-orange/LN/uamrandcode/sendMsgLogin";
        HttpPost postMethod = new HttpPost(url);
        String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
        postMethod.setHeader("Cookie", cookie);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        postMethod.setEntity(myEntity);
        ResponseValue res = CommonHttpMethod.doPostSSL(postMethod);
        System.out.println(res.getResponse());
    }

    /**
     * 通用post方法
     *
     * @param postRequest
     * @return
     */
    public static ResponseValue doPostSSLUID(HttpPost postRequest) {
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
                for (int i = 0; i < header.length; i++) {
                    if (header[i].getValue().contains("UID")) {
                        UID = header[0].getValue().split("UID=")[1];
                        UID = UID.split(";")[0];
                        System.out.println("UID: " + UID);
                    }
                }

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

    public static CloseableHttpClient createSSLClientDefault() {
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

    // 获取账单信息

    /**
     * 手机验证码
     */
    public static void smsCheck(String smsCode) {
        String url = "https://clientaccess.10086.cn/biz-orange/LN/tempIdentCode/getTmpIdentCode";
        HttpPost postMethod = new HttpPost(url);
//		String EncryptMobile = YD_RSA_Encrypt.getEntrypt("leadeon" + mobile + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
//		String EncryptServicePassword =  YD_RSA_Encrypt.getEntrypt("leadeon" + servicePassword + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
        String data = "{\"cid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"0\",\"reqBody\":{\"businessCode\":\"01\",\"cellNum\":\"" + EncryptMobile + "\",\"passwd\":\"" + EncryptServicePassword + "\",\"smsPasswd\":\"" + smsCode + "\"},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"962b5407ae093cad36206ace578a2504\"}";
        String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
        postMethod.setHeader("Cookie", cookie);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        postMethod.setEntity(myEntity);
        ResponseValue res = CommonHttpMethod.doPostSSL(postMethod);
        for (Cookie c : res.getCookies()) {
            if (c.getName().equals("JSESSIONID")) {
                JSESSIONID = c.getValue();
                System.out.println("JSESSIONID: " + JSESSIONID);
            }
            if (c.getName().equals("UID")) {
                UID = c.getValue();
                System.out.println("UID: " + UID);
            }
        }
//		System.out.println(res.getResponse());
    }

    /**
     * 查询
     */
    public static String getDetail() {
        String url = "https://clientaccess.10086.cn/biz-orange/BN/queryDetail/getDetail";
        HttpPost postMethod = new HttpPost(url);
        String data = "{\"ak\":\"F4AA34B89513F0D087CA0EF11A3277469DC74905\",\"cid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"0\",\"reqBody\":{\"billMonth\":\"" + month + "\",\"cellNum\":\"" + mobile + "\",\"page\":" + pageNO + ",\"tmemType\":\"02\",\"unit\":" + numEachPage + "},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"e517313049ef8e12b82bf3e30d574362\"}";
        String cookie = "JSESSIONID=" + JSESSIONID + "; UID=" + UID + "; Comment=SessionServer-unity; Path=/; Secure";
        postMethod.setHeader("Cookie", cookie);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);
        postMethod.setEntity(myEntity);
        ResponseValue res = CommonHttpMethod.doPostSSL(postMethod);
        System.out.println(res.getResponse());
        return res.getResponse();
    }

//    public static void main(String[] args) {
//        EncryptMobile = YD_RSA_Encrypt
//                .getEntrypt("leadeon" + mobile + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
//        EncryptServicePassword = YD_RSA_Encrypt.getEntrypt(
//                "leadeon" + EncryptServicePassword + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
//        String data = "{\"cid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"3\",\"reqBody\":{\"ccPasswd\":\""
//                + EncryptServicePassword + "\",\"cellNum\":\"" + EncryptMobile
//                + "\",\"sendSmsFlag\":\"1\"},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"\"}";
//        login(data);
//        data = "{\"cid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"ctid\":\"+C5zXwvbuUoKb0v7AhOkXRX+qPlj+28JYaoJaxCMK8vBCLxJ9aeq2IlqFgZDcj8NoSzAohRNVoSYW0ltlxT0S3DqkzEcNiLsfb4V1OaqDHx/09zZz6UDAnKGytrluheb\",\"cv\":\"3.1.0\",\"en\":\"0\",\"reqBody\":{\"cellNum\":\""
//                + mobile + "\"},\"sn\":\"H30-T10\",\"sp\":\"720x1280\",\"st\":\"1\",\"sv\":\"4.4.2\",\"t\":\"\"}";
//        test(data);
//        System.out.println("JSESSIONID: " + JSESSIONID);
//        System.out.println("UID: " + UID);
//        System.out.println("请输入短信验证码：");
//        Scanner messagesc = new Scanner(System.in);
//        String messageCode = messagesc.nextLine();
//        smsCheck(messageCode);
//        getDetail();
//    }
}

