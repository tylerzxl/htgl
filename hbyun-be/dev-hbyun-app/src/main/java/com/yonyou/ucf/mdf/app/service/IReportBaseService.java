package com.yonyou.ucf.mdf.app.service;

import com.yonyou.ucf.mdd.common.model.model.ReportResult;

import java.util.Map;


public interface IReportBaseService {
	ReportResult query(Map<String, Object> param) throws Exception;
}
