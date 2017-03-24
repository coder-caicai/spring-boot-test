package com.hbc.api.mapper;

import com.hbc.api.model.LtCall;
import com.hbc.api.util.MyMapper;

import java.util.List;


/**
 * ccz
 */
public interface LtCallMapper extends MyMapper<LtCall> {


    public List<LtCall> getListByMobile(String mobile);
}
