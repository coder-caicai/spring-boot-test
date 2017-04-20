package com.hbc.api.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class ReadAndWriteFile {
	
	private final static Logger log = LoggerFactory.getLogger(ReadAndWriteFile.class);
	
	
	//从文件读取人员信息和数据
	public JSONObject readFile(String path){
		
		log.info("开始读取文件: DB_ID.txt");
		JSONObject json = new JSONObject();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(new File(path)));
			
			json = JSONObject.parseObject(reader.readLine().trim());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return json;
	}
	
	
	public void writeFile(JSONObject json, String path){
		try{
			log.info("开始写文件: DB_ID.txt");
		    File file =new File(path);
	
//		    if(file.exists()){
//		    	file.delete();
//		    }
//		    file.createNewFile();
	
		    FileWriter fileWritter = new FileWriter(file,false);
		    BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		    
		    bufferWritter.write(json.toString());
		    
		    bufferWritter.flush();

		    bufferWritter.close();
	
		    log.info("文件写入结束");
		    
	    }catch(IOException e){
	    	e.printStackTrace();
	    }
	}
	
}
