package com.xinrui.code.service.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Service;

import com.xinrui.code.exception.CalException;
import com.xinrui.code.service.ImgPreProcessService;
import com.xinrui.code.util.CodeConstants;
import com.xinrui.code.util.Constants;
import com.xinrui.code.util.JwUtils;

@Service
public class ImgPreProcessServiceImpl implements ImgPreProcessService {

	private Map<BufferedImage, String> trainMap = null;

	public String downloadImage(String url, String imgName) {

		CloseableHttpClient httpClient = JwUtils.getHttpClient();
		HttpGet getMethod = new HttpGet(url);
		getMethod.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625Firefox/3.6.6 Greatwqs");
		HttpResponse response = null;
		try {
			response = httpClient.execute(getMethod);
			if ("HTTP/1.1 200 OK".equals(response.getStatusLine().toString())) {
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				OutputStream os = new FileOutputStream(new File(Constants.SRCPATH + imgName));
				int length = -1;
				byte[] bytes = new byte[1024];
				while ((length = is.read(bytes)) != -1) {
					os.write(bytes, 0, length);
				}
				os.close();
				return Constants.SRCPATH + imgName;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new CalException(CodeConstants.HTTP_REQUEST_ERROR, "http请求异常");
		} catch (IOException e) {
			e.printStackTrace();
			throw new CalException(CodeConstants.SAVE_FILE_ERROR, "生成文件错误!");
		}
		return null;
	}

	public int isBlue(int colorInt) {
		Color color = new Color(colorInt);
		int rgb = color.getRed() + color.getGreen() + color.getBlue();
		if (rgb == 153)
			return 1;
		return 0;
	}

	public int isBlack(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() <= 100)
			return 1;
		return 0;
	}

	public BufferedImage removeBackgroud(String picFile) throws Exception {
		BufferedImage img = ImageIO.read(new File(picFile));
		img = img.getSubimage(5, 1, img.getWidth() - 5, img.getHeight() - 2);
		img = img.getSubimage(0, 0, 50, img.getHeight());
		int width = img.getWidth();
		int height = img.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (isBlue(img.getRGB(x, y)) == 1) {
					img.setRGB(x, y, Color.BLACK.getRGB());
				} else {
					img.setRGB(x, y, Color.WHITE.getRGB());
				}
			}
		}
		return img;
	}

	public List<BufferedImage> splitImage(BufferedImage img) throws Exception {
		List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		int width = img.getWidth() / 4;
		int height = img.getHeight();
		subImgs.add(img.getSubimage(0, 0, width, height));
		subImgs.add(img.getSubimage(width, 0, width, height));
		subImgs.add(img.getSubimage(width * 2, 0, width, height));
		subImgs.add(img.getSubimage(width * 3, 0, width, height));
		return subImgs;
	}

	public Map<BufferedImage, String> loadTrainData() {
		if (trainMap == null) {
			Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
			File dir = new File(Constants.TRAINPATH);
			File[] files = dir.listFiles();
			for (File file : files) {
				try {
					map.put(ImageIO.read(file), file.getName().charAt(0) + "");
				} catch (IOException e) {
					e.printStackTrace();
					throw new CalException(CodeConstants.FILE_LOAD_ERROR, "文件加载异常：" + "[" + Constants.TRAINPATH + "]");
				}
			}
			trainMap = map;
		}
		return trainMap;
	}

	public String getSingleCharOcr(BufferedImage img, Map<BufferedImage, String> map) {
		String result = "#";
		int width = img.getWidth();
		int height = img.getHeight();
		int min = width * height;
		for (BufferedImage bi : map.keySet()) {
			int count = 0;
			if (Math.abs(bi.getWidth() - width) > 2)
				continue;
			int widthmin = width < bi.getWidth() ? width : bi.getWidth();
			int heightmin = height < bi.getHeight() ? height : bi.getHeight();
			Label1: for (int x = 0; x < widthmin; ++x) {
				for (int y = 0; y < heightmin; ++y) {
					if (isBlack(img.getRGB(x, y)) != isBlack(bi.getRGB(x, y))) {
						count++;
						if (count >= min)
							break Label1;
					}
				}
			}
			if (count < min) {
				min = count;
				result = map.get(bi);
			}
		}
		return result;
	}

	public String getAllOcr(String file) throws Exception {
		BufferedImage img = removeBackgroud(file);
		List<BufferedImage> listImg = splitImage(img);
		Map<BufferedImage, String> map = loadTrainData();
		String result = "";
		for (BufferedImage bi : listImg) {
			result += getSingleCharOcr(bi, map);
		}
		return result;
	}
}
