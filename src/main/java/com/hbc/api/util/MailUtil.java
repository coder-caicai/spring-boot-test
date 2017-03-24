package com.hbc.api.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

/**
 * Created by cheng on 2017/2/13.
 */
@Component
public class MailUtil {

    protected static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 修改application.properties的用户，才能发送。
     */
    public void sendSimpleEmail(String toMail,Integer clientId,String sceret){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("caichengzhang@hbc315.com");//发送者.
        String mails = "caichengzhang@hbc315.com";
        if(StringUtils.isNotBlank(toMail)){
            mails = mails+","+toMail;
        }
        message.setTo(mails.split(","));//接收者.
        message.setSubject("汇百川爬虫对接账户");//邮件主题.
        String text = String.format("账号: clientId: %s,   secret: %s",clientId,sceret);
        message.setText(text);//邮件内容.
        mailSender.send(message);//发送邮件
    }

    public void sendHtmlEmail(String toMail,Integer clientId,String sceret) throws javax.mail.MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("caichengzhang@hbc315.com");
        helper.setTo(toMail);
        String mails = "caichengzhang@hbc315.com";
        if(StringUtils.isNotBlank(toMail)){
            mails = mails+","+toMail;
        }
        helper.setTo(mails.split(","));
        helper.setSubject("汇百川爬虫对接账户");
        String text = String.format("<body>" +
                "账号如下:<br>" +
                "clientId:%s<br>" +
                "sceret:%s</body>",clientId,sceret.replace("$","&#36;"));
        helper.setText(text, true);
//        FileSystemResource file = new FileSystemResource(new File("D:/test/head/head1.jpg"));
//        helper.addInline("head",file);
        mailSender.send(message);//发送邮件
    }
}
