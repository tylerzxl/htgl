package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.enums.OperationTypeEnum;
import com.yonyou.ucf.mdd.common.model.Pager;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyou.ucf.mdd.common.utils.json.GsonHelper;
import com.yonyou.ucf.mdd.core.dto.POIDto;
import com.yonyou.ucf.mdd.core.utils.DateKit;
import com.yonyou.ucf.mdd.core.utils.UIMetaHelper;
import com.yonyou.ucf.mdd.ext.api.IBillService;
import com.yonyou.ucf.mdd.ext.bill.dto.BillDataDto;
import com.yonyou.ucf.mdd.ext.bill.rule.common.ResultList;
import com.yonyou.ucf.mdd.ext.core.AppContext;
import com.yonyou.ucf.mdd.ext.i18n.utils.MddMultilingualUtil;
import com.yonyou.ucf.mdd.ext.model.LoginUser;
import com.yonyou.ucf.mdd.ext.poi.service.POIService;
import com.yonyou.ucf.mdd.ext.util.ResultMessage;
import com.yonyou.ucf.mdd.poi.api.IPerdicateHandler;
import com.yonyou.ucf.mdd.poi.itf.IPOIService;
import com.yonyou.ucf.mdd.poi.model.ExcelExportData;
import com.yonyou.ucf.mdd.poi.model.StreamParam;
import com.yonyou.ucf.mdd.poi.service.DefaultPerdicateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.imeta.biz.base.BizException;
import org.imeta.spring.support.cache.RedisManager;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Controller
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillController extends BaseController {

    private final IBillService billService;

    private final ApplicationContext applicationContext;

    private final IPOIService poiService;

    @RequestMapping("/list")
    public void list(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        Pager pager = null;
        try {
            pager = billService.queryByPage(bill);
            renderJson(response, ResultMessage.data(pager));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when query bill list", e);
        }

    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/detail")
    public void detail(String billnum, String id, @RequestParam(required = false) String terminalType, HttpServletRequest request, HttpServletResponse response) {
        try {
            BillDataDto bill = new BillDataDto();
            bill.setBillnum(billnum);
            bill.setId(id + "");
            bill.setTenantId(MddBaseContext.getTenantId());
            bill.setUserId(MddBaseContext.getThreadContext("userId"));
            bill.setTerminalType(terminalType);
            Map map = billService.detail(bill);
            renderJson(response, ResultMessage.data(map));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when query bill detail", e);
        }

    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/querytree")
    public void querytree(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            List list = billService.querytree(bill);
            renderJson(response, ResultMessage.data(list));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when query bill tree", e);
        }

    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/enter")
    public void enter(String billnum, String id, HttpServletRequest request, HttpServletResponse response) {
        try {
            BillDataDto bill = new BillDataDto();
            bill.setBillnum(billnum);
            bill.setId(id + "");
            Map map = billService.enter(bill);
            renderJson(response, ResultMessage.data(map));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when do bill enter", e);
        }

    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/movefirst")
    public void movefirst(String billnum, HttpServletRequest request, HttpServletResponse response) {

        try {
            BillDataDto bill = new BillDataDto();
            bill.setBillnum(billnum);
            Map map = billService.movefirst(bill);
            renderJson(response, ResultMessage.data(map));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when bill move first", e);
        }

    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/moveprev")
    public void moveprev(String billnum, String id, HttpServletRequest request, HttpServletResponse response) {

        try {
            BillDataDto bill = new BillDataDto();
            bill.setBillnum(billnum);
            bill.setId(id + "");
            Map map = billService.moveprev(bill);
            renderJson(response, ResultMessage.data(map));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when bill move prev", e);
        }

    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/movenext")
    public void movenext(String billnum, String id, HttpServletRequest request, HttpServletResponse response) {

        try {
            BillDataDto bill = new BillDataDto();
            bill.setBillnum(billnum);
            bill.setId(id + "");
            Map map = billService.movenext(bill);
            renderJson(response, ResultMessage.data(map));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when bill move next", e);
        }

    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/movelast")
    public void movelast(String billnum, HttpServletRequest request, HttpServletResponse response) {

        try {
            BillDataDto bill = new BillDataDto();
            bill.setBillnum(billnum);
            Map map = billService.movelast(bill);
            renderJson(response, ResultMessage.data(map));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when bill move last", e);
        }

    }

    @RequestMapping("/add")
    public void add(@RequestBody BillDataDto bill, @RequestParam(required = false) String terminalType, HttpServletRequest request, HttpServletResponse response) {

        try {
            String billnum = bill.getBillnum();
            BillDataDto baseReqDto = new BillDataDto();
            baseReqDto.setBillnum(billnum);
            baseReqDto.setTerminalType(terminalType);
            String json = billService.add(baseReqDto);
            renderJson(response, ResultMessage.toMap(json, true));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when do bill add", e);
        }

    }

    @RequestMapping("/delete")
    public void delete(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeVoid(bill, false, request, response);
    }

    @RequestMapping("/save")
    public void save(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, false, request, response);
    }

    @RequestMapping("/audit")
    public void audit(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, false, request, response);
    }

    @RequestMapping("/unaudit")
    public void unaudit(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, true, request, response);
    }

    @RequestMapping("/close")
    public void close(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, false, request, response);
    }

    @RequestMapping("/open")
    public void open(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, true, request, response);
    }

    @RequestMapping("/lock")
    public void lock(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, false, request, response);
    }

    @RequestMapping("/unlock")
    public void unlock(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, true, request, response);
    }

    @RequestMapping("/stop")
    public void stop(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, false, request, response);
    }

    @RequestMapping("/unstop")
    public void unstop(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, true, request, response);
    }

    @RequestMapping("/invalid")
    public void invalid(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, false, request, response);
    }

    @RequestMapping("/lineclose")
    public void lineclose(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, false, request, response);
    }

    @RequestMapping("/lineopen")
    public void lineopen(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, true, request, response);
    }


    @RequestMapping("/ref/getRefData")
    public void getRefData(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            Object result = billService.getRefData(bill);
            renderJson(response, ResultMessage.data(result));
        } catch (Throwable e) {
            renderJson(response, ResultMessage.error(e.toString()));
        }
    }

    @RequestMapping(value = "/ref/getGridData")
    public void getGridData(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            Pager rev = billService.getGridData(bill);
            renderJson(response, ResultMessage.data(rev));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.toString()));
        }

    }

    /**
     * @author zhouchenwen 查询商品存量
     */
    @RequestMapping(value = "/ref/getCurrentStock", method = RequestMethod.POST)
    public void getCurrentStock(@RequestBody Map<String, String> paramData, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            LoginUser user = AppContext.getCurrentUser();
            if (null == user) {
                throw new Exception(com.yonyou.iuap.ucf.common.i18n.MessageUtils.getMessage("P_YS_PF_PROCENTER_0000023225") /* "请登录后再操作！" */);
            }
            String skuIds = paramData.get("skuIds");
            Long warehouseId = Long.parseLong(paramData.get("warehouseId"));
            JSONArray skuIdArray = JSONArray.fromObject(skuIds);
            if (skuIdArray == null || skuIdArray.size() == 0) {
                throw new Exception(com.yonyou.iuap.ucf.common.i18n.MessageUtils.getMessage("P_YS_PF_PROCENTER_0000023219") /* "商品sku主键不能为空！" */);
            }

        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.toString()));
        }
    }


    /**
     * <strong>数据交换处理</strong><br>
     * <br>
     *
     * @param bill
     * @param request
     * @param response
     * @author shenlei
     * @date 2017年10月24日 下午7:59:08
     */
    @RequestMapping(value = "/exchange", method = RequestMethod.POST)
    public void exchange(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        executeData(bill, true, request, response);
    }

    @PostMapping("/exec/{action}")
    public void executeAction(@RequestBody BillDataDto bill, @PathVariable String action, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (StringUtils.isEmpty((bill.getAction()))) {
                bill.setAction(action);
            }
            RuleExecuteResult result = billService.executeUpdate(bill.getAction(), bill);
            renderJson(response, ResultMessage.data(result.getData()));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    @RequestMapping("/copy")
    public void copy(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            String json = billService.copy(bill);
            renderJson(response, ResultMessage.toMap(json, true));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("exception when do bill copy", e);
        }
    }


    private void executeData(BillDataDto bill, boolean isHoldNull, HttpServletRequest request, HttpServletResponse response) {
        try {
            String action = Thread.currentThread().getStackTrace()[2].getMethodName();
            RuleExecuteResult result = billService.executeUpdate(action, bill);
            renderJson(response, ResultMessage.data(result.getData(), isHoldNull));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    private void executeVoid(BillDataDto bill, boolean isHoldNull, HttpServletRequest request, HttpServletResponse response) {
        try {
            String action = Thread.currentThread().getStackTrace()[2].getMethodName();
            billService.executeUpdate(action, bill);
            renderJson(response, ResultMessage.success());
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    /**
     * 批量删除
     *
     * @param bill
     * @param request
     * @param response
     */
    @RequestMapping("/batchdelete")
    public void batchdelete(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (bill.getData() != null) {
                bill.setAction(request.getParameter("action"));
                ResultList resultList = billService.batchdelete(bill);
                renderJson(response, ResultMessage.data(resultList));
            } else {
                renderJson(response, ResultMessage.error("no data"));
            }
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    @RequestMapping("/check")
    public void check(@RequestBody BillDataDto checkItem, HttpServletRequest request, HttpServletResponse response) {
        try {
            RuleExecuteResult result = billService.check(checkItem);
            renderJson(response, ResultMessage.data(result.getData()));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }


    @RequestMapping("/submit")
    public void submit(@RequestBody BillDataDto baseReqDto, HttpServletRequest request, HttpServletResponse response) {
        try {
            RuleExecuteResult result = billService.executeUpdate(OperationTypeEnum.SUBMIT.getValue(), baseReqDto);
            renderJson(response, ResultMessage.data(result.getData(), false));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    @RequestMapping("/unsubmit")
    public void unsubmit(@RequestBody BillDataDto baseReqDto, HttpServletRequest request, HttpServletResponse response) {
        try {
            RuleExecuteResult result = billService.executeUpdate(OperationTypeEnum.UNSUBMIT.getValue(), baseReqDto);
            renderJson(response, ResultMessage.data(result.getData(), true));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    /**
     * 批量提交
     *
     * @param bill
     * @param request
     * @param response
     */
    @RequestMapping("/batchsubmit")
    public void batchsubmit(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (bill.getData() != null) {
                bill.setAction(request.getParameter("action"));
                ResultList resultList = billService.batchsubmit(bill);
                renderJson(response, ResultMessage.data(resultList));
            } else {
                renderJson(response, ResultMessage.error("no data"));
            }
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    /**
     * 批量撤回
     *
     * @param bill
     * @param request
     * @param response
     */
    @RequestMapping("/batchunsubmit")
    public void batchunsubmit(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (bill.getData() != null) {
                bill.setAction(OperationTypeEnum.UNSUBMIT.getValue());
                ResultList resultList = billService.batchDo(bill);
                renderJson(response, ResultMessage.data(resultList));
            } else {
                renderJson(response, ResultMessage.error("no data"));
            }
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    @RequestMapping("/batchaudit")
    public void batchaudit(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (bill.getData() != null) {
                bill.setAction(OperationTypeEnum.AUDIT.getValue());
                ResultList resultList = billService.batchDo(bill);
                renderJson(response, ResultMessage.data(resultList));
            } else {
                renderJson(response, ResultMessage.error("no data"));
            }
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    @RequestMapping("/batchunaudit")
    public void batchunaudit(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (bill.getData() != null) {
                bill.setAction(OperationTypeEnum.UNAUDIT.getValue());
                ResultList resultList = billService.batchDo(bill);
                renderJson(response, ResultMessage.data(resultList));
            } else {
                renderJson(response, ResultMessage.error("no data"));
            }
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    @RequestMapping("/batchDo")
    public void batchDo(@RequestBody BillDataDto bill, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (bill.getData() != null) {
                String action = request.getParameter("action");
                bill.setAction(action);
                ResultList resultList = billService.batchDo(bill);
                renderJson(response, ResultMessage.data(resultList));
            } else {
                renderJson(response, ResultMessage.error("no data"));
            }
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    @PostMapping("/executeFormulaCalculate")
    public void executeFormulaCalculate(@RequestBody BillDataDto baseReqDto, HttpServletRequest request, HttpServletResponse response) {
        try {
            //TODO trigger fields
            RuleExecuteResult result = billService.executeUpdate("recalculate", baseReqDto);
            renderJson(response, ResultMessage.data(result.getData(), true));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    /**
     * 单据 导入数据
     *
     * @param file
     * @param billnum
     * @param request
     * @param response
     */
    @RequestMapping("/billImport")
    public void billImport(@RequestParam("file") MultipartFile file,
                           @RequestParam(value = "billnum", required = false) String billnum, @RequestParam(value = "asyncKey", required = false) String asyncKey,
                           @RequestParam(required = false) String mapCondition, HttpServletRequest request,
                           HttpServletResponse response) {
        try {
            log.debug("bill import with asyncKey {}", asyncKey);
            Map<String, Object> params = new HashMap<>();
            params.put("billnum", billnum);
            params.put("asyncKey", asyncKey);
            params.put("mapCondition", mapCondition);
            ResultList resultList = null;
            if (StringUtils.isNotBlank(asyncKey)) {
                CompletableFuture<ResultList> result = CompletableFuture.supplyAsync(() -> {
                    try {
                        return billService.billImport(params, file);
                    } catch (Exception e) {
                        RedisManager redis = AppContext.cache();
                        Map<String, Object> map = new HashMap<>();
                        map.put("data", e.getMessage());
                        map.put("flag", "0");
                        map.put("count", 0);
                        map.put("successCount", 0);
                        map.put("failCount", 0);
                        map.put("percentage", "100");
                        redis.set(asyncKey, GsonHelper.ToJSon(map));
                        throw new BizException(MddMultilingualUtil.getFWMessage("P_YS_FW-PUB_MDD-BACK_0001065338", "异步导入异常") /* "异步导入异常" */, e);
                    }
                });
            } else {
                Map<String, Object> datas = POIService.getImportData(file);
                resultList = billService.billImport(params, datas);
            }
            renderJson(response, ResultMessage.data(resultList));
        } catch (Exception e) {
            e.printStackTrace();
            if (!StringUtils.isEmpty(asyncKey)) {
                RedisManager redis = AppContext.cache();
                Map<String, Object> map = new HashMap<>();
                map.put("flag", "0");
                map.put("data", e.getMessage());
                redis.set(asyncKey, GsonHelper.ToJSon(map));
                log.error("异步导入失败", e);
            }
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }



    @RequestMapping("export")
    public void export(@RequestBody POIDto poiDto, HttpServletRequest request, HttpServletResponse response) {
        try {
            UIMetaBaseInfo baseInfo = UIMetaHelper.getUIMetaBaseInfo(poiDto.getBillnum(), MddBaseContext.getTenantId());
            POIDto queryBill=new POIDto();
            BeanUtils.copyProperties(poiDto,queryBill);
            queryBill.setAction("query");
            queryBill.setIsIncludeMeta(true);
            poiDto.setTenantId(MddBaseContext.getTenantId());
            poiDto.setUserId(MddBaseContext.getThreadContext("userId"));
            // 如果模板信息需要过滤需要传入ViewControlParams参数
            String[] ruleLvs = new String[3];
            ruleLvs[0] = "common";
            ruleLvs[1] = baseInfo.getSubid();
            ruleLvs[2] = baseInfo.getBillnum();
            poiDto.setRuleLvs(ruleLvs);
            IPerdicateHandler perdicateHandler = MddBaseContext.getBean(IPerdicateHandler.class);
            if (null == perdicateHandler) {
                perdicateHandler = new DefaultPerdicateHandler();
            }
            ExcelExportData excelData = poiService.export(poiDto, perdicateHandler);
            String fileName = org.apache.commons.lang3.StringUtils.isNotBlank(poiDto.getFileName()) ? poiDto.getFileName() : poiDto.getBillnum() + DateKit.getCurrTime();
            excelData.setFileName(fileName);
            StreamParam streamParam = new StreamParam(fileName, excelData, response);
            poiService.downLoadToResponse(streamParam);
        } catch (Exception e) {
            log.error(e.getMessage());
            renderJson(response, com.yonyou.ucf.mdf.app.common.ResultMessage.error(e.getMessage()));
        }
    }


}