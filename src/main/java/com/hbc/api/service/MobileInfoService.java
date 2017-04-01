package com.hbc.api.service;

import com.hbc.api.mapper.MobileInfoMapper;
import com.hbc.api.model.MobileInfo;
import com.hbc.api.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by cheng on 2016/12/15.
 */
@Service
public class MobileInfoService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MobileInfoMapper mobileInfoMapper;

    public void save(String mobile,String isp,String timeLength,String userName,String address){
        MobileInfo mobileInfo = new MobileInfo();
        mobileInfo.setMobile(mobile);
        mobileInfo.setIsp(isp);
        mobileInfo.setTime_length(timeLength);
        mobileInfo.setUser_name(userName);
        mobileInfo.setAddress(address);
        mobileInfo.setCreated_time(DateUtil.sdfYYYY_MM_DD_HH_mm_ss.format(new Date()));
        mobileInfoMapper.insert(mobileInfo);
    }

    public void update(MobileInfo mobileInfo){
        mobileInfo.setCreated_time(DateUtil.sdfYYYY_MM_DD_HH_mm_ss.format(new Date()));
        mobileInfoMapper.updateByPrimaryKey(mobileInfo);
    }

    public MobileInfo getByMobile(String mobile){
        MobileInfo result = mobileInfoMapper.getByMobile(mobile);
        return result;
    }

}
