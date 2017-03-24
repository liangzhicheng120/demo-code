package com.xinrui.code.action;

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
	public BaseResultModel getcode(String url) throws Exception {
		CheckUtil.isValidUrl(url);
		BaseResultModel baseResultModel = new BaseResultModel();
		String code = imgPreProcessService.getAllOcr(imgPreProcessService.downloadImage(url, "code.png"));
		baseResultModel.setValue(code);
		return baseResultModel;
	}

}
