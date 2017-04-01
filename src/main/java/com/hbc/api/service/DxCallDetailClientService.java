package com.hbc.api.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geotmt.client.Client;
import com.google.common.collect.Lists;
import com.hbc.api.dto.AbnormalInfoDTO;
import com.hbc.api.dto.BaseInfoDTO;
import com.hbc.api.dto.BestFriendDTO;
import com.hbc.api.dto.CallDurationDateDTO;
import com.hbc.api.dto.CallTimesAndDurationDTO;
import com.hbc.api.dto.CallTimesDateDTO;
import com.hbc.api.dto.ContactsInfoDTO;
import com.hbc.api.dto.DataDto;
import com.hbc.api.dto.FriendsCityDTO;
import com.hbc.api.dto.MonthlyDTO;
import com.hbc.api.dto.NightInfoDTO;
import com.hbc.api.dto.ReportDTO;
import com.hbc.api.dto.TopDataDto;
import com.hbc.api.mapper.DxCallClientMapper;
import com.hbc.api.mapper.DxCallDetailClientMapper;
import com.hbc.api.mapper.TelephoneNumberMapper;
import com.hbc.api.model.DxCallClient;
import com.hbc.api.model.DxCallDetailClient;
import com.hbc.api.model.TelephoneNumber;
import com.hbc.api.util.DESedeCoder;
import com.hbc.api.util.DateUtil;
import com.hbc.api.util.HttpClientUtil;
import com.hbc.api.util.RedisUtil;
//import com.sun.tools.javac.comp.Todo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ccz
 * @since 2016-07-22 11:09
 */
@Service
public class DxCallDetailClientService {

	private static Logger log = LoggerFactory.getLogger(DxCallDetailClientService.class);

	@Autowired
	private DxCallClientMapper dxCallClientMapper;

	@Autowired
	private DxCallDetailClientMapper dxCallDetailClientMapper;

	@Autowired
	private TelephoneNumberMapper telephoneNumberMapper;

	private static final String server = "http://yz.geotmt.com" ; // http://yz.geotmt.com、https://yz.geotmt.com
	private static final int encrypted = 1 ; // 是否加密传输  1是0否
	private static final String encryptionType = "AES2" ; // AES(秘钥长度不固定)、AES2(秘钥长度16)、DES(秘钥长度8)、DESede(秘钥长度24)、XOR(秘钥只能是数字)
	private static final String encryptionKey = "hbc12345hbc12345" ; // 加密类型和加密秘钥向GEO索取(如果是获取数据的时候传的是RSA那么这里自己定义即可)
	private static final String username = "hbcCrawler" ; // 账户向GEO申请开通
	private static final String password = "crawler1q2w3e4r" ; // GEO提供
	private static final String uno = "200263" ; // GEO提供
	private static final String etype = "" ; // RSA 或 ""
	private static final int dsign = 0 ; // 是否进行数字签名 1是0否

	// 构造客户端(线程安全)
	public static final Client client = new Client();  // 如果接入只是一个账号的话那么该类的构造只需在启动的时候构造一次即可
	static{
		client.setServer(server);
		client.setEncrypted(encrypted);
		client.setEncryptionType(encryptionType);
		client.setEncryptionKey(encryptionKey);
		client.setUsername(username);
		client.setPassword(password);
		client.setUno(uno);
		client.setEtype(etype);
		client.setDsign(dsign);
	}


	//归属地接口地址
	private static String URL="http://localbase.hbc315.com/tel/search?phone=";

	public List<DataDto> getListByMobile(String mobile) {
		List<DxCallClient> dxCallClients = dxCallClientMapper.getListByMobile(mobile);
		if(dxCallClients != null && dxCallClients.size() > 0){
			List<Integer> callIds = new ArrayList<>();
			for(DxCallClient dxCallClient : dxCallClients){
				callIds.add(dxCallClient.getId());
			}
			List<DxCallDetailClient> list = getListByCallIds(callIds);
			List<DataDto> resultList = new ArrayList<>();
			for(DxCallDetailClient dxCallDetailClient : list){
				resultList.add(modileToDto(dxCallDetailClient));
			}
			return resultList;
		}
		return null;
	}

