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
import com.hbc.api.dto.TopDataDto;
import com.hbc.api.mapper.DxCallClientMapper;
import com.hbc.api.mapper.DxCallDetailClientMapper;
import com.hbc.api.mapper.TelephoneNumberMapper;
import com.hbc.api.mapper.YdCallClientMapper;
import com.hbc.api.mapper.YdCallDetailClientMapper;
import com.hbc.api.model.DxCallClient;
import com.hbc.api.model.DxCallDetailClient;
import com.hbc.api.model.TelephoneNumber;
import com.hbc.api.model.YdCallClient;
import com.hbc.api.model.YdCallDetailClient;
import com.hbc.api.util.HttpClientUtil;

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
 * @author ccz
 * @since 2016-07-22 11:09
 */
@Service
public class YdCallDetailClientService {
	private static Logger log = LoggerFactory.getLogger(YdCallDetailClientService.class);
    @Autowired
    private YdCallDetailClientMapper ydCallDetailClientMapper;

    @Autowired
    private YdCallClientMapper ydCallClientMapper;
    
    
    @Autowired
    private TelephoneNumberMapper telephoneNumberMapper;

    /**
	 * 获取详单数据
	 * @param mobile
	 * @return
     */
    public List<DataDto> getListByMobile(String mobile) {
        List<YdCallClient> ydCallClients = ydCallClientMapper.getListByMobile(mobile);
        if(ydCallClients != null && ydCallClients.size() > 0){
            List<Integer> callIds = new ArrayList<>();
            for(YdCallClient ydCallClient : ydCallClients){
                callIds.add(ydCallClient.getId());
            }
            List<YdCallDetailClient> list = getListByCallIds(callIds);
            List<DataDto> resultList = new ArrayList<>();
            for(YdCallDetailClient ydCallDetailClient : list){
                resultList.add(modileToDto(ydCallDetailClient));
            }
            return resultList;
        }
        return null;
    }

    public List<TopDataDto> getTop10ByMobile(String mobile){
        List<YdCallClient> ydCallClients = ydCallClientMapper.getListByMobile(mobile);
        if(ydCallClients != null && ydCallClients.size() > 0){
            List<Integer> callIds = new ArrayList<>();
            for(YdCallClient ydCallClient : ydCallClients){
                callIds.add(ydCallClient.getId());
            }
            List<YdCallDetailClient> list = ydCallDetailClientMapper.getTop10ByCallId(callIds);
            List<TopDataDto> resultList = new ArrayList<>();
            for(YdCallDetailClient ydCallDetailClient : list){
                TopDataDto dto = new TopDataDto();
                dto.setMobile(ydCallDetailClient.getEachOtherNm());
                dto.setCallTimes(ydCallDetailClient.getCallTimes());
                resultList.add(dto);
            }
            return resultList;
        }
        return null;
    }
    //通话总次数top10
    public List<CallTimesDateDTO> getCallTimesTop10ByMobile(String mobile){
        List<YdCallClient> ydCallClients = ydCallClientMapper.getListByMobile(mobile);
        if(ydCallClients != null && ydCallClients.size() > 0){
            List<Integer> callIds = new ArrayList<>();
            for(YdCallClient ydCallClient : ydCallClients){
                callIds.add(ydCallClient.getId());
            }
            List<CallTimesDateDTO> list = ydCallDetailClientMapper.getCallTimesTop10ByCallId(callIds);
            return list;
        }
        return null;
    }
    //通话总时长top10
    public List<CallDurationDateDTO> getCallDurationTop10ByMobile(String mobile){
        List<YdCallClient> ydCallClients = ydCallClientMapper.getListByMobile(mobile);
        if(ydCallClients != null && ydCallClients.size() > 0){
            List<Integer> callIds = new ArrayList<>();
            for(YdCallClient ydCallClient : ydCallClients){
                callIds.add(ydCallClient.getId());
            }
            List<CallDurationDateDTO> list = ydCallDetailClientMapper.getCallDurationTop10ByCallId(callIds);
            return list;
        }
        return null;
    }
    
