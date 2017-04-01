package com.hbc.api.mapper;


import com.hbc.api.model.MobileInfo;
import com.hbc.api.util.MyMapper;
import org.apache.ibatis.annotations.Param;

public interface MobileInfoMapper extends MyMapper<MobileInfo> {

    public MobileInfo getByMobile(@Param("mobile") String mobile);
}