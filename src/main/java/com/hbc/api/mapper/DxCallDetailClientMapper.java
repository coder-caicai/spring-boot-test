
package com.hbc.api.mapper;

import com.hbc.api.dto.BestFriendDTO;
import com.hbc.api.dto.CallDurationDateDTO;
import com.hbc.api.dto.CallTimesAndDurationDTO;
import com.hbc.api.dto.CallTimesDateDTO;
import com.hbc.api.dto.ContactsInfoDTO;
import com.hbc.api.dto.NightInfoDTO;
import com.hbc.api.model.DxCallDetailClient;
import com.hbc.api.util.MyMapper;

import java.util.List;
import java.util.Map;


/**
 * ccz
 */
public interface DxCallDetailClientMapper extends MyMapper<DxCallDetailClient> {

    public List<DxCallDetailClient> getListByCallId(List<Integer> callIds);

    public List<DxCallDetailClient> getTop10ByCallId(List<Integer> callIds);
    
    public List<CallTimesDateDTO> getCallTimesTop10ByCallId(List<Integer> callIds);
    
    public List<CallDurationDateDTO> getCallDurationTop10ByCallId(List<Integer> callIds);
    
    public Integer getNightCallTimesByCallId(List<Integer> callIds);
    
    public Integer getNightCallDurationByCallId(List<Integer> callIds);
    
    public Integer getRoamingDaysByCallId(List<Integer> callIds);
    
    public Integer getSleepingDaysByCallId(List<Integer> callIds);
    
    public ContactsInfoDTO getContactsInfoByCallIdAndPhone(Map<String,Object> map) ;
    
    public NightInfoDTO getNightInfoByCallId(List<Integer> callIds);
    
    public CallTimesAndDurationDTO getCallTimesAndDurationByCallIdAndType(Map<String,Object> map);
    
    public List<BestFriendDTO> getAllContactsInfoByCallId(List<Integer> callIds);

    List<DxCallDetailClient> findNewData(Integer id);

}
