package com.xinrui.code.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ImgPreProcessService {
	
	public String downloadImage(String url) throws Exception;

	public int isBlue(int colorInt);

	public int isBlack(int colorInt);

	public BufferedImage removeBackgroud(String picFile) throws Exception;

	public List<BufferedImage> splitImage(BufferedImage img) throws Exception;

	public Map<BufferedImage, String> loadTrainData() throws IOException;

	public String getSingleCharOcr(BufferedImage img, Map<BufferedImage, String> map);

	public String getAllOcr(String file) throws Exception;

}