	public List<TopDataDto> getTop10ByMobile(String mobile){
		List<DxCallClient> dxCallClients = dxCallClientMapper.getListByMobile(mobile);
		if(dxCallClients != null && dxCallClients.size() > 0){
			List<Integer> callIds = new ArrayList<>();
			for(DxCallClient dxCallClient : dxCallClients){
				callIds.add(dxCallClient.getId());
			}
			List<DxCallDetailClient> list = dxCallDetailClientMapper.getTop10ByCallId(callIds);
			List<TopDataDto> resultList = new ArrayList<>();
			for(DxCallDetailClient dxCallDetailClient : list){
				TopDataDto dto = new TopDataDto();
				dto.setMobile(dxCallDetailClient.getCallMobile());
				dto.setCallTimes(dxCallDetailClient.getCallTimes());
				resultList.add(dto);
			}
			return resultList;
		}
		return null;
	}
	//通话总次数top10
	public List<CallTimesDateDTO> getCallTimesTop10ByMobile(String mobile){
		List<DxCallClient> dxCallClients = dxCallClientMapper.getListByMobile(mobile);
		if(dxCallClients != null && dxCallClients.size() > 0){
			List<Integer> callIds = new ArrayList<>();
			for(DxCallClient dxCallClient : dxCallClients){
				callIds.add(dxCallClient.getId());
			}
			List<CallTimesDateDTO> list = dxCallDetailClientMapper.getCallTimesTop10ByCallId(callIds);
			return list;
		}
		return null;
	}
	//通话总时长top10
	public List<CallDurationDateDTO> getCallDurationTop10ByMobile(String mobile){
		List<DxCallClient> dxCallClients = dxCallClientMapper.getListByMobile(mobile);
		if(dxCallClients != null && dxCallClients.size() > 0){
			List<Integer> callIds = new ArrayList<>();
			for(DxCallClient dxCallClient : dxCallClients){
				callIds.add(dxCallClient.getId());
			}
			List<CallDurationDateDTO> list = dxCallDetailClientMapper.getCallDurationTop10ByCallId(callIds);
			return list;
		}
		return null;
	}

	//夜间通话总次数
	public CallTimesDateDTO getNightCallTimesByMobile(String mobile){
		CallTimesDateDTO result=new CallTimesDateDTO();
		List<DxCallClient> dxCallClients = dxCallClientMapper.getListByMobile(mobile);
		if(dxCallClients != null && dxCallClients.size() > 0){
			List<Integer> callIds = new ArrayList<>();
			for(DxCallClient dxCallClient : dxCallClients){
				callIds.add(dxCallClient.getId());
			}
			Integer times = dxCallDetailClientMapper.getNightCallTimesByCallId(callIds);
			result.setCallTimes(times);
			result.setMobile(mobile);
			return result;
		}
		return null;
	}
	//夜间通话总时长
	public CallDurationDateDTO getNightCallDurationByMobile(String mobile){
		CallDurationDateDTO result= new CallDurationDateDTO();
		List<DxCallClient> dxCallClients = dxCallClientMapper.getListByMobile(mobile);
		if(dxCallClients != null && dxCallClients.size() > 0){
			List<Integer> callIds = new ArrayList<>();
			for(DxCallClient dxCallClient : dxCallClients){
				callIds.add(dxCallClient.getId());
			}
			Integer duration = dxCallDetailClientMapper.getNightCallDurationByCallId(callIds);
			result.setMobile(mobile);
			result.setCallDuration(duration==null?0:duration);
			return result;
		}
		return null;
	}

	//漫游天数，过去6个月存在漫游通话记录的天数
	public Integer getRoamingDaysByMobile(String mobile){
		List<DxCallClient> dxCallClients = dxCallClientMapper.getListByMobile(mobile);
		if(dxCallClients != null && dxCallClients.size() > 0){
			List<Integer> callIds = new ArrayList<>();
			for(DxCallClient dxCallClient : dxCallClients){
				callIds.add(dxCallClient.getId());
			}
			Integer days = dxCallDetailClientMapper.getRoamingDaysByCallId(callIds);
			return days;
		}
		return null;
	}

