package com.yonyou.ucf.mdf.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.yonyou.common.bizflow.utils.HttpClientUtil;
import com.yonyou.iuap.print.client.utils.PrintUrlUtil;
import com.yonyou.iuap.utils.HttpTookit;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdd.common.exceptions.ExceptionSubCode;
import com.yonyou.ucf.mdd.common.exceptions.MddMsgException;
import com.yonyou.ucf.mdd.common.interfaces.context.ISimpleUser;
import com.yonyou.ucf.mdd.common.model.enums.EnumBase;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyou.ucf.mdd.core.utils.UIMetaHelper;
import com.yonyou.ucf.mdd.ext.api.IBillService;
import com.yonyou.ucf.mdd.print.itf.IPrintService;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import com.yonyou.ucf.mdf.app.util.CommonUtil;
import com.yonyou.ucf.mdf.app.util.PubUtils;
import com.yonyou.ucf.mdf.app.util.RuleEngineUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yonyou.ucf.mdf.app.util.ProcEnumUtil.procEnum;


/**
 * 云打印相关的服务
 * 云打印所有的服务请求都是form表单的形式发送的
 */
@RestController
@RequestMapping("/print/")
@Slf4j
public class BillPrintController extends BaseController {

    private static final String PRINTPREVIEW = "/print/previewPrintTemplate";
    private static final String PRINTPREVIEWAPI = "/getTemplateContent";
    private static final String EXTKEY_BIZDOMAINCODE = "bizDomainCode";

    @Value("${self.service.host}")
    private String selfHost;

    @Autowired
    @Qualifier("mddPrintService")
    private IPrintService printService;
    @Autowired
    private IBillService billService;

