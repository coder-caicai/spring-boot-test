package com.hbc.api.spider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import com.alibaba.fastjson.JSONObject;

public class DxGdClientSpider {

	private static String imei = "860308022570432";
//	private static String mobile = "18928320007";
//	private static String pwd = "110726";
	private static String mobile = "17722862060";
	private static String pwd = "989977";
	private static String domain = "http://61.140.99.28:8080/MOService/api?v=2.1&name=jbAClientSp&category=android&imsi="+mobile+"&paramStr=";
	private static String termcode = "MI2";
	private static String qrytime = "201606";
	public static final String a(String paramString)
	{
		BigInteger param= new BigInteger(paramString.getBytes());
		return new BigInteger("02013302259969").xor(param).toString(16);
	}
	public static String g(String paramString) {
		if ((paramString == null) || (paramString.length() == 0)) {
			throw new IllegalArgumentException("String to encript cannot be null or zero length");
		}
		StringBuffer localStringBuffer = new StringBuffer();
		int m = 0;
		while (true) {
			try {
				MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
				localMessageDigest.update(paramString.getBytes());
				byte[] md5Digest = localMessageDigest.digest();
				if (m < md5Digest.length) {
					if ((md5Digest[m] & 0xFF) < 16)
						localStringBuffer.append("0" + Integer.toHexString(md5Digest[m] & 0xFF));
					else
						localStringBuffer.append(Integer.toHexString(md5Digest[m] & 0xFF));
				} else {
					return localStringBuffer.toString().toUpperCase();
				}
			} catch (Exception e) {
				e.printStackTrace();
				// au.a("mD5crypt", paramString);
			}
			m += 1;
		}

	}

	// 加密
	public static final String a(String paramString1, String paramString2) {
		BigInteger bigInteger = new BigInteger(paramString1.getBytes());
		return new BigInteger(paramString2.hashCode() + "").xor(bigInteger).toString(16);
	}

	// 解密
	public static final String b(String paramString1, String paramString2) {
		BigInteger bigInteger = new BigInteger(paramString2.hashCode() + "");
		try {
			paramString1 = new String(new BigInteger(paramString1, 16).xor(bigInteger).toByteArray());
			return paramString1;
		} catch (Exception e) {
		}
		return "";
	}

	// 解密
	public static final String b(String paramString2) {
		BigInteger bigInteger = new BigInteger("02013302259969".hashCode() + "");
		try {
			return  new String(new BigInteger(paramString2, 16).xor(bigInteger).toByteArray());
		} catch (Exception e) {
		}
		return "";
	}
	public static void test(String[] args) throws IOException {
		Map<String, String> cookieMap = new HashMap<>();
		Connection con = null;
		String md5 = g("androidjbAClientSp2.1v:2!2F8H2d&]");
		Long m = System.currentTimeMillis();
		String sn = a(imei+mobile);
		String para = mobile+m+"3"+"jbAClientSp"+"2.1"+"v:2!2F8H2d&]"+"1"+"android"+"MI2"+sn+"";
		String sig = g(para);
//		String paramStr = "-69703d31302e302e322e3135266d6574686f643d757365722e636c69656e744c6f67696e322665736e3d267465726d636f64653d4d49322674696d657374616d703d31343734373331393637303830266368616e6e656c3d3326763d322e3126666f726d61743d31267369673d3044393134433132343438383635323539343739304233354543343042373335266c6f67696e6163636f756e743d3138393238333230303037266c6f67696e7077643d313130373236266c6f67696e747970653d322661707076657273696f6e3d332e312e382e312669734175746f4c6f67696e3d35266265737441707049643d44517a66756170613362423078372b434f306a63374c332f65696e683747304d6f56344e797572497450593d26736e3d333833363330333333303338333033323332333533373330333433333332333133383339333233383332653666323161623133362676616c6964617465436f64653d266d617265614335a94178";
		String paramStr = "ip=10.0.2.15&method=user.clientLogin2&esn=&termcode=MI2&timestamp="+m+"&channel=3&v=2.1&format=1&sig="+sig+"&loginaccount="+mobile+"&loginpwd="+pwd+"&logintype=2&appversion=3.1.8.1&isAutoLogin=5&bestAppId=DQzfuapa3bB0x7+CO0jc7L3/einh7G0MoV4NyurItPY=&sn="+sn+"&validateCode=&mareaCode=";
		paramStr = a(paramStr, md5);
		String indexUrl = "http://61.140.99.28:8080/MOService/api?v=2.1&name=jbAClientSp&category=android&imsi="+mobile+"&paramStr="+paramStr;
		con = Jsoup.connect(indexUrl);
		con.header("APIAuthorize-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
		Response indexResponse = con.timeout(30000).method(Method.GET).ignoreContentType(true).followRedirects(true)
				.execute();
		String result = b(indexResponse.body(), md5);
		System.out.println(result);
		JSONObject jsonObject = JSONObject.parseObject(result);
		System.out.println(jsonObject.toJSONString());
		//获取图片验证码
		String imgUrl = "http://61.140.99.28:8080/MOService/validateCode?imsi="+mobile;
		con = Jsoup.connect(imgUrl);
		con.header("APIAuthorize-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
		Response imgResponse = con.timeout(30000).method(Method.GET).ignoreContentType(true).followRedirects(true)
				.execute();
		Response imageRs = con.cookies(cookieMap).ignoreContentType(true).followRedirects(true).method(Method.GET).execute();// 获取响应
		InputStream is = new ByteArrayInputStream(imageRs.bodyAsBytes());
		File file = new File("/Users/cheng/image.jpg");
		OutputStream os = new FileOutputStream(file);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		os.close();
		is.close();
		System.out.println("请输入图片验证码：");
		Scanner messagesc = new Scanner(System.in);
		String messageCode = messagesc.nextLine();

		sn = a(imei+mobile+messageCode);
		para = mobile+"1474733140661"+"3"+"jbAClientSp"+"2.1"+"v:2!2F8H2d&]"+"1"+"android"+"MI2"+sn+messageCode;
		sig = g(para);
		paramStr = "ip=10.111.25.68&method=user.clientLogin2&esn=&termcode=MI2&timestamp="+"1474733140661"+"&channel=3&v=2.1&format=1&sig="+sig+"&loginaccount="+mobile+"&loginpwd="+pwd+"&logintype=2&appversion=3.1.8.1&isAutoLogin=5&bestAppId=IiRF42ycq7YGBENQ0Jum1weoBqQIm8sxl7NllCPqrkQ=&sn="+sn+"&validateCode="+messageCode+"&mareaCrWe=";
		System.out.println("paramStr:"+paramStr);
		paramStr = a(paramStr,md5);
		String loginUrl = domain +paramStr;
		con = Jsoup.connect(loginUrl);
		con.header("APIAuthorize-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
		Response loginResponse = con.timeout(30000).method(Method.GET).ignoreContentType(true).followRedirects(true)
				.execute();
		String loginResult = b(loginResponse.body(), md5);
		JSONObject loginJsonObject = JSONObject.parseObject(loginResult);
		System.out.println(loginJsonObject.toJSONString());
		String sessionkey = loginJsonObject.getJSONObject("response").getString("sessionkey");
		System.out.println("sessionkey:"+sessionkey);


		//取详单
		String paramString3 = "";
		long localLong = System.currentTimeMillis();
		sig=g("jbAClientSp"+mobile+"android"+termcode+sessionkey+localLong+paramString3+"3"+"2.1"+"1"+"v:2!2F8H2d&]");
		paramStr = "method=detail.queryTdetail&esn=&termcode=MI2&sessionkey="+sessionkey+"&timestamp="+localLong+"&password="+pwd+"&sn="+sn+"&listingSign=1&qrytime="+qrytime+"&currentPage=1&pageSize=10000&channel=3&format=1&sig="+sig;
		paramStr = a(paramStr, md5);
		String detailUrl = domain +paramStr;
		con = Jsoup.connect(detailUrl);
		con.header("APIAuthorize-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36");// 配置模拟浏览器
		Response detailResponse = con.timeout(30000).method(Method.GET).ignoreContentType(true).followRedirects(true)
				.execute();
		System.out.println(detailResponse.body());
		String detailResult = b(detailResponse.body(), md5);
		JSONObject detailJsonObject = JSONObject.parseObject(detailResult);
		System.out.println(detailJsonObject.toJSONString());

	}
}
