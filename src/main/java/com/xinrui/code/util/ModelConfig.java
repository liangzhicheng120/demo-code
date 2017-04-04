package com.xinrui.code.util;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ModelConfig {

	private static Logger logger = Logger.getLogger(ModelConfig.class);

	/**
	 * 模型属性配置文件
	 */
	public static String MODEL_PROPERTIES_PATH;

	public static String ROOT = "/home/tomcat/apache-tomcat-8.0.41/webapps";

	public static String IMAGES_SOURCE_FILE_PATH = "demo-code/images/";

	public static String IMAGES_MODEL_FILE_PATH = "demo-code/images/model/";

	static {
		Properties p = new Properties();
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader == null) {
				loader = ModelConfig.class.getClassLoader();
			}
			p.load(new InputStreamReader(MODEL_PROPERTIES_PATH == null ? loader.getResourceAsStream("model.properties") : new FileInputStream(
					MODEL_PROPERTIES_PATH), "UTF-8"));
			String root = p.getProperty("root", "").replaceAll("\\\\", "/");
			if (!root.endsWith("/")) {
				root += "/";
			}
			IMAGES_SOURCE_FILE_PATH = root + p.getProperty("images.source.file.path", IMAGES_SOURCE_FILE_PATH);
			IMAGES_MODEL_FILE_PATH = root + p.getProperty("images.model.file.path", IMAGES_MODEL_FILE_PATH);
		} catch (Exception e) {
			logger.error("模型属性配置文件读取错误:[" + e + "]");
		}

	}
}
