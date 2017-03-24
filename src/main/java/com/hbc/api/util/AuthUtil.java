package com.hbc.api.util;


import com.hbc.api.auth.JWTSigner;
import com.hbc.api.auth.JWTVerifier;
import com.hbc.api.auth.JWTVerifyException;
import com.hbc.api.model.ApiAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证处理公共类
 * 
 * @author lichunhua
 *
 */

public class AuthUtil {

	// 密钥
	private static final String SECRET;

	private static Logger logger = LoggerFactory.getLogger(AuthUtil.class);

	// 密钥初始化
	static {
		//SECRET = "love my baby";
		SECRET = "sF*e3)%g@E8^h2N=r";
	}


	// 证书生成对象
	private static JWTSigner signer = new JWTSigner(SECRET);

	// 证书解析对象
	private static JWTVerifier verifier = new JWTVerifier(SECRET);

	/**
	 * 生成token
	 * 
	 * @param
	 * @return token
	 */
	public  String getToken(ApiAuthorize authorize) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 24);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 000);
		HashMap<String, Object> claims = new HashMap<String, Object>();
		claims.put("clientId", authorize.getId());
		claims.put("company", authorize.getCompany());
		claims.put("expire", cal.getTime());
		String token = signer.sign(claims);
		return token;
	}
	

	/**
	 * 验证token是否合法
	 * 
	 * @param token
	 *            token
	 * @return 返回用户名
	 */
	public static Map<String, Object> verifyTokenUser(String token) {
		Map<String, Object> decoded = null;

		try {
			decoded = verifier.verify(token);
		} catch (Exception e) {
			return decoded;
		}

		return decoded;

	}

	/**
	 * 验证token过期时间
	 * 
	 * @param token
	 *            要验证的token
	 * @return 是否过期
	 */
	public static boolean verifyTokenExpire(String token) {
		Map<String, Object> decoded;
		Calendar cal = Calendar.getInstance();
		try {
			decoded = verifier.verify(token);
		} catch (Exception e) {
			return false;
		}
		if (decoded.get("expire") == null)
			return false;
		cal.setTimeInMillis(Long.valueOf(decoded.get("expire").toString()));
		Date expire = cal.getTime();
		if (expire.after(new Date())) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 验证IP是否合法
	 * 
	 * @param token
	 *            token
	 * @return 返回合法IP列表
	 */
	public static boolean verifyTokenIP(String token, HttpServletRequest request) {
		Map<String, Object> decoded;
		try {
			decoded = verifier.verify(token);
		} catch (Exception e) {
			return false;
		}

		String ip = null;
		String requestIP = getIpAddr(request);
		
		if (decoded.get("ipAddrs") != null)
			ip = decoded.get("ipAddrs").toString();

		if (ip.equals("*"))
			return true;
		else {
			String[] ipList = ip.split(",");
			for (String ipAddress : ipList) {
				if (ipAddress.equals(requestIP))
					return true;
			}
		}

		return false;

	}

	
	private static String getIpAddr(HttpServletRequest request) {  
	    String ip = request.getHeader("x-forwarded-for");  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("PRoxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getHeader("WL-Proxy-Client-IP");  
	    }  
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	        ip = request.getRemoteAddr();  
	    }  
	    return ip;  
	}

	/**
	 * 解析证书
	 * @param token
	 * @return
     */
	public static Map<String,Object> verify(String token){
		Map<String, Object> decoded = null;
		try {
			decoded = verifier.verify(token);
		} catch (Exception e) {
			logger.error("解析token出错:"+e.getMessage());
		}
		return decoded;
	}



	/**
	 * 返回用户是否是合法用户
	 * 
	 * @param user
	 *            用户对象
	 * @return 是否合法
	 */
//	public static boolean VerifyUser(AuthUser user) {
//
//		if (user != null) {
//			//TODO
//			return true;
//		} else {
//			return false;
//		}
//	}

}
