package com.hbc.api.util;

import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.fastjson.JSON;
import com.hbc.api.Application;
import com.sun.javafx.collections.MappingChange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Map;

/**
 * Created by cheng on 16/10/31.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class AuthUtilServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void createAuth(){
        String salt  = BCrypt.gensalt(11);
        String pwd = BCrypt.hashpw("200010",salt);
        logger.info(pwd);
    }

    @Test
    public  void verify (){
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjb21wYW55IjoidGVzdCIsImNsaWVudElkIjoyMDAwMDQsImV4cGlyZSI6MTQ4MjU2MjgwMDAwMH0.TgxEJFhF-dBVwuV4k0bzFauiG2Z-443dXlYHpbIgXdk";
        Map<String,Object>  map = AuthUtil.verify(token);
        logger.info(JSON.toJSONString(map));
    }
}
