package com.hbc.api.mapper;


import com.hbc.api.model.MobileTag;

public interface MobileTagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MobileTag record);

    int insertSelective(MobileTag record);

    MobileTag selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MobileTag record);

    int updateByPrimaryKey(MobileTag record);
    
    //根据手机号查询数量
    long selectByMobile(MobileTag record);
}