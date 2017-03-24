package com.hbc.api.mapper;

import com.hbc.api.model.DxCallClient;
import com.hbc.api.model.YdCallClient;
import com.hbc.api.util.MyMapper;

import java.util.List;


/**
 * ccz
 */
public interface YdCallClientMapper extends MyMapper<YdCallClient> {

    public List<YdCallClient> getListByMobile(String mobile);
}
