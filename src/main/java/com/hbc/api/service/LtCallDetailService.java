package com.hbc.api.service;

import com.alibaba.fastjson.JSONObject;
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
import com.hbc.api.dto.RoamInfoDTO;
import com.hbc.api.dto.TopDataDto;
import com.hbc.api.mapper.LtCallMapper;
import com.hbc.api.mapper.TelephoneNumberMapper;
import com.hbc.api.mapper.LtCallDetailMapper;
import com.hbc.api.model.DxCallClient;
import com.hbc.api.model.LtCall;
import com.hbc.api.model.LtCallDetail;
import com.hbc.api.model.TelephoneNumber;
import com.hbc.api.util.HttpClientUtil;
import com.mysql.fabric.xmlrpc.base.Data;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cheng on 16/7/25.
 */
@Service
public class LtCallDetailService {
	private static Logger log = LoggerFactory.getLogger(LtCallDetailService.class);
	@Autowired
	private LtCallDetailMapper ltCallDetailMapper;

	@Autowired
	private LtCallMapper ltCallMapper;
	
    @Autowired
    private TelephoneNumberMapper telephoneNumberMapper;

	//归属地接口地址 TODO
    private static String URL="http://localbase.hbc315.com/tel/search?phone=";
    
	public List<DataDto> getCallDetail(String mobile) {
		List<LtCall> callList = ltCallMapper.getListByMobile(mobile);
		List<Integer> callIdList = new ArrayList<>();
		for (LtCall ltCall : callList) {
			callIdList.add(ltCall.getId());
		}
		if (callIdList == null || callIdList.size() == 0) {
			return null;
		}
		List<LtCallDetail> list = ltCallDetailMapper.getListByCallId(callIdList);
		List<DataDto> resultList = new ArrayList<>();
		for (LtCallDetail ltCallDetail : list) {
			resultList.add(modelToDto(ltCallDetail));
		}
		return resultList;
	}

	public List<TopDataDto> getTop10ByMobile(String mobile) {
		List<LtCall> callList = ltCallMapper.getListByMobile(mobile);
		List<Integer> callIdList = new ArrayList<>();
		for (LtCall ltCall : callList) {
			callIdList.add(ltCall.getId());
		}
		List<LtCallDetail> list = ltCallDetailMapper.getTop10ByCallId(callIdList);
		List<TopDataDto> result = new ArrayList<>();
		for (LtCallDetail ltCallDetail : list) {
			TopDataDto topDataDto = new TopDataDto();
			topDataDto.setMobile(ltCallDetail.getOthernum());
			topDataDto.setCallTimes(ltCallDetail.getCallTimes());
			result.add(topDataDto);
		}
		return result;
	}

	// 通话总次数top10
	public List<CallTimesDateDTO> getCallTimesTop10ByMobile(String mobile) {
		List<LtCall> callList = ltCallMapper.getListByMobile(mobile);
		if (callList != null && callList.size() > 0) {
			List<Integer> callIdList = new ArrayList<>();
			for (LtCall ltCall : callList) {
				callIdList.add(ltCall.getId());
			}
			List<CallTimesDateDTO> list = ltCallDetailMapper.getCallTimesTop10ByCallId(callIdList);
			return list;
		}
		return null;
	}

	// 通话总时长top10
	public List<CallDurationDateDTO> getCallDurationTop10ByMobile(String mobile) {
		List<LtCall> callList = ltCallMapper.getListByMobile(mobile);
		if (callList != null && callList.size() > 0) {
			List<Integer> callIdList = new ArrayList<>();
			for (LtCall ltCall : callList) {
				callIdList.add(ltCall.getId());
			}
			List<CallDurationDateDTO> list = ltCallDetailMapper.getCallDurationTop10ByCallId(callIdList);
			return list;
		}
		return null;
	}

	// 夜间通话总次数
	public CallTimesDateDTO getNightCallTimesByMobile(String mobile) {
		CallTimesDateDTO result = new CallTimesDateDTO();
		List<LtCall> callList = ltCallMapper.getListByMobile(mobile);
		if (callList != null && callList.size() > 0) {
			List<Integer> callIdList = new ArrayList<>();
			for (LtCall ltCall : callList) {
				callIdList.add(ltCall.getId());
			}
			Integer times = ltCallDetailMapper.getNightCallTimesByCallId(callIdList);
			result.setCallTimes(times);
			result.setMobile(mobile);
			return result;
		}
		return null;
	}

	// 夜间通话总时长
	public CallDurationDateDTO getNightCallDurationByMobile(String mobile) {
		CallDurationDateDTO result = new CallDurationDateDTO();
		List<LtCall> callList = ltCallMapper.getListByMobile(mobile);
		if (callList != null && callList.size() > 0) {
			List<Integer> callIdList = new ArrayList<>();
			for (LtCall ltCall : callList) {
				callIdList.add(ltCall.getId());
			}
			Integer duration = ltCallDetailMapper.getNightCallDurationByCallId(callIdList);
			result.setMobile(mobile);
			result.setCallDuration(duration==null?0:duration);
			return result;
		}
		return null;
	}

