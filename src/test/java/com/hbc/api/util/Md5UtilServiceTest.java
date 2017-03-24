package com.hbc.api.util;

import com.hbc.api.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.mail.MessagingException;

/**
 * Created by cheng on 16/10/31.
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class Md5UtilServiceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Md5Util md5Util;

    @Test
    public void test() throws MessagingException {
        String text = md5Util.string2MD5("$2a$11$XzLSS3.I7KL2si.oFg7p4OySizsJY1xii5z9b4iEDx0BsavXqoKQ.");
        logger.info(text);
    }

}
