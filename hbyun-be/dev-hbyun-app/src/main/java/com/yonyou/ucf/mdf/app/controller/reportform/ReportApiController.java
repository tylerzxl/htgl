package com.yonyou.ucf.mdf.app.controller.reportform;

import com.yonyou.ucf.mdd.common.model.model.ReportResult;
import com.yonyou.ucf.mdf.app.controller.BaseController;
import com.yonyou.ucf.mdf.app.service.IReportBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 报表-查询接口
 */
@RestController
@RequestMapping("/v1")
@Slf4j
public class ReportApiController extends BaseController {

    @Autowired
    private IReportBaseService reportBaseService;

    @RequestMapping("/query")
    public ReportResult query(@RequestBody Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
        try {
            ReportResult reportResult = reportBaseService.query(param);

            return reportResult;
        } catch (Exception e) {
            log.error("ReportQuery", e);
            ReportResult reportResult = new ReportResult();
            reportResult.setMessage(e.getMessage());
            reportResult.setSuccess(false);
            return reportResult;
        }
    }

}
