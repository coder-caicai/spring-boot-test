package com.hbc.api.controller;

import com.hbc.api.util.EncryptUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * Created by cheng on 16/9/13.
 */

@Controller
@RequestMapping("/aes")
public class AesController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("")
    public String index(HttpServletRequest request){
        return "aes";
    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    public String indexPost(HttpServletRequest request){
        logger.info("remoteAddr:"+request.getRemoteAddr());
        String ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isNotBlank(ip)){
            logger.info("x-forwarded-for:"+ip);
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
            logger.info("Proxy-Client-IP:"+ip);
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
            logger.info("WL-Proxy-Client-IP:"+ip);

        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
            logger.info("aa:"+ip);
        }
        return "aes";
    }

    @RequestMapping(value = "encrypt",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String encrypt(String para) throws Exception {
        return EncryptUtils.aesEncrypt(para);
    }

    @RequestMapping(value = "decrypt",produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String decrypt(String para) throws Exception {
        return EncryptUtils.aesDecrypt(para);
    }




    @RequestMapping(value = "/test",method = RequestMethod.POST)
    @ResponseBody
    public String test(HttpServletRequest request) throws Exception {
        byte buffer[] = getRequestPostBytes(request);
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = "UTF-8";
        }
        String str = new String(buffer, charEncoding);
        logger.info("###########:"+str);
        return "success";
    }
    public  byte[] getRequestPostBytes(HttpServletRequest request)
            throws Exception {
        int contentLength = request.getContentLength();
        if(contentLength<0){
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {

            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

}
