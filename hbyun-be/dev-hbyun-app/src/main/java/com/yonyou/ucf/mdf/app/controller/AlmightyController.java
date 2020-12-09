package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import com.yonyou.ucf.mdf.app.service.IBillService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 通用Controller 默认可以处理
 */
@Controller
@RequestMapping("/custom")
public class AlmightyController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(AlmightyController.class);

    @Autowired
    private IBillService billService;

    @RequestMapping(value = "/do/{action}",method = RequestMethod.POST)
    public void list(@PathVariable String action, @RequestBody BaseReqDto queryParam, HttpServletRequest request, HttpServletResponse response) {
        try {
            queryParam.setAction(action);
            Object obj = billService.doAction(queryParam);
            renderJson(response, ResultMessage.data(obj));
        } catch (Exception e) {
            logger.error(e.getMessage());
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }
}