    /**
     * 通过业务对象中所有的模版
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/getTemplateByBo")
    public void getTemplateByBo(@RequestParam(value = "billno", required = true) String billno, @RequestParam(value = "domain", required = false) String domain,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            if (StringUtils.isBlank(billno)) {
                throw new MddMsgException("参数为空", ExceptionSubCode.PARAM_IS_NULL);
            }
//            String appCode = getAppCode(billno);
            if (StringUtils.isNotBlank(domain)) {
                billno = domain + "." + billno;
            }
            String appCode = null;// appCode和业务域在mdd中处理 --yanx于2020/6/11注释
            List<Map<String, Object>> tempaltes = printService.getTemplateByBo(appCode, billno);
            renderJson(response, ResultMessage.data(tempaltes));
        } catch (Exception e) {
            log.info("通过租户获取业务对象异常" + e.getMessage());
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }


    public String getAppCode(String billNo) throws Exception {
        BaseReqDto bill = new BaseReqDto();
        bill.setBillnum(billNo);
        bill.setTenantId(CommonUtil.getTenantId());
        bill.setUserId(CommonUtil.getUserId());
        UIMetaBaseInfo uiinfo = UIMetaHelper.getUIMetaBaseInfo(billNo, MddBaseContext.getTenantId());
        return uiinfo.getSubid();
    }

    private boolean isGoodJson(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        } else {
            try {
                (new JsonParser()).parse(json);
                return true;
            } catch (JsonParseException var3) {
                return false;
            }
        }
    }

    /**
     * 预览
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("printPreview")
    public void printPreview(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            log.info("[BillPrintController] printPreview 参数===" + params);
            String contextPath = request.getContextPath();
            String resUrl = printPreview(params, contextPath);
            renderJson(response, ResultMessage.data(resUrl));
        } catch (Exception e) {
            log.info("预览失败" + e.getMessage());
            e.printStackTrace();
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    /**
     * 打印获取运行态数据
     *
     * @param request
     * @param response
     */
    @RequestMapping("/getTemplateContent")
    public void detail(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        String billnum = (String) params.get("billno");
        String id = ((List<String>) params.get("ids")).get(0);
        Long groupSchemaId = 0L;
        try {
            BaseReqDto bill = new BaseReqDto();
            bill.setId(id);
            bill.setBillnum(billnum);
            bill.setTenantId(CommonUtil.getTenantId());
            bill.setUserId(CommonUtil.getUserId());
            bill.setGroupSchemaId(groupSchemaId);
            Map<String, Object> map = (Map<String, Object>) billService.detail(bill);
            Map<String, String> enumMap = getEnumMap(billnum, CommonUtil.getTenantId());
            JSONObject jsonObject = convertData(billnum, (List<String>) params.get("ids"), map, enumMap);
            //处理枚举
            renderJson(response, ResultMessage.data(jsonObject.toJSONString()));
        } catch (Exception e) {
            log.info("获取打印内容失败{}", e.getMessage());
            e.printStackTrace();
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    @RequestMapping(value = "/previewPrintTemplate", method = RequestMethod.GET)
    public void previewPrintTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String printHtml = this.getPreviewEditTemplate();
        response.setContentType("text/html; charset=utf-8");
        response.getWriter().print(printHtml);
        response.flushBuffer();
    }

    /**
     * 获得预览打印模板的URL
     *
     * @return
     */
    protected String getPreviewEditTemplate() {
        String previewURL = PrintUrlUtil.getpreviewUrl();
        return HttpTookit.doGet(previewURL, null);
    }

    /**
     * 打印预览内部提供
     *
     * @param params
     * @return
     */
    public String printPreview(Map<String, Object> params, String contextPath) throws Exception {
        ISimpleUser user = MddBaseContext.getCurrentUser();
        String yxyTenantId = user.getTenantId();
        params.put("tenantId", yxyTenantId);
        Object templateObj = params.get("template");
        if (templateObj == null || StringUtils.isEmpty(templateObj.toString())) {
            throw new Exception("模版Code不能为空");
        }
        if (log.isInfoEnabled()) {
            log.info("printPreview json : {}", JSON.toJSONString(params));
        }

        String bizDomainCode = (String) params.get(EXTKEY_BIZDOMAINCODE);
        String paramStr = URLEncoder.encode(JSON.toJSONString(params), "utf-8");
        String url = selfHost + PRINTPREVIEW + "?tenantId=" + yxyTenantId + "&template=" + templateObj + "&printcode=" + templateObj + "&params=" + paramStr + "&sendType=post&enctype=json";
        String routeUrl = "/print";// node端转发到后台
        //print  bill
//        Object routeUrlObjct = params.get("route");
//        if (routeUrlObjct != null && !StringUtils.isEmpty(routeUrlObjct.toString()))
//            routeUrl = routeUrlObjct.toString();
        url = url + "&serverUrl=" + routeUrl + PRINTPREVIEWAPI;///uniform
        if (null != bizDomainCode) {
            url += "&domainDataBaseByCode=" + bizDomainCode;
        }
        return url;
    }


    public String getUiBaseInfo(String billNo, List<String> ids) throws Exception {
        BaseReqDto bill = new BaseReqDto();
        bill.setIds(JSONObject.toJSONString(ids));
        bill.setBillnum(billNo);
        bill.setTenantId(CommonUtil.getTenantId());
        bill.setUserId(CommonUtil.getUserId());
        RuleContext ruleContext = RuleEngineUtils.prepareRuleContext(bill, "printnow");
        UIMetaBaseInfo uiinfo = ruleContext.getUiMetaBaseInfo();
//        String fullName = uiinfo.getFullname();
//        org.imeta.core.model.Entity entity = BizContext.getMetaRepository().entity(fullName);
        return uiinfo.getEntityCode();
    }

    public JSONObject convertData(String billnum, List<String> ids, Map<String, Object> map, Map<String, String> enumMap) throws Exception {
        String mainCode = getUiBaseInfo(billnum, ids);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject jsonObject = new JSONObject();//最后返回
        List<Map> mainMap = new ArrayList<>();
        Map<String, Object> entityMap = new HashMap<>();
        mainMap.add(entityMap);
        String childKey = "";//子表主键
        jsonObject.put(mainCode, mainMap);//主表map
        String mainId = "";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if ("id".equals(entry.getKey())) {
                mainId = entry.getValue().toString();
            }
            if ("pubts".equals(entry.getKey())) {
//                Date date = new Date(Timestamp.valueOf(entry.getValue().toString()).getTime());
//                String time = simpleDateFormat.format(date);
                // mdd给的值就是Date类型,无需再做转换 --yanx于2020/6/15注释
                entityMap.put(entry.getKey(), entry.getValue());
                continue;
            }
            if (entry.getValue() instanceof ArrayList) {
                jsonObject.put(entry.getKey(), entry.getValue());
                childKey = entry.getKey();
            } else {//主表
                entityMap.put(entry.getKey(), procEnum(enumMap, entry));
            }
        }
        if (!StringUtils.isEmpty(childKey)) {
            String cid = "";
            JSONArray jsonArray = jsonObject.getJSONArray(childKey);
            String childKeyId = childKey + "_id";
            List<JSONObject> newJson = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {//遍历子表
                JSONObject obj = jsonArray.getJSONObject(i);
                for (String key : obj.keySet()) {//遍历处理枚举
                    obj.put(key, procEnum(enumMap, key, obj.getString(key)));
                }
                cid = obj.getString("id");
                obj.put("id", mainId);
                obj.put(childKeyId, cid);
                // 日期类型暂不做特殊处理 --yanx于2020/7/1注释
//                if (obj.containsKey("pubts")) {
//                    Date date = new Date(Timestamp.valueOf(obj.get("pubts").toString()).getTime());
//                    String time = simpleDateFormat.format(date);
//                    obj.put("pubts", time);
//                }

                newJson.add(obj);
            }
            jsonObject.remove(childKey);
            jsonObject.put(childKey, newJson);
        }
        return jsonObject;
    }