    //夜间通话总次数
    public CallTimesDateDTO getNightCallTimesByMobile(String mobile){
    	CallTimesDateDTO result= new CallTimesDateDTO();
    	List<YdCallClient> ydCallClients = ydCallClientMapper.getListByMobile(mobile);
        if(ydCallClients != null && ydCallClients.size() > 0){
            List<Integer> callIds = new ArrayList<>();
            for(YdCallClient ydCallClient : ydCallClients){
                callIds.add(ydCallClient.getId());
            }
            Integer times = ydCallDetailClientMapper.getNightCallTimesByCallId(callIds);
            result.setCallTimes(times);
            result.setMobile(mobile);
            return result;
        }
        return null;
    }
    //夜间通话总时长
    public CallDurationDateDTO getNightCallDurationByMobile(String mobile){
    	CallDurationDateDTO result= new CallDurationDateDTO();
        List<YdCallClient> ydCallClients = ydCallClientMapper.getListByMobile(mobile);
        if(ydCallClients != null && ydCallClients.size() > 0){
            List<Integer> callIds = new ArrayList<>();
            for(YdCallClient ydCallClient : ydCallClients){
                callIds.add(ydCallClient.getId());
            }
            Integer duration = ydCallDetailClientMapper.getNightCallDurationByCallId(callIds);
            result.setMobile(mobile);
            result.setCallDuration(duration==null?0:duration);
            return result;
        }
        return null;
    }
    
    //漫游天数，过去6个月存在漫游通话记录的天数
    public Integer getRoamingDaysByMobile(String mobile){
        List<YdCallClient> ydCallClients = ydCallClientMapper.getListByMobile(mobile);
        if(ydCallClients != null && ydCallClients.size() > 0){
            List<Integer> callIds = new ArrayList<>();
            for(YdCallClient ydCallClient : ydCallClients){
                callIds.add(ydCallClient.getId());
            }
            Integer days = ydCallDetailClientMapper.getRoamingDaysByCallId(callIds);
            return days;
        }
        return null;
    }
    
    //手机静默情况   无通话记录的天数
    public Integer getSleepingDaysByMobile(String mobile){
        List<YdCallClient> ydCallClients = ydCallClientMapper.getListByMobile(mobile);
        if(ydCallClients != null && ydCallClients.size() > 0){
            List<Integer> callIds = new ArrayList<>();
            for(YdCallClient ydCallClient : ydCallClients){
                callIds.add(ydCallClient.getId());
            }
            Integer days = ydCallDetailClientMapper.getSleepingDaysByCallId(callIds);
            return days;
        }
        return null;
    }

    private List<YdCallDetailClient> getListByCallIds(List<Integer> callIds){
        if(callIds != null && callIds.size() > 0){
            List<YdCallDetailClient> list = ydCallDetailClientMapper.getListByCallId(callIds);
            return list;
        }
        return null;
    }

    private DataDto modileToDto(YdCallDetailClient ydCallDetailClient){
        DataDto dataDto = new DataDto();
        dataDto.setCallMobile(ydCallDetailClient.getEachOtherNm());
        dataDto.setCallFee(ydCallDetailClient.getCommFee());
        dataDto.setCallStyle(ydCallDetailClient.getCommType() );//市话/长途
        dataDto.setCallType(ydCallDetailClient.getCommMode() );//主叫/被叫
        dataDto.setCallArea(ydCallDetailClient.getCommPlac());
        dataDto.setCallTime(ydCallDetailClient.getStartTime());
        dataDto.setCallTimeCost(ydCallDetailClient.getCommTimeH5());
        return dataDto; 
        
    }

	public List<DataDto> detailToDataDto(List<YdCallDetailClient> modelList){
		List<DataDto> list = Lists.newArrayList();
		modelList.forEach( model -> {
			list.add(modileToDto(model));
		});
		return list;
	}





