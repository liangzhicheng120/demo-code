package com.xinrui.code.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xinrui.code.bean.BaseResultModel;
import com.xinrui.code.service.ImgPreProcessService;
import com.xinrui.code.util.CheckUtil;

@Controller
public class CodeProduce {
	@Autowired
	private ImgPreProcessService imgPreProcessService;

	@RequestMapping(value = "/getCode")
	@ResponseBody
	public BaseResultModel getcode(String url, HttpServletRequest request) throws Exception {
		CheckUtil.isValidUrl(url);
		BaseResultModel baseResultModel = new BaseResultModel();
		String srcPath = request.getSession().getServletContext().getRealPath("images");
		String trainPath = srcPath + "\\train\\";
		String code = imgPreProcessService.getAllOcr(imgPreProcessService.downloadImage(url, "code.png", srcPath), trainPath);
		baseResultModel.setValue(code);
		return baseResultModel;
	}

}
