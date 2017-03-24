package com.hbc.api.util;

import java.io.*;

public class FileUtils {
	/**
	 * 图片验证码处理工具类
	 * @param contentPath 相对路径
	 * @param imageByte  图片流
     * @return 图片path
     */
	public static String bytesToFile(String contentPath,byte[] imageByte) {
		String fileName = Math.random()+".png";
		File dir = new File(contentPath,"imagecodedir");
		if(!dir.exists()){
			dir.mkdir();
		}
		File file = new File(dir,fileName);
		InputStream is = new ByteArrayInputStream(imageByte);
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		String imagePath = "/imagecodedir/"+fileName;
		return imagePath;
	}
}
