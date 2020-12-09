package com.yonyou.ucf.mdf.app.service.impl;

import com.yonyou.ucf.mdd.common.model.model.ReportResult;
import com.yonyou.ucf.mdf.app.service.IReportBaseService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("mddDefaultReportService")
public class DefaultReportService implements IReportBaseService {
	
	@Override
	public ReportResult query(Map<String,Object> param) throws Exception {
		return BillReportService.query(param);
	}

}
