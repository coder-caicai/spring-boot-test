package com.hbc.api.spider;

import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.hbc.api.util.DESedeCoder;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DxClientSpider {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	private static String url = "http://cservice.client.189.cn:8004/map/clientXML/?encrypted=true";
//	private static String Token = "";
//	private static String mobile = "18911206086";
//	private static String pwd = "989977";
//	private static String startDate = "20160720";
//	private static String endDate = "20160721";
	
	public static String login(String mobile,String pwd) throws Exception{
		String data = "<Request>"
						+ "<Content>"
							+ "<FieldData>"
								+ "<PswType>01</PswType>"
								+ "<DeviceToken>01dc409d5f6d2f78acf6e4964d25998cdc1a9c7b95f3a852dcabbea446ad192d</DeviceToken>"
								+ "<AccountType>c2000004</AccountType>"
								+ "<PhoneNbr>"+mobile+"</PhoneNbr>"
								+ "<PhonePsw>"+pwd+"</PhonePsw>"
							+ "</FieldData>"
							+ "<Attach>iPhone</Attach>"
						+ "</Content>"
						+ "<HeaderInfos>"
							+ "<Source>120002</Source>"
							+ "<UserLoginName>"+mobile+"</UserLoginName>"
							+ "<Code>loginInfo</Code>"
							+ "<Token></Token>"
							+ "<Timestamp>"+sdf.format(new Date())+"</Timestamp>"
							+ "<ClientType>#5.5.0#channel50#iPhone 6 Plus#</ClientType>"
							+ "<SourcePassword>TiqmIZ</SourcePassword>"
						+ "</HeaderInfos>"
					+ "</Request>";
		String result = httpUtil(data);
		SAXReader saxReader = new SAXReader(); 
		Reader reader = new StringReader(result);
		Document document = saxReader.read(reader); 
		Element responseElement = document.getRootElement();
		Map<String,String> map = new HashMap<>();
		for(Iterator i = responseElement.elementIterator(); i.hasNext();){ 
			Element HeaderInfos = (Element) i.next(); 
			map.put(HeaderInfos.getName(), HeaderInfos.getText());
			for(Iterator j = HeaderInfos.elementIterator(); j.hasNext();){ 
				Element ResponseData=(Element) j.next();
				map.put(ResponseData.getName(), ResponseData.getText());
				for(Iterator k = ResponseData.elementIterator(); k.hasNext();){
					Element Data =(Element) k.next(); 
					map.put(Data.getName(), Data.getText());
					for(Iterator h = Data.elementIterator(); h.hasNext();){
						Element init =(Element) h.next(); 
						map.put(init.getName(), init.getText());
					}
				}
			}
		}
		String token = map.get("Token");
		msg(mobile,token);
		return token;
	}
	
	public static void msg(String mobile,String token) throws Exception{
		String data = "<Request>"
						+ "<Content>"
							+ "<FieldData>"
								+ "<PhoneNbr>"+mobile+"</PhoneNbr>"
							+ "</FieldData>"
							+ "<Attach>iPhone</Attach>"
						+ "</Content>"
						+ "<HeaderInfos>"
							+ "<Source>120002</Source>"
							+ "<UserLoginName>"+mobile+"</UserLoginName>"
							+ "<Code>getRandomV2</Code>"
							+ "<Token>"+token+"</Token>"
							+ "<Timestamp>"+sdf.format(new Date())+"</Timestamp>"
							+ "<ClientType>#5.5.0#channel50#iPhone 6 Plus#</ClientType>"
							+ "<SourcePassword>TiqmIZ</SourcePassword>"
						+ "</HeaderInfos>"
					+ "</Request>";
		String result = httpUtil(data);
	}
	
	public static void spider(String mobile,String token,String msg,String startDate,String endDate) throws Exception {
//		System.out.println("请输入短信验证码：");
//        Scanner messagesc = new Scanner(System.in);
//        String messageCode = messagesc.nextLine();
		String data = "<Request>"
						+ "<Content>"
							+ "<FieldData>"
								+ "<PhoneNum>"+mobile+"</PhoneNum>"
								+ "<Type>1</Type>"
								+ "<StartTime>"+startDate+"</StartTime>"
								+ "<EndTime>"+endDate+"</EndTime>"
								+ "<Random>"+msg+"</Random>"
							+ "</FieldData>"
							+ "<Attach>iPhone</Attach>"
						+ "</Content>"
						+ "<HeaderInfos>"
							+ "<Source>120002</Source>"
							+ "<UserLoginName>"+mobile+"</UserLoginName>"
							+ "<Code>jfyBillDetail</Code>"
							+ "<Token>"+token+"</Token>"
							+ "<Timestamp>"+sdf.format(new Date())+"</Timestamp>"
							+ "<ClientType>#5.5.0#channel50#iPhone 6 Plus#</ClientType>"
							+ "<SourcePassword>TiqmIZ</SourcePassword>"
						+ "</HeaderInfos>"
					+ "</Request>";
		String result = httpUtil(data);
		SAXReader saxReader = new SAXReader(); 
		Reader reader = new StringReader(result);
		Document document = saxReader.read(reader); 
		Element Response = document.getRootElement();
		List<Element> Responses =  Response.elements();
		Element ResponseData = Responses.get(1);
		List<Element> ResponseDatas = ResponseData.elements();
		Element Data = ResponseDatas.get(3);
		List<Element> ItemsList = Data.elements();
		Element Items = ItemsList.get(0);
		List<Element> itemList = Items.elements();
		System.out.println("CallType:"+itemList.get(0).elementText("CallType"));
		System.out.println("CallMobile:"+itemList.get(0).elementText("CallMobile"));
		System.out.println("CallTime:"+itemList.get(0).elementText("CallTime"));
		System.out.println("CallTimeCost:"+itemList.get(0).elementText("CallTimeCost"));
		System.out.println("CallStyle:"+itemList.get(0).elementText("CallStyle"));
		System.out.println("CallArea:"+itemList.get(0).elementText("CallArea"));
		System.out.println("CallFee:"+itemList.get(0).elementText("CallFee"));
	}
	public static String httpUtil(String data) throws Exception{
		data = DESedeCoder.doEncryptData(data);
		HttpClient httpclient = new HttpClient();
		PostMethod post = new PostMethod(url);
		String info = null;
		try {
			RequestEntity entity = new StringRequestEntity(data, "text/xml", "iso-8859-1");
			post.setRequestEntity(entity);
			httpclient.executeMethod(post);
			int code = post.getStatusCode();
			if (code == HttpStatus.SC_OK)
				info = new String(post.getResponseBodyAsString());
			    String result = DESedeCoder.doDecryptData(info);
		        System.out.println("解密后数据: string:"+result); 
		        return result;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return null;
	}

//	public static void main(String[] args){
//		String num = "17762271294";
//		String result2 =  DESedeCoder.doEncryptDataBase64(num);
//		System.out.println(result2);
//		String result = DESedeCoder.doDecryptData("b5720f816f50db5eb94116fd795b9f770f4af1f252692aa8c138f0e8150856db0b52b7c8000a7be699aabc4ab106f380f9e488a10e8269792beb5b46a667cdf32e20cf7649e74841dcfc49d871e100bda5b005efdca1abf6d8f95b802b6db01dca73824a025160ffad8e144ec4284962f2455cea7f42495b600b7387ca79e1939858163c0f554c68a780f7c852b8e6d1c4d3a572bf1bf77a56a0a172ed0ad1f19b324ad60e91a5cc0911713e7105b44082544a6f109bc80c83965874e2b6b5f7adc8f72c875d46949d8536a8b9ca013295d59af788b9c9eb");
//		System.out.println(result);
//	}
}
