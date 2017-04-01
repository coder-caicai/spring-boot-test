package com.hbc.api.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AUTH;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommonHttpMethod {



	private static Logger logger = LoggerFactory.getLogger(CommonHttpMethod.class);

	/**
	 * 通用get方法
	 * @param httpGet
	 * @return
	 */
	public static ResponseValue doGet(HttpGet httpGet) {
		//防止自动重定向
		HttpParams params = new BasicHttpParams();
		params.setParameter("http.protocol.handle-redirects", false); 
		httpGet.setParams(params);
		
		String result = "";
		HttpResponse httpResponse;
		ResponseValue resValue = new ResponseValue();



		CloseableHttpClient client = HttpClients.custom().build();


		HttpClientContext context = HttpClientContext.create();
		try {

			//设置代理

			if(StringUtils.isNotBlank(getProxyIp())){
				// 依次是代理地址，代理端口号，协议类型
				String[] ipp = getProxyIp().split(":");
				String ip = ipp[0];
				String prot = ipp[1];

				HttpHost proxy = new HttpHost(ip,Integer.parseInt(prot),"https");
				BasicScheme proxyAuth = new BasicScheme();
				proxyAuth.processChallenge(new BasicHeader(AUTH.PROXY_AUTH, "BASIC realm=default"));
				BasicAuthCache authCache = new BasicAuthCache();
				authCache.put(proxy, proxyAuth);
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(
						new AuthScope(proxy),
						new UsernamePasswordCredentials("sundechong", "6bem5bau"));
				context.setAuthCache(authCache);
				context.setCredentialsProvider(credsProvider);

				RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
				httpGet.setConfig(config);
			}



//			long a = System.currentTimeMillis();
//			HttpHost proxy = new HttpHost("122.72.32.72", 80, "http");
//			long b = System.currentTimeMillis();
//			logger.info("代理耗时:" + (b-a));
//			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//			httpGet.setConfig(config);

			httpGet.setHeader("APIAuthorize-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36");
			httpGet.setHeader("Content-Type", "text/html; charset=utf-8");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");

			httpResponse = client.execute(httpGet, context);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == 200 || code == 302) {
				// get response cookies
				CookieStore cookieStore = context.getCookieStore();
				List<Cookie> cookies = cookieStore.getCookies();
				if (cookies != null) {
					resValue.setCookies(cookies);
				}

				// get 302 location
				Header[] hs = httpResponse.getAllHeaders();
				for (Header h : hs) {
					if (h.getName().equals("Location")) {
						System.out.println(h.getValue());
						resValue.setLocation(h.getValue());
					}
				}

				// get response body
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);
				//System.out.println(result);
				resValue.setResponse(result);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (IOException e) {
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (MalformedChallengeException e) {
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
		return resValue;
	}
	
	/**
	 * 通用post方法
	 * @param postRequest
	 * @return
	 */
	public static ResponseValue doPost(HttpPost postRequest) {

		ResponseValue response = new ResponseValue();
		postRequest.setHeader("APIAuthorize-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36");
		postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		postRequest.setHeader("Accept-Encoding", "gzip, deflate");
		postRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");

		//设置代理
//		HttpHost proxy = new HttpHost("119.48.176.140", 8118, "http");
//		long a = System.currentTimeMillis();
//		HttpHost proxy = new HttpHost("122.72.32.72", 80, "http");
//		long b = System.currentTimeMillis();
//		logger.info("代理耗时:" + (b-a));
//		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//		postRequest.setConfig(config);

		CloseableHttpClient client = HttpClients.custom().build();
		HttpClientContext context = HttpClientContext.create();
		try {
			HttpResponse httpResponse = client.execute(postRequest, context);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == 200 || code == 302) {
				// get response cookies
				CookieStore cookieStore = context.getCookieStore();
				List<Cookie> cookies = cookieStore.getCookies();
				if (cookies != null) {
					response.setCookies(cookies);
				}

				Header[] hs = httpResponse.getAllHeaders();
				for (Header h : hs) {
					if (h.getName().equals("Location")) {
						System.out.println(h.getValue());
						response.setLocation(h.getValue());
					}
				}

				HttpEntity httpEntity = httpResponse.getEntity();
				String result = EntityUtils.toString(httpEntity);
				//System.out.println(result);
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
	
	/**
	 * 通用get方法(没有任何头参数)
	 * @param httpGet
	 * @return
	 */
	public static ResponseValue doGetWithNoParams(HttpGet httpGet) {
		//防止自动重定向
		HttpParams params = new BasicHttpParams();
		params.setParameter("http.protocol.handle-redirects", false); 
		httpGet.setParams(params);
		
		String result = "";
		HttpResponse httpResponse;
		ResponseValue resValue = new ResponseValue();


		CloseableHttpClient client = HttpClients.custom().build();
		HttpClientContext context = HttpClientContext.create();
		try {

//			HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
//			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
//			httpGet.setConfig(config);

//			httpGet.setHeader("APIAuthorize-Agent",
//					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36");
//			httpGet.setHeader("Content-Type", "text/html; charset=utf-8");
//			httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
//			httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");

			httpResponse = client.execute(httpGet, context);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == 200 || code == 302) {
				// get response cookies
				CookieStore cookieStore = context.getCookieStore();
				List<Cookie> cookies = cookieStore.getCookies();
				if (cookies != null) {
					resValue.setCookies(cookies);
				}

				// get 302 location
				Header[] hs = httpResponse.getAllHeaders();
				for (Header h : hs) {
					if (h.getName().equals("Location")) {
						System.out.println(h.getValue());
						resValue.setLocation(h.getValue());
					}
				}

				// get response body
				HttpEntity httpEntity = httpResponse.getEntity();
				result = EntityUtils.toString(httpEntity);
				//System.out.println(result);
				resValue.setResponse(result);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = e.getMessage().toString();
		} catch (IOException e) {
			e.printStackTrace();
			result = e.getMessage().toString();
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return resValue;
	}
	
	/**
	 * 通用post方法
	 * @param postRequest
	 * @return
	 */
	public static ResponseValue doPostSSL(HttpPost postRequest) {

		ResponseValue response = new ResponseValue();

		postRequest.setHeader("Accept-Encoding", "gzip, deflate");
		postRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		CloseableHttpClient client = createSSLClientDefault();
		if(StringUtils.isNotBlank(getProxyIp())){
			// 依次是代理地址，代理端口号，协议类型
			String[] ipp = getProxyIp().split(":");
			String ip = ipp[0];
			String prot = ipp[1];
			HttpHost proxy = new HttpHost(ip,Integer.parseInt(prot),"https");
			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			postRequest.setConfig(config);
		}
		HttpClientContext context = HttpClientContext.create();
		try {
			HttpResponse httpResponse = client.execute(postRequest, context);
			
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == 200 || code == 302) {
				// get response cookies
				CookieStore cookieStore = context.getCookieStore();
				List<Cookie> cookies = cookieStore.getCookies();
				if (cookies != null) {
					response.setCookies(cookies);
				}

				Header[] hs = httpResponse.getAllHeaders();
				for (Header h : hs) {
					if (h.getName().equals("Location")) {
						System.out.println(h.getValue());
						response.setLocation(h.getValue());
					}
				}

				HttpEntity httpEntity = httpResponse.getEntity();
				String result = EntityUtils.toString(httpEntity);
				//System.out.println(result);
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
    
	/**
	 * 通用post方法
	 * @param postRequest
	 * @return
	 */
	public static ResponseValue doPostSSLUID(HttpPost postRequest) {

		ResponseValue response = new ResponseValue();
		String UID = "";
		
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
				UID = header[0].getValue().split("UID=")[1];
				UID = UID.split(";")[0];
				System.out.println("UID: " + UID);
				
				if (cookies != null) {
					response.setCookies(cookies);
				}

				Header[] hs = httpResponse.getAllHeaders();
				for (Header h : hs) {
					if (h.getName().equals("Location")) {
						System.out.println(h.getValue());
						response.setLocation(h.getValue());
					}
				}

				HttpEntity httpEntity = httpResponse.getEntity();
				String result = EntityUtils.toString(httpEntity);
				//System.out.println(result);
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
	
    public static CloseableHttpClient createSSLClientDefault(){
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
         return  HttpClients.createDefault();
    }

	private static String getProxyIp(){
		Connection con = null;
		String httpProxyUrl = "http://dps.kuaidaili.com/api/getdps/?orderid=999033723725968&num=1&ut=1&sep=1";
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
