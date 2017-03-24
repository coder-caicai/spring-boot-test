package com.hbc.api.util;

import com.alibaba.fastjson.JSON;
import com.hbc.api.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.mail.MessagingException;
import java.util.Map;

/**
 * Created by cheng on 16/10/31.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class MailUtilServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MailUtil mailUtil;

    @Test
    public void send() throws MessagingException {
        mailUtil.sendHtmlEmail("bj_ccz@163.com",200008,"$2a$11$bC6qltsIiR6t44PkA7fqGe4xlDRKxjhGb.7K678xeEKodGm2pe6w6");
    }

}
