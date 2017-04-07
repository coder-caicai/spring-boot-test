package com.hbc.api.service;

import com.hbc.api.mapper.APIAuthorizeMapper;
import com.hbc.api.model.ApiAuthorize;
import com.hbc.api.util.AuthUtil;
import com.hbc.api.util.DateUtil;
import com.hbc.api.util.MailUtil;
import com.hbc.api.util.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录验证 	login
 * 验证token 	verifyUser
 *
 * @author cyzhao
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private APIAuthorizeMapper apiAuthorizeMapper;

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private Md5Util md5Util;

    public boolean createAuth(String company, String mail) throws MessagingException {
        if (StringUtils.isNotBlank(company)) {
            ApiAuthorize apiAuthorize = new ApiAuthorize();
            apiAuthorize.setCompany(company);
            apiAuthorize.setStatus(0);
            apiAuthorize.setCreateDate(DateUtil.sdfYYYY_MM_DD_HH_mm_ss.format(new Date()));
            String salt = BCrypt.gensalt(11);
            String pwd = BCrypt.hashpw(company, salt);
            String md5pwd = md5Util.string2MD5(pwd);
            apiAuthorize.setSecret(md5pwd);
            apiAuthorize.setRemark("新开账户");
            apiAuthorizeMapper.insertUseGeneratedKeys(apiAuthorize);
            logger.info(company+"新开账号:"+pwd);
            mailUtil.sendHtmlEmail(mail, apiAuthorize.getId(), pwd);
            return true;
        } else {
            return false;
        }
    }


    public HashMap<String, String> login(String clientId, String clientSecret) {
        HashMap<String, String> dataMap = new HashMap<String, String>();
        ApiAuthorize apiAuthorize = new ApiAuthorize();
        apiAuthorize.setId(Integer.parseInt(clientId));
        ApiAuthorize auth = apiAuthorizeMapper.selectOne(apiAuthorize);
        //验证clientId是否存在
        if (auth == null) {
            logger.error("根据clientId,查询Authorize失败!");
            return dataMap;
        }

        if(!md5Util.string2MD5(clientSecret).equals(auth.getSecret())){
            logger.error("auth:"+auth.getSecret());
            logger.error("user 传入的clientSecret: " + clientSecret);
            logger.error("authorize 数据库中传入的clientSecret: " + auth.getSecret());
            logger.error("BCrypt验证密码失败!");
            return dataMap;
        }

        //验证密码BCrypt
//        if (!BCrypt.checkpw(clientId, clientSecret)) {
//            logger.error("user 传入的clientSecret: " + clientSecret);
//            logger.error("authorize 数据库中传入的clientSecret: " + auth.getSecret());
//            logger.error("密码验证密码失败!");
//            return dataMap;
//        }

        //验证账号是否停用
        if(auth.getStatus().equals(1)){
            logger.error("user 传入的clientSecret: " + clientSecret);
            logger.error("authorize 数据库中传入的clientSecret: " + auth.getSecret());
            logger.error("账号已经停用");
            return dataMap;
        }

        //身份验证成功，生成token
        String token = createToken(auth);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 000);
        Long expire = new Timestamp(cal.getTimeInMillis()).getTime();
        dataMap.put("access_token", token);
        dataMap.put("expire_at", expire.toString());
        return dataMap;
    }

    //生成token
    private String createToken(ApiAuthorize authorize) {
        String token = "";
        AuthUtil authUtil = new AuthUtil();
        token = authUtil.getToken(authorize);
        return token;
    }

    //验证token
    public Map<String, Object> verifyUser(String token) {
        return AuthUtil.verifyTokenUser(token);
    }
//
//    public static void main(String[] args){
//        Md5Util md5Util = new Md5Util();
//        String md5 = md5Util.string2MD5("$2a$11$15fkacD6UhJnQJxB2bma.OxJp1WyG7U4T42TW6PWKz1WC5d0aq7mW");
//        String mm = "b2128900d5f4750ff66810e293aca647";
//        System.out.println(md5);
//    }

}