package com.yonyou.ucf.mdf.app.controller;

import com.alibaba.fastjson.JSON;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.model.uimeta.ui.ViewModel;
import com.yonyou.ucf.mdd.common.utils.json.GsonHelper;
import com.yonyou.ucf.mdd.core.dto.POIDto;
import com.yonyou.ucf.mdd.core.i18n.utils.MddMultilingualUtil;
import com.yonyou.ucf.mdd.ext.core.AppContext;
import com.yonyou.ucf.mdd.poi.itf.IPOIService;
import com.yonyou.ucf.mdd.poi.model.ExcelExportData;
import com.yonyou.ucf.mdd.poi.model.StreamParam;
import com.yonyou.ucf.mdd.uimeta.api.UIMetaEngine;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import com.yonyou.ucf.mdf.app.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping({"/billtemp/"})
public class BillPOIController extends BaseController {

    private final static Integer PING_THRESHOLD = 8; //8次请求之后返回一次新的

    //解决缓存轰炸问题,无论前端发多少请求,这里都要挡几波
    private final Map<String, String> pingCache = new ConcurrentHashMap<>();
    private final Map<String, Integer> pingCount = new ConcurrentHashMap<>();

    private final IPOIService poiService;

    @RequestMapping("export")
    public void export(@RequestBody POIDto poiDto, HttpServletRequest request, HttpServletResponse response) {
        try {
            poiDto.setTenantId(MddBaseContext.getTenantId());
            poiDto.setUserId(MddBaseContext.getCurrentUser().getUserId());
            ViewModel simpleVM = UIMetaEngine.getInstance().getSimpleVM(poiDto.getBillnum(), poiDto.getTplId(), poiDto.getTenantId(), true, null, null);
            //TODO 缺少翻译逻辑
            ExcelExportData excelData = poiService.getTempExportData(simpleVM);
            String fileName = simpleVM.getBillName();
            if (MddMultilingualUtil.getI18nConfigEnable()) {
                String resid = simpleVM.getcName_resid();
                Map<String, String> nameML = MddMultilingualUtil.getLocalTransMessage(Collections.singletonList(resid));
                if (StringUtils.isNotEmpty(resid) && nameML.containsKey(resid)) {
                    fileName = nameML.get(resid);
                }
            }
            excelData.setFileName(fileName);
            StreamParam streamParam = new StreamParam(fileName, excelData, response);
            poiService.downLoadToResponse(streamParam);
        } catch (Exception e) {
            log.error("exception when do export ", e);
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }


    @RequestMapping("getImportProcess")
    public void getImportProcess(@RequestParam(value = "asyncKey", required = true) String asyncKey, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            if (pingCache.get(asyncKey) != null && pingCount.get(asyncKey) > 0) {
                Integer curCount = pingCount.get(asyncKey);
                pingCount.put(asyncKey, curCount == null ? PING_THRESHOLD : curCount - 1);
                renderJson(response, pingCache.get(asyncKey));
                return;
            }
            String value = AppContext.cache().get(asyncKey);
            if (StringUtils.isBlank(value)) {
                log.error("#######BillPOIContoller::getImportProcess，获取导入进度信息异常");
                // AppContext.cache().del(asyncKey);
                AppContext.cache().expire(asyncKey, 3600);
                renderJson(response, ResultMessage.error(MddMultilingualUtil.getFWMessage("P_YS_FW-PUB_MDD-BACK_0001065487", "获取导入进度信息异常") /* "获取导入进度信息异常" */));
            }
            Map<String, Object> processMap = JSON.parseObject(value);
            if (null != processMap.get("code") && 999 == (Integer) processMap.get("code")) {
                // AppContext.cache().del(asyncKey);
                AppContext.cache().expire(asyncKey, 3600);
                Object message = processMap.get("message");
                String errInfo;
                if (message instanceof List) {
                    errInfo = JSON.toJSONString(message);
                } else {
                    errInfo = String.valueOf(message);
                }
                // 暂时抛出业务异常
                throw new BusinessException(errInfo);
            } else {
                Object percentage = processMap.get("percentage");
                if (null != percentage && Double.parseDouble(percentage.toString()) >= 100 && null == processMap.get("data")) {
                    processMap.put("percentage", "99");
                }
                String result = ResultMessage.data(GsonHelper.ToJSon(processMap));
                renderJson(response, result);
            }
        } catch (Exception e) {
            log.error("获取导入进程失败" + e.getMessage(), e);
            AppContext.cache().del(asyncKey);
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

}
