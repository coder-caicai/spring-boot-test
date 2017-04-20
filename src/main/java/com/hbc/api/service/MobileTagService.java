package com.hbc.api.service;

import com.alibaba.fastjson.JSONObject;
import com.hbc.api.mapper.DxCallDetailClientMapper;
import com.hbc.api.mapper.LtCallDetailMapper;
import com.hbc.api.mapper.MobileTagMapper;
import com.hbc.api.mapper.YdCallDetailClientMapper;
import com.hbc.api.model.DxCallDetailClient;
import com.hbc.api.model.LtCallDetail;
import com.hbc.api.model.MobileTag;
import com.hbc.api.model.YdCallDetailClient;
import com.hbc.api.util.CommonHttpMethod;
import com.hbc.api.util.CustomConfigUtil;
import com.hbc.api.util.ReadAndWriteFile;
import com.hbc.api.util.ResponseValue;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cheng on 2017/4/10.
 */
public class MobileTagService {
    private final static Logger logger = LoggerFactory.getLogger(MobileTagService.class);

    private static int YD_ID;

    private static int LT_ID;

    private static int DX_ID;

    @Autowired
    MobileTagMapper mobileTagMapper;

    @Autowired
    YdCallDetailClientMapper ydCallClientDetailMapper;

    @Autowired
    LtCallDetailMapper ltCallDetailMapper;

    @Autowired
    DxCallDetailClientMapper dxCallClientDetailMapper;

    @Autowired
    CustomConfigUtil customConfigUtil;

    @Autowired
    ReadAndWriteFile readAndWriteFile;

    /**
     * 定时任务
     */
    @ConfigurationProperties(prefix = "custom")
    @Scheduled(cron = "*/10 * * * * ? ")
    public void run(){

        Set<String> mobiles = new HashSet<String>();
        //取新增手机号
        try {
            mobiles = getNewData();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error," + e.getClass() + ": "+ e.getMessage());
            return;
        }


        //爬虫
        try {
            getTag(mobiles);
        } catch (IOReactorException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Set<String> getNewData(){
        JSONObject json = readAndWriteFile.readFile(customConfigUtil.getDB_ID_PATH());
        YD_ID = json.getInteger("yd");
        LT_ID = json.getInteger("lt");
        DX_ID = json.getInteger("dx");
        int ydInc = YD_ID;
        int ltInc = LT_ID;
        int dxInc = DX_ID;
        logger.info("上次数据库ID数值 YD:" + YD_ID + ",LT:" + LT_ID + ",DX:" + DX_ID);

        Set<String> mobiles = new HashSet<String>();
        for(YdCallDetailClient yd:ydCallClientDetailMapper.findNewData(YD_ID)){
            mobiles.add(yd.getEachOtherNm());
            ydInc++;
        }
        for(LtCallDetail lt:ltCallDetailMapper.findNewData(LT_ID)){
            mobiles.add(lt.getOthernum());
            ltInc++;
        }
        for(DxCallDetailClient dx:dxCallClientDetailMapper.findNewData(DX_ID)){
            mobiles.add(dx.getCallMobile());
            dxInc++;
        }
        logger.info("经过去重，新增手机号数量：" + mobiles.size());

        JSONObject jsonInput = new JSONObject();
        jsonInput.put("yd", ydInc);
        jsonInput.put("lt", ltInc);
        jsonInput.put("dx", dxInc);
        readAndWriteFile.writeFile(jsonInput,customConfigUtil.getDB_ID_PATH());

        return mobiles;
    }

    public void getTag(Set<String> mobiles) throws IOReactorException, InterruptedException{

        // 开始批量请求
        AtomicInteger number = new AtomicInteger(0);
        customConfigUtil.getProxyModel();

        for (String mobile : mobiles) {
            //计数加一
            number.incrementAndGet();
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        //查询是否存在
                        MobileTag mobileTag = new MobileTag();
                        mobileTag.setMobile(mobile);

                        long num = mobileTagMapper.selectByMobile(mobileTag);
                        if(num != 0L){
                            logger.info("数据库中已存在" + mobile);
                            return;
                        }

                        logger.info("开始爬取手机号: "+mobile);
                        String result = spider(mobile);

                        if(result == null) return;
                        logger.info("爬虫处理后数据: "+mobile + ":" + result);

                        //插入数据库
                        mobileTag.setTag(result);
                        mobileTagMapper.insertSelective(mobileTag);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(mobile + ",error," + e.getClass() + ": "+ e.getMessage());
                    } finally {
                        //计数减一
                        number.decrementAndGet();
                    }
                }
            });

            try {
                while(Integer.valueOf(customConfigUtil.getThreadNumber()) < number.get()){
                    Thread.sleep(500);
                }
                logger.info("已线程启动数：" + number.get());
                t.start();

            } catch (Exception e) {
                //计数减一
                number.decrementAndGet();
            }

        }

        while(number.get() > 1){
            Thread.sleep(500);
        }

        return;
    }

    public String spider(String mobile){
        String url = "http://ip.cn/db.php?num=" + mobile;
        HttpGet httpGet = new HttpGet(url);

//		String cookie = "CNZZDATA123770=cnzz_eid%3D1194651968-1484014622-%26ntime%3D1484014622";
//		httpGet.setHeader("Cookie", cookie);
        httpGet.setHeader("Referer","http://ip.cn/db.php");
        httpGet.setHeader("Upgrade-Insecure-Requests","1");

        ResponseValue res = CommonHttpMethod.doGet(httpGet);

        String result = res.getResponse();

        if(result == null){
            return result;
        }

        if(result.contains("所在城市")){
            result = result.split("</code>")[1];
            result = result.split("<")[0];
        }
        else if(result.contains(">暂无")){
            result = "暂无";
        }
        else{
            result = result.split("</code>")[1];
            result = result.split("<")[0];
        }

        result = result.replaceAll("所在城市", "");
        result = result.replaceAll("：", "");
        result = result.replaceAll(":", "");
        result = result.replaceAll("&nbsp;", "");
        return result;
    }
}
