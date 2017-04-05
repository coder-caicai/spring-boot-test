package com.hbc.api.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by cheng on 2017/2/13.
 */
@Component
public class MailUtil {

    protected static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${ip.address}")
    private String address;

    @Value("${ip.toMail}")
    private String toMail;


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


    public void sendIpEmail()  {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("caichengzhang@hbc315.com");
            helper.setTo(toMail);
            helper.setSubject("爬虫项目服务器ip报警");
            StringBuilder sb  = new StringBuilder("<body>");
            sb.append("该服务器ip出现异常,请及时更换ip,ip信息如下:<br>");
            Enumeration<NetworkInterface> netInterfaces = null;
            try {
                netInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            String ipStr = null;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ia = ni.getInetAddresses();
                while (ia.hasMoreElements()) {
                    InetAddress ip = ia.nextElement();
                    sb.append(ip.getHostAddress());
                    sb.append("<br>");
                    if(ip.getHostAddress().contains("192.")){
                        ipStr = ip.getHostAddress()+" "+DateUtil.sdfYYYY_MM_DD_HH_mm_ss.format(new Date());
                    }
                }
            }
            sb.append("</body>");
            helper.setText(sb.toString(), true);
            writeIp(ipStr);
            mailSender.send(message);
            logger.info("html邮件已经发送。");
        } catch (MessagingException e) {
            logger.error("发送html邮件时发生异常！", e);
        }
    }

    public void writeIp(String ip){
        File file = new File(address, "ip.txt");
        try {
            file.createNewFile(); // 创建文件
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 向文件写入内容(输出流)
        byte bt[] = new byte[1024];
        bt = ip.getBytes();
        try {
            FileOutputStream in = new FileOutputStream(file);
            try {
                in.write(bt, 0, bt.length);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
