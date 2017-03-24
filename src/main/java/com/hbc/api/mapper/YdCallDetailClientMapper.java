
package com.hbc.api.mapper;

import com.hbc.api.dto.BestFriendDTO;
import com.hbc.api.dto.CallDurationDateDTO;
import com.hbc.api.dto.CallTimesAndDurationDTO;
import com.hbc.api.dto.CallTimesDateDTO;
import com.hbc.api.dto.NightInfoDTO;
import com.hbc.api.model.DxCallDetailClient;
import com.hbc.api.model.YdCallDetailClient;
import com.hbc.api.util.MyMapper;

import java.util.List;
import java.util.Map;


/**
 * ccz
 */
public interface YdCallDetailClientMapper extends MyMapper<YdCallDetailClient> {

    public List<YdCallDetailClient> getListByCallId(List<Integer> callIds);

    public List<YdCallDetailClient> getTop10ByCallId(List<Integer> callIds);
    
    public List<CallTimesDateDTO> getCallTimesTop10ByCallId(List<Integer> callIds);
    
    public List<CallDurationDateDTO> getCallDurationTop10ByCallId(List<Integer> callIds);
    
    public Integer getNightCallTimesByCallId(List<Integer> callIds);
    
    public Integer getNightCallDurationByCallId(List<Integer> callIds);
    
    public Integer getRoamingDaysByCallId(List<Integer> callIds);
    
    public Integer getSleepingDaysByCallId(List<Integer> callIds);
    
    public NightInfoDTO getNightInfoByCallId(List<Integer> callIds);
    
    public CallTimesAndDurationDTO getCallTimesAndDurationByCallIdAndType(Map<String,Object> map);
    
    public List<BestFriendDTO> getAllContactsInfoByCallId(List<Integer> callIds);
}