	//手机静默情况   无通话记录的天数
	public Integer getSleepingDaysByMobile(String mobile){
		List<DxCallClient> dxCallClients = dxCallClientMapper.getListByMobile(mobile);
		if(dxCallClients != null && dxCallClients.size() > 0){
			List<Integer> callIds = new ArrayList<>();
			for(DxCallClient dxCallClient : dxCallClients){
				callIds.add(dxCallClient.getId());
			}
			Integer days = dxCallDetailClientMapper.getSleepingDaysByCallId(callIds);
			return days;
		}
		return null;
	}

	//获取PDF报告信息
	public ReportDTO getReportInfo(String name,String phoneNum,String idCard,String contact1,String contactNum1,String contact2,String contactNum2){
		log.info("获取PDF报告信息开始");
		ReportDTO report=new ReportDTO();
		List<DxCallClient> dxCallClients = dxCallClientMapper.getListByMobile(phoneNum);
		List<Integer> callIds = new ArrayList<>();
		if(dxCallClients != null && dxCallClients.size() > 0){
			for(DxCallClient dxCallClient : dxCallClients){
				callIds.add(dxCallClient.getId());
			}
		}
		if (callIds.size()==0) {
			return null;
		}
		//获取所有联系人信息
		List<BestFriendDTO> allContactsInfo=dxCallDetailClientMapper.getAllContactsInfoByCallId(callIds);

		String[] called=new String[]{
				"baseInfo","sleepingDays","nightInfo","monthlyInfo","contactsInfo"
		};

		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		final CountDownLatch threadSignal = new CountDownLatch(called.length);
		try {
			for (final String callName : called) {
				cachedThreadPool.execute(new Runnable() {
					@Override
					public void run() {
						switch (callName) {
							// 基本信息核实
							case "baseInfo":
								BaseInfoDTO baseInfo = getBaseInfo(name, phoneNum, idCard);
								report.setBaseInfo(baseInfo);
								break;
							// 手机静默天数
							case "sleepingDays":
								log.info("获取手机静默天数信息开始");
								Long startTime2 = System.currentTimeMillis();
								Integer days = dxCallDetailClientMapper.getSleepingDaysByCallId(callIds);
								report.setSleepDays(days);
								Long cost2= System.currentTimeMillis()-startTime2;
								log.info("获取手机静默天数信息结束 ：耗时"+cost2+"毫秒");
								break;
							// 夜间通话情况
							case "nightInfo":
								log.info("获取夜间通话情况信息开始");
								Long startTime3 = System.currentTimeMillis();
								NightInfoDTO nightInfo = dxCallDetailClientMapper.getNightInfoByCallId(callIds);
								report.setNightInfo(nightInfo);
								Long cost3= System.currentTimeMillis()-startTime3;
								log.info("获取夜间通话情况信息结束 ：耗时"+cost3+"毫秒");
								break;
							// 运营商月度信息统计
							case "monthlyInfo":
								log.info("获取运营商月度信息统计信息开始");
								Long startTime4 = System.currentTimeMillis();
								List<MonthlyDTO> monthlyInfo = getMonthlyInfo(dxCallClients);
								report.setMonthlyDA(monthlyInfo);
								Long cost4= System.currentTimeMillis()-startTime4;
								log.info("获取运营商月度信息统计信息结束 ：耗时"+cost4+"毫秒");
								break;
							// 联系人核验
							case "contactsInfo":
								log.info("获取联系人核验信息开始");
								Long startTime5 = System.currentTimeMillis();
								if (StringUtils.isNotEmpty(contactNum1) || StringUtils.isNotEmpty(contactNum2)) {
									List<ContactsInfoDTO> contactsInfo = getContactsInfo(contact1, contactNum1, contact2,
											contactNum2, callIds, allContactsInfo);
									report.setContactsInfo(contactsInfo);
								}
								Long cost5= System.currentTimeMillis()-startTime5;
								log.info("获取联系人核验信息结束 ：耗时"+cost5+"毫秒");
								break;
						}
						threadSignal.countDown();
					}
				});
			}
			threadSignal.await();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		//归属地及地域
		log.info("获取归属地及地域信息开始");
		Long startTime6 = System.currentTimeMillis();
		dealAreaInfo(allContactsInfo,report);
		Long cost6= System.currentTimeMillis()-startTime6;
		log.info("获取归属地及地域信息结束 ：耗时"+cost6+"毫秒");
		//亲密伙伴TOP10
		report.setBestFriends(allContactsInfo.subList(0, 10));
		//特殊通话
		log.info("获取特殊通话信息开始");
		Long startTime7= System.currentTimeMillis();
		dealAbnormalInfo( report,allContactsInfo);
		Long cost7= System.currentTimeMillis()-startTime7;
		log.info("获取特殊通话 信息结束 ：耗时"+cost7+"毫秒");
		log.info("获取PDF报告信息结束");
		return report;
	}

	//获取PDF报告信息-特殊通话
	public void dealAbnormalInfo(ReportDTO report,List<BestFriendDTO> allContactsInfo) {
		Map<String,BestFriendDTO> allContactsMap=new HashMap<String,BestFriendDTO>();
		for (BestFriendDTO bestFriendDTO : allContactsInfo) {
			allContactsMap.put(bestFriendDTO.getPhone(), bestFriendDTO);
		}
		List<AbnormalInfoDTO> abnormalInfo= new ArrayList<AbnormalInfoDTO>();
		String[] called=new String[]{
				"collection","creditCard","medium","credit","fy"
		};
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		final CountDownLatch threadSignal = new CountDownLatch(called.length);
		try {
			for (final String callName : called) {
				cachedThreadPool.execute(new Runnable() {
					@Override
					public void run() {
						switch (callName) {
							// 催收统计
							case "collection":
								List<TelephoneNumber> collectionList = telephoneNumberMapper.getCollectionTelephoneNumbers(allContactsInfo);
								if (collectionList!=null &&collectionList.size()>0) {
									abnormalInfo.add(getAbnormalInfo(allContactsMap,collectionList,"催收"));
								}
								break;
							// 信用卡统计
							case "creditCard":
								List<TelephoneNumber> creditCardList = telephoneNumberMapper.getCreditCardTelephoneNumbers(allContactsInfo);
								if (creditCardList!=null &&creditCardList.size()>0) {
									abnormalInfo.add(getAbnormalInfo(allContactsMap,creditCardList,"银行"));
								}
								break;
							// 中介统计
							case "medium":
								List<TelephoneNumber> mediumList = telephoneNumberMapper.getCreditMediumTelephoneNumbers(allContactsInfo);
								if (mediumList!=null &&mediumList.size()>0) {
									abnormalInfo.add(getAbnormalInfo(allContactsMap,mediumList,"中介"));
								}
								break;
							// 贷款机构统计
							case "credit":
								List<TelephoneNumber> creditList = telephoneNumberMapper.getCreditTelephoneNumbers(allContactsInfo);
								if (creditList!=null &&creditList.size()>0) {
									abnormalInfo.add(getAbnormalInfo(allContactsMap,creditList,"机构"));
								}
								break;
							// 法院统计
							case "fy":
								List<TelephoneNumber> fyList = telephoneNumberMapper.getFyTelephoneNumbers(allContactsInfo);
								if (fyList!=null &&fyList.size()>0) {
									abnormalInfo.add(getAbnormalInfo(allContactsMap,fyList,"法院"));
								}
								break;
						}
						threadSignal.countDown();
					}
				});
			}
			threadSignal.await();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		report.setAbnormalInfo(abnormalInfo);
	}

	private AbnormalInfoDTO getAbnormalInfo(Map<String,BestFriendDTO> allContactsMap,List<TelephoneNumber> list,String name) {
		AbnormalInfoDTO abnormalInfo = new AbnormalInfoDTO();
		abnormalInfo.setAbnormalType(name);
		abnormalInfo.setCalledTimes(0);
		abnormalInfo.setCallTimes(0);
		abnormalInfo.setTalkDuration(0);
		abnormalInfo.setTalkTimes(0);
		for (TelephoneNumber telephoneNumber : list) {
			BestFriendDTO friendDTO=allContactsMap.get(telephoneNumber.getTelephone());
			abnormalInfo.setCalledTimes(abnormalInfo.getCalledTimes()+friendDTO.getCalledTimes());
			abnormalInfo.setCallTimes(abnormalInfo.getCallTimes()+friendDTO.getCallTimes());
			abnormalInfo.setTalkDuration(abnormalInfo.getTalkDuration()+friendDTO.getTalkDuration());
			abnormalInfo.setTalkTimes(abnormalInfo.getTalkTimes()+friendDTO.getTalkTimes());
		}
		return abnormalInfo;
	}


	//获取PDF报告信息-基本信息核实
	public BaseInfoDTO getBaseInfo(String name,String phoneNum,String idCard) {
		BaseInfoDTO baseInfoDTO=new BaseInfoDTO();
		baseInfoDTO.setName(name);
		baseInfoDTO.setIdCard(idCard);
		baseInfoDTO.setPhone(phoneNum);
		//会员等级 TODO
		baseInfoDTO.setMemberLevel("未知");
		//入网时间 TODO
		baseInfoDTO.setOpenTime("未知");
		//入网时间 TODO
		baseInfoDTO.setRealName("未知");
		return baseInfoDTO;
	}

	//获取PDF报告信息-运营商数据分析
	public List<MonthlyDTO> getMonthlyInfo(List<DxCallClient> dxCallClients) {
		List<MonthlyDTO> list= new ArrayList<MonthlyDTO>();
		for (DxCallClient client :dxCallClients) {
			MonthlyDTO monthlyDTO=new MonthlyDTO();
			monthlyDTO.setMonth(client.getCallDate());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("callId", client.getId());
			map.put("type", "0");
			CallTimesAndDurationDTO callInfo=dxCallDetailClientMapper.getCallTimesAndDurationByCallIdAndType(map);
			map.put("type", "1");
			CallTimesAndDurationDTO calledInfo=dxCallDetailClientMapper.getCallTimesAndDurationByCallIdAndType(map);
			monthlyDTO.setCallDuration(callInfo==null||callInfo.getCallDuration()==null?0:callInfo.getCallDuration());
			monthlyDTO.setCallTimes(callInfo==null||callInfo.getCallTimes()==null?0:callInfo.getCallTimes());
			monthlyDTO.setCalledDuration(calledInfo==null||calledInfo.getCallDuration()==null?0:calledInfo.getCallDuration());
			monthlyDTO.setCalledTimes(calledInfo==null||calledInfo.getCallTimes()==null?0:calledInfo.getCallTimes());
			list.add(monthlyDTO);
		}
		list.sort((MonthlyDTO x,MonthlyDTO y) -> Integer.valueOf(x.getMonth()).compareTo( Integer.valueOf(y.getMonth())));
		return list;
	}
	//获取PDF报告信息-联系人核验
	public List<ContactsInfoDTO> getContactsInfo(String contact1,String contactNum1,String contact2,String contactNum2,List<Integer> callIds,List<BestFriendDTO> allContactsInfo) {
		List<ContactsInfoDTO> contactsInfo=new ArrayList<ContactsInfoDTO>();
		if (StringUtils.isNotEmpty(contactNum1)) {
			contactsInfo.add(getContactInfo(contact1, contactNum1,callIds, allContactsInfo));
		}
		if (StringUtils.isNotEmpty(contactNum2)) {
			contactsInfo.add(getContactInfo(contact2, contactNum2,callIds, allContactsInfo));
		}

		return contactsInfo;
	}
	//联系人信息获取
	private ContactsInfoDTO getContactInfo(String name, String phone,List<Integer> callIds,List<BestFriendDTO> allContactsInfo) {

		if (StringUtils.isEmpty(phone)) {
			return null;
		}
		ContactsInfoDTO contactsInfo=new ContactsInfoDTO();
		for (int i = 0; i < allContactsInfo.size(); i++) {
			BestFriendDTO friendInfo=allContactsInfo.get(i);
			if (phone.equals(friendInfo.getPhone())) {
				contactsInfo.setName(name);
				contactsInfo.setPhone(phone);
				contactsInfo.setTalkRanking(i+1);
				contactsInfo.setTalkDays(friendInfo.getTalkDays());
				contactsInfo.setTalkTimes(friendInfo.getTalkTimes());
				contactsInfo.setTalkDuration(friendInfo.getTalkDuration());
				break;
			}
		}
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("phone", phone);
//		map.put("callIds", callIds);
//		ContactsInfoDTO contactsInfo = dxCallDetailClientMapper.getContactsInfoByCallIdAndPhone(map);
//		contactsInfo.setName(name);
//		contactsInfo.setPhone(phone);
		return contactsInfo;
	}
	//归属地及地域统计处理及特殊号码统计
	private void dealAreaInfo(List<BestFriendDTO> allContactsInfo,ReportDTO report){
		Map<String, FriendsCityDTO> areaInfo = new HashMap<String, FriendsCityDTO>();
		try {
			CountDownLatch threadSignal = new CountDownLatch(allContactsInfo.size());
			for (BestFriendDTO bestFriendDTO : allContactsInfo) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String res = HttpClientUtil.get(URL + bestFriendDTO.getPhone());
							JSONObject result = JSONObject.parseObject(res);
							String city = result == null || result.get("city") == null ? "未知"
									: result.get("city").toString();
							bestFriendDTO.setCity(city);
							if (areaInfo.containsKey(city)) {
								FriendsCityDTO friendsCityDTO = areaInfo.get(city);
								friendsCityDTO.setNumber(friendsCityDTO.getNumber() + 1);
								friendsCityDTO
										.setTalkTimes(friendsCityDTO.getTalkTimes() + bestFriendDTO.getTalkTimes());
								friendsCityDTO.setTalkDuration(
										friendsCityDTO.getTalkDuration() + bestFriendDTO.getTalkDuration());
								friendsCityDTO
										.setCallTimes(friendsCityDTO.getCallTimes() + bestFriendDTO.getCallTimes());
								friendsCityDTO.setCalledTimes(
										friendsCityDTO.getCalledTimes() + bestFriendDTO.getCalledTimes());
								areaInfo.put(city, friendsCityDTO);
							} else {
								FriendsCityDTO friendsCityDTO = new FriendsCityDTO();
								friendsCityDTO.setCity(city);
								friendsCityDTO.setNumber(1);
								friendsCityDTO.setTalkTimes(bestFriendDTO.getTalkTimes());
								friendsCityDTO.setTalkDuration(bestFriendDTO.getTalkDuration());
								friendsCityDTO.setCallTimes(bestFriendDTO.getCallTimes());
								friendsCityDTO.setCalledTimes(bestFriendDTO.getCalledTimes());
								areaInfo.put(city, friendsCityDTO);
							}
						} catch (Exception e) {
							log.error(e.getMessage());
						}
						threadSignal.countDown();
					}
				}).start();
			}
			threadSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<FriendsCityDTO> area=new ArrayList<FriendsCityDTO>();
		for (FriendsCityDTO friendsCityDTO : areaInfo.values()) {
			area.add(friendsCityDTO);
		}
		area.sort((FriendsCityDTO h1, FriendsCityDTO h2) -> h2.getNumber().compareTo(h1.getNumber()));
		report.setFriendsCity(area);
	}



	private List<DxCallDetailClient> getListByCallIds(List<Integer> callIds){
		if(callIds != null && callIds.size() > 0){
			List<DxCallDetailClient> list = dxCallDetailClientMapper.getListByCallId(callIds);
			return list;
		}
		return null;
	}

	private DataDto modileToDto(DxCallDetailClient callDetailClient){
		DataDto dataDto = new DataDto();
		BeanUtils.copyProperties(callDetailClient,dataDto);
		return dataDto;
	}

	public List<DataDto> detailListToDataDto(List<DxCallDetailClient> dtoList){
		List<DataDto> list = Lists.newArrayList();
		dtoList.forEach( dto -> {
			list.add(modileToDto(dto));
		});
		return list;
	}


	public String getTimeLength(String mobile){
		String path = server+"/civp/getview/api/u/queryUnify" ;
		// 请求参数(client里面会自动加密,所以这里请使用明文)
		Map<String,String> params = new HashMap<String,String>();
		params.put("innerIfType", "A3") ;
		params.put("cid", mobile) ;
		params.put("idNumber", "460006198912180030") ;
		params.put("realName", "张三") ;
		params.put("authCode", client.rpad(uno+":"+params.get("cid"), 32)) ;
		// 请求数据接口返回json
		String data = client.getData(path,params) ;
		JSONObject jsonObject = JSON.parseObject(data);
		JSONObject jsonData = jsonObject.getJSONObject("data");
		JSONArray RSL = jsonData.getJSONArray("RSL");
		JSONObject RS = RSL.getJSONObject(0).getJSONObject("RS");
		String desc = RS.getString("desc");
		log.info(desc);
		return desc;
	}

}