    private Map<String, String> genReqHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return PubUtils.genYHTAccessTokenReqHeader(request);
    }


    public Map<String, String> getEnumMap(String billno, String tenantId) throws Exception {
        Map<String, String> enumMap = new HashMap<>();

        Map<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(billno) && billno.endsWith("List")) {
            billno = billno.substring(0, billno.indexOf("List"));
        }
        String url = MddBaseContext.getEnvConfig("metadata.ui.url") + "/mdf/union/designmeta/getMetaData";

        BasicNameValuePair nameValuePair = new BasicNameValuePair("billNo", billno);
        BasicNameValuePair nameValuePair2 = new BasicNameValuePair("tenantId", tenantId);
        BasicNameValuePair nameValuePair3 = new BasicNameValuePair("domain", MddBaseContext.getEnvConfig("spring.application.name"));
        List<NameValuePair> list = new ArrayList<>();
        list.add(nameValuePair);
        list.add(nameValuePair2);
        list.add(nameValuePair3);
        Map<String, String> mapHeader = this.genReqHeader();
        String result = HttpClientUtil.httpsGet(url, mapHeader, list);
        //com.yonyou.ucf.mdf.app.bizflow.utils.HttpClientUtil.httpsGet(url, mapHeader, list);
        log.error(result);
        if (StringUtils.isEmpty(result)) {
            return new HashMap<>();
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.containsKey("code")) {
            String code = jsonObject.getString("code");
            if (!"200".equals(code)) {
                return new HashMap<>();
            }
        }
        JSONObject data = jsonObject.containsKey("data") ? jsonObject.getJSONObject("data") : new JSONObject();
        JSONObject viewApplication = data.containsKey("viewApplication") ? data.getJSONObject("viewApplication") : new JSONObject();
        JSONObject view = viewApplication.containsKey("view") ? viewApplication.getJSONObject("view") : new JSONObject();
        JSONArray containers = view.containsKey("containers") ? view.getJSONArray("containers") : new JSONArray();
        if (containers == null || containers.size() == 0) {
            return new HashMap<>();
        }
        for (int i = 0; i < containers.size(); i++) {
            JSONObject contain = containers.getJSONObject(i);
            JSONArray containers2 = contain.getJSONArray("containers");
            if (containers2 == null || containers2.size() == 0) {
                continue;
            }
            for (int j = 0; j < containers2.size(); j++) {
                JSONObject con = containers2.getJSONObject(j);
                JSONArray controls = con.getJSONArray("controls");
                if (controls == null || controls.size() == 0) {
                    continue;
                }
                for (int k = 0; k < controls.size(); k++) {
                    JSONObject jsonObject1 = controls.getJSONObject(k);
                    if (jsonObject1.containsKey("bEnum") && jsonObject1.getBoolean("bEnum") && jsonObject1.containsKey("cEnumString")) {
                        String cEnumString = jsonObject1.getString("cEnumString");
                        String cItemName = jsonObject1.getString("cItemName");
                        enumMap.put(cItemName, cEnumString);
                    } else if (jsonObject1.containsKey("bEnum") && jsonObject1.getBoolean("bEnum") && !StringUtils.isEmpty("cEnumType")) {
                        String cEnumType = jsonObject1.getString("cEnumType");
                        String cItemName = jsonObject1.getString("cItemName");
                        JSONArray jsonArray = getEnumBases(cEnumType);//枚举系统字段 构造枚举json
                        if (!jsonArray.isEmpty()) {
                            enumMap.put(cItemName, jsonArray.toJSONString());
                        }
                    }
                }
            }
        }
        return enumMap;
    }

    public JSONArray getEnumBases(String enumType) {
        Map<String, Object> params = new HashMap<>();
        params.put("enumtype", enumType);
        List<EnumBase> list = MddBaseContext.uimetaDao().selectList("com.yonyou.ucf.mdd.uimeta.enumsMapper.getEnumByType", params);
        JSONArray jsonArray = new JSONArray();
        if (!list.isEmpty()) {
            list.forEach(enumbase -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", enumbase.getEnumCode());
                jsonObject.put("value", enumbase.getEnumName());
                jsonArray.add(jsonObject);
            });
        }
        return jsonArray;
    }
}
