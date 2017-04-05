package com.hbc.api.controller;

import com.alibaba.fastjson.JSON;
import com.hbc.api.common.EnumResultStatus;
import com.hbc.api.dto.ResultDto;
import com.hbc.api.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Created by ccz.
 */

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseContoller {

	private static Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	AuthService authService;
	@Value("${auth.pwd}")
	private String pwd;


	@RequestMapping(value = "save")
	public String save(Model model){
		model.addAttribute("data","请填写表单信息");
		return "save";
	}

	@RequestMapping(value = "doSave",method = RequestMethod.POST)
	public String doSave(String company, String mail,String passWord, Model model)  {
		if(StringUtils.isNotBlank(passWord) && passWord.equals(pwd)){
			try {
				authService.createAuth(company,mail);
				model.addAttribute("data","创建成功了!");
			} catch (MessagingException e) {
				e.printStackTrace();
				model.addAttribute("data","创建失败了!");
			}
		}else{
			model.addAttribute("data","创建失败了!");
		}
		return "save";
	}

	/**
	 * 取得token
	 */
	@RequestMapping(value = "")
	@ResponseBody
	public String getToken(HttpServletRequest request,String  clientId,String clientSecret) {
		if(StringUtils.isBlank(clientId)){
			clientId = request.getAttribute("clientId").toString();
		}
		if(StringUtils.isBlank(clientSecret)){
			clientSecret = request.getAttribute("clientSecret").toString();
		}
		ResultDto resultDto = new ResultDto();
		HashMap<String, String> dataMap;
		try {
			dataMap= authService.login(clientId,clientSecret);
		} catch (Exception e) {
			StackTraceElement[] error = e.getStackTrace();
			for (StackTraceElement stackTraceElement : error) {
				log.error(stackTraceElement.toString());
			}
			resultDto.setStatus(EnumResultStatus.ERROR);
			resultDto.setMsg(EnumResultStatus.ERROR.getName());
			return  responseStr(JSON.toJSONString(resultDto));
		}
		if (dataMap.get("access_token") != null) {
			resultDto.setStatus(EnumResultStatus.SUCCESS);
			resultDto.setMsg(EnumResultStatus.SUCCESS.getName());
			resultDto.setData(dataMap.get("access_token"));
		} else {
			resultDto.setStatus(EnumResultStatus.ERROR);
			resultDto.setMsg(EnumResultStatus.ERROR.getName());
		}
		return  responseStr(JSON.toJSONString(resultDto));
	}
}