    /* 
     * @function 获取获取PDF报告信息
     * 
     * @param name 
	 *         姓名   
	 * @param phoneNum
	 *         手机号码
	 * @param idCard   
	 *         身份证号  
	 * @param contact1
	 *         联系人一
	 * @param contactNum1 
	 *         联系人号码
	 * @param contact2 
	 *         联系人一
	 * @param contactNum2
	 *         联系人号码
     */
	public ReportDTO getReportInfo(String name, String phoneNum, String idCard, String contact1, String contactNum1,
			String contact2, String contactNum2) {
    	log.info("获取PDF报告信息开始");
		ReportDTO report = new ReportDTO();
		List<YdCallClient> ydCallClients = ydCallClientMapper.getListByMobile(phoneNum);
		List<Integer> callIds = new ArrayList<Integer>();
    	if(ydCallClients != null && ydCallClients.size() > 0){
            for(YdCallClient ydCallClient : ydCallClients){
                callIds.add(ydCallClient.getId());
            }
        }
    	if (callIds.size()==0) {
			return null;
		}
    	log.info("获取所有联系人信息开始");
    	Long startTime1 = System.currentTimeMillis();
    	//获取所有联系人信息
    	List<BestFriendDTO> allContactsInfo=ydCallDetailClientMapper.getAllContactsInfoByCallId(callIds);
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
								Integer days = ydCallDetailClientMapper.getSleepingDaysByCallId(callIds);
								report.setSleepDays(days);
								Long cost2= System.currentTimeMillis()-startTime2;
								log.info("获取手机静默天数信息结束 ：耗时"+cost2+"毫秒");
								break;
							// 夜间通话情况
							case "nightInfo":
								log.info("获取夜间通话情况信息开始");
								Long startTime3 = System.currentTimeMillis();
								NightInfoDTO nightInfo = ydCallDetailClientMapper.getNightInfoByCallId(callIds);
								report.setNightInfo(nightInfo);
								Long cost3= System.currentTimeMillis()-startTime3;
								log.info("获取夜间通话情况信息结束 ：耗时"+cost3+"毫秒");
								break;
							// 运营商月度信息统计
							case "monthlyInfo":
								log.info("获取运营商月度信息统计信息开始");
								Long startTime4 = System.currentTimeMillis();
								List<MonthlyDTO> monthlyInfo = getMonthlyInfo(ydCallClients);
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
	/*
	 * @function 获取进本信息核实
	 * 
	 * @param name 
	 *         姓名   
	 * @param phoneNum
	 *         手机号码
	 * @param idCard   
	 *         省份证号       
	 */

	public BaseInfoDTO getBaseInfo(String name, String phoneNum, String idCard) {
		BaseInfoDTO baseInfoDTO = new BaseInfoDTO();
		baseInfoDTO.setName(name);
		baseInfoDTO.setIdCard(idCard);
		baseInfoDTO.setPhone(phoneNum);
		// 会员等级 TODO
		baseInfoDTO.setMemberLevel("未知");
		// 入网时间 TODO
		baseInfoDTO.setOpenTime("未知");
		// 入网时间 TODO
		baseInfoDTO.setRealName("未知");
		return baseInfoDTO;
	}

	/*
	 * @function 获取PDF报告信息-运营商数据分析
	 * 
	 * @param dxCallClients 
	 *              
	 */ 
    public List<MonthlyDTO> getMonthlyInfo(List<YdCallClient> ydCallClients) {
    	List<MonthlyDTO> list= new ArrayList<MonthlyDTO>();
    	for (YdCallClient client :ydCallClients) {
    		MonthlyDTO monthlyDTO=new MonthlyDTO();
    		monthlyDTO.setMonth(client.getCallDate());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("callId", client.getId());
			map.put("type", "0");
			CallTimesAndDurationDTO callInfo=ydCallDetailClientMapper.getCallTimesAndDurationByCallIdAndType(map);
			map.put("type", "1");
			CallTimesAndDurationDTO calledInfo=ydCallDetailClientMapper.getCallTimesAndDurationByCallIdAndType(map);
			monthlyDTO.setCallDuration(callInfo==null||callInfo.getCallDuration()==null?0:callInfo.getCallDuration());
			monthlyDTO.setCallTimes(callInfo==null||callInfo.getCallTimes()==null?0:callInfo.getCallTimes());
			monthlyDTO.setCalledDuration(calledInfo==null||calledInfo.getCallDuration()==null?0:calledInfo.getCallDuration());
			monthlyDTO.setCalledTimes(calledInfo==null||calledInfo.getCallTimes()==null?0:calledInfo.getCallTimes());
			list.add(monthlyDTO);
    	}
		list.sort((MonthlyDTO x,MonthlyDTO y) -> Integer.valueOf(x.getMonth()).compareTo( Integer.valueOf(y.getMonth())));
    	return list;
    }
    /*
	 * @function 归属地及地域统计处理
	 * 
	 * @param allContactsInfo
	 *            联系人信息 
	 * @param  report   
	 *            pdf报告
	 *            
	 */ 
  	private void dealAreaInfo(List<BestFriendDTO> allContactsInfo,ReportDTO report){
  		Map<String, FriendsCityDTO> areaInfo = new HashMap<String, FriendsCityDTO>();
  		//归属地接口地址
  		String URL="http://localbase.hbc315.com/tel/search?phone=";
  		try {
			CountDownLatch threadSignal = new CountDownLatch(allContactsInfo.size());
			for (BestFriendDTO bestFriendDTO : allContactsInfo) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String res = HttpClientUtil.get(URL + bestFriendDTO.getPhone());
							JSONObject result = JSONObject.parseObject(res);
							String city = result == null || result.get("city") == null ? "未知": result.get("city").toString();
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
  	/*
	 * @function 获取PDF报告信息-联系人核验
	 * 
	 * @param contact1
	 *           
	 * @param  contactNum1 
	 *   
	 * @param contact2
	 *           
	 * @param  contactNum2
	 *           
	 * @param callIds
	 *           
	 * @param  allContactsInfo          
	 */ 
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
    /*
	 * @function 联系人信息获取
	 * 
	 * @param name
	 *          姓名
	 * @param  phone 
	 *          手机号码
	 * @param callIds
	 *           
	 * @param  allContactsInfo
	 *              联系人    
	 */ 
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
   		return contactsInfo;
    }
}
