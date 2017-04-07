package com.hbc.api.conf;

import com.google.common.collect.Maps;
import com.hbc.api.mapper.TIspNumberMapper;
import com.hbc.api.model.TIspNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cheng on 2017/4/6.
 */
@Component
public class LoadTIspNumberInfo implements CommandLineRunner {

    public static Map<String,TIspNumber> ispMap = new HashMap(358206);

    @Autowired
    private TIspNumberMapper tIspNumberMapper;

    @Override
    public void run(String... args) throws Exception {
        List<TIspNumber> list = tIspNumberMapper.selectAll();
        list.forEach(model -> {
            ispMap.put(model.getPhone(),model);
        });
    }

    public TIspNumber getByPhone(String phone){
       if(phone.length() == 11){
            return ispMap.get(phone.substring(0,7));
       }else{
           return null;
       }
    }
}
