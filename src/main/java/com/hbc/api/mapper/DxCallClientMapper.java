package com.hbc.api.mapper;

import com.hbc.api.model.DxCallClient;
import com.hbc.api.util.MyMapper;

import java.util.List;


/**
 * ccz
 */
public interface DxCallClientMapper extends MyMapper<DxCallClient> {

    public List<DxCallClient> getListByMobile(String mobile);
}
