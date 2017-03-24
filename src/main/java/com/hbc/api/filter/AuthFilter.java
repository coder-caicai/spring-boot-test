package com.hbc.api.filter;

import com.alibaba.fastjson.JSON;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.service.AuthService;
import com.hbc.api.util.AesUtil;
import com.hbc.api.util.AuthUtil;
import com.hbc.api.util.EncryptUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by cheng on 16/7/21.
 */

/**
 * 使用注解标注过滤器
 *
 * @WebFilter将一个实现了javax.servlet.Filter接口的类定义为过滤器 属性filterName声明过滤器的名称, 可选
 * 属性urlPatterns指定要过滤 的URL模式,也可使用属性value来声明.(指定要过滤的URL模式是必选属性)
 */
//@WebFilter(filterName="myFilter",urlPatterns="/*")
@Component("authFilter")
public class AuthFilter implements Filter {


    private Logger logger = LoggerFactory.getLogger(getClass());

    private String key = "e28a4f2d9eb6aa3a2f3484cb368a73e9";

    @Autowired
    private AuthService authService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ResultDto resultDto = new ResultDto();
        if (httpRequest.getRequestURI().startsWith("/auth")) {
            doParameter(request);
            chain.doFilter(request, response);
        }else if(httpRequest.getRequestURI().endsWith(".js") ||httpRequest.getRequestURI().endsWith(".png") ){
            chain.doFilter(request, response);
        }else if(httpRequest.getRequestURI().startsWith("/aes")){
            chain.doFilter(request, response);
        }else if(httpRequest.getRequestURI().startsWith("/test")){
            chain.doFilter(request, response);
        } else {
            doParameter(request);
            String token = request.getAttribute("accessToken")==null?null:request.getAttribute("accessToken").toString();
            Map<String, Object> authMap = authService.verifyUser(token);
            if (authMap == null) {
                resultDto.setStatus(EnumResultStatus.ERROR_TOKEN);
                resultDto.setMsg(EnumResultStatus.ERROR_TOKEN.getName());
                response.getWriter().write(JSON.toJSONString(resultDto));
                return;
            } else if (!AuthUtil.verifyTokenExpire(token)) {
                resultDto.setStatus(EnumResultStatus.ERROR_TOKEN_EXPIRED);
                resultDto.setMsg(EnumResultStatus.ERROR_TOKEN_EXPIRED.getName());
                response.getWriter().write(JSON.toJSONString(resultDto));
                return;
            } else {
                Map<String, Object> map = AuthUtil.verify(token);
                Integer clientId = map.get("clientId") == null ? null : Integer.parseInt(map.get("clientId").toString());
                if (clientId == null) {
                    logger.error("解析token出错没有获得clientId。");
                    resultDto.setStatus(EnumResultStatus.ERROR_TOKEN);
                    resultDto.setMsg(EnumResultStatus.ERROR_TOKEN.getName());
                    response.getWriter().write(JSON.toJSONString(resultDto));
                    return;
                } else {
                    request.setAttribute("clientId", clientId);
                    chain.doFilter(request, response);
                }
            }
        }
    }

    @Override
    public void destroy() {

    }

    private void doParameter(ServletRequest request){
        String data = request.getParameter("data");
        if (StringUtils.isNotBlank(data)) {
            data = data.replace(" ","+");
            String para = null;
            try {
                logger.info("加密数据:"+data);
                para = EncryptUtils.aesDecrypt(data);
                logger.info("解密数据:"+para);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return ;
            }
            String[]  parameters = para.split("&");
            for (String parameter : parameters) {
                String[] keyValue = parameter.split("=");
                if(keyValue.length == 2){
                    request.setAttribute(keyValue[0],keyValue[1]);
                }else {
                    request.setAttribute(keyValue[0],null);
                }
            }
        }else{
//            logger.error("传参有误!");
        }
    }


}