	// 漫游天数，过去6个月存在漫游通话记录的天数
	public Integer getRoamingDaysByMobile(String mobile) {
		List<LtCall> callList = ltCallMapper.getListByMobile(mobile);
		if (callList != null && callList.size() > 0) {
			List<Integer> callIdList = new ArrayList<>();
			for (LtCall ltCall : callList) {
				callIdList.add(ltCall.getId());
			}
			Integer days = ltCallDetailMapper.getRoamingDaysByCallId(callIdList);
			return days;
		}
		return null;
	}

	// 手机静默情况 无通话记录的天数
	public Integer getSleepingDaysByMobile(String mobile) {
		List<LtCall> callList = ltCallMapper.getListByMobile(mobile);
		if (callList != null && callList.size() > 0) {
			List<Integer> callIdList = new ArrayList<>();
			for (LtCall ltCall : callList) {
				callIdList.add(ltCall.getId());
			}
			Integer days = ltCallDetailMapper.getSleepingDaysByCallId(callIdList);
			return days;
		}
		return null;
	}

    //获取PDF报告信息
    public ReportDTO getReportInfo(String name,String phoneNum,String idCard,String contact1,String contactNum1,String contact2,String contactNum2){
    	
    	log.info("获取PDF报告信息开始");
    	
    	ReportDTO report=new ReportDTO();
    	List<LtCall> callList = ltCallMapper.getListByMobile(phoneNum);
    	List<Integer> callIds = new ArrayList<>();
    	if(callList != null && callList.size() > 0){
            for(LtCall ltCall : callList){
                callIds.add(ltCall.getId());
            }
        }   
    	if (callIds.size()==0) {
			return null;
		}
    	log.info("获取所有联系人信息开始");
    	Long startTime1 = System.currentTimeMillis();
    	//获取所有联系人信息
    	List<BestFriendDTO> allContactsInfo=ltCallDetailMapper.getAllContactsInfoByCallId(callIds);
    	Long cost1 = System.currentTimeMillis()-startTime1;
    	log.info("获取所有联系人信息结束 ：耗时"+cost1+"毫秒");
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
							Integer days = ltCallDetailMapper.getSleepingDaysByCallId(callIds);
							report.setSleepDays(days);
							Long cost2= System.currentTimeMillis()-startTime2;
							log.info("获取手机静默天数信息结束 ：耗时"+cost2+"毫秒");
							break;
						// 夜间通话情况
						case "nightInfo":
							log.info("获取夜间通话情况信息开始");
							Long startTime3 = System.currentTimeMillis();
							NightInfoDTO nightInfo = ltCallDetailMapper.getNightInfoByCallId(callIds);
							report.setNightInfo(nightInfo);
							Long cost3= System.currentTimeMillis()-startTime3;
							log.info("获取夜间通话情况信息结束 ：耗时"+cost3+"毫秒");
							break;
						// 运营商月度信息统计
						case "monthlyInfo":
							log.info("获取运营商月度信息统计信息开始");
							Long startTime4 = System.currentTimeMillis();
							List<MonthlyDTO> monthlyInfo = getMonthlyInfo(callList);
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
    
    
    
    //获取PDF报告信息-漫游通话信息
    public List<RoamInfoDTO> getRoamInfo(){
    	
    	return null;
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
    public List<MonthlyDTO> getMonthlyInfo(List<LtCall> ltCall) {
    	List<MonthlyDTO> list= new ArrayList<MonthlyDTO>();
    	for (LtCall client :ltCall) {
    		MonthlyDTO monthlyDTO=new MonthlyDTO();
    		monthlyDTO.setMonth(client.getCallDate());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("callId", client.getId());
			map.put("type", "2");
			CallTimesAndDurationDTO callInfo=ltCallDetailMapper.getCallTimesAndDurationByCallIdAndType(map);
			map.put("type", "1");
			CallTimesAndDurationDTO calledInfo=ltCallDetailMapper.getCallTimesAndDurationByCallIdAndType(map);
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
	//归属地及地域统计处理
	private void dealAreaInfo(List<BestFriendDTO> allContactsInfo, ReportDTO report) {
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
		List<FriendsCityDTO> area = new ArrayList<FriendsCityDTO>();
		for (FriendsCityDTO friendsCityDTO : areaInfo.values()) {
			area.add(friendsCityDTO);
		}
		area.sort((FriendsCityDTO h1, FriendsCityDTO h2) -> h2.getNumber().compareTo(h1.getNumber()));
		report.setFriendsCity(area);
	}
	

	private DataDto modelToDto(LtCallDetail ltCallDetail) {
		DataDto dataDto = new DataDto();
		dataDto.setCallArea(ltCallDetail.getHomearea());
		dataDto.setCallTimeCost(ltCallDetail.getCalllonghour());
		dataDto.setCallTime(ltCallDetail.getCalldate() + " " + ltCallDetail.getCalltime());
		dataDto.setCallType(ltCallDetail.getCalltype().equals("1") ? "1" : "0");
		dataDto.setCallStyle(ltCallDetail.getLandtype().equals("国内通话") ? "0" : "1");
		dataDto.setCallFee(ltCallDetail.getTotalfee());
		dataDto.setCallMobile(ltCallDetail.getOthernum());
		return dataDto;
	}

	public List<DataDto> detailToDataDto(List<LtCallDetail> modelList){
		List<DataDto> list = Lists.newArrayList();
		modelList.forEach( model -> {
				list.add(modelToDto(model));
		});
		return list;
	}
}
