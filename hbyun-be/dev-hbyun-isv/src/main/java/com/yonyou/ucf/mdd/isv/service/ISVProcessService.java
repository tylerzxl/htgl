package com.yonyou.ucf.mdd.isv.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yonyou.iuap.utils.CookieUtil;
import com.yonyou.ucf.mdd.bpm.model.BpmRequestBody;
import com.yonyou.ucf.mdd.bpm.model.BpmResponse;
import com.yonyou.ucf.mdd.bpm.uitls.WorkFlowUtils;
import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdd.common.enums.OperationTypeEnum;
import com.yonyou.ucf.mdd.common.enums.VerifyStateEnum;
import com.yonyou.ucf.mdd.common.exceptions.ExceptionSubCode;
import com.yonyou.ucf.mdd.common.exceptions.MddBpmException;
import com.yonyou.ucf.mdd.common.exceptions.MddMsgException;
import com.yonyou.ucf.mdd.common.interfaces.context.ISimpleUser;
import com.yonyou.ucf.mdd.common.model.SimpleUser;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyou.ucf.mdd.common.utils.json.GsonHelper;
import com.yonyou.ucf.mdd.core.utils.MetaAttributeUtils;
import com.yonyou.ucf.mdd.core.utils.UIMetaHelper;
import com.yonyou.ucf.mdd.ext.bill.meta.biz.BillMetaBiz;
import com.yonyou.ucf.mdd.ext.core.AppContext;
import com.yonyou.ucf.mdd.ext.model.BillContext;
import com.yonyou.ucf.mdd.rule.api.RuleEngine;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.imeta.core.lang.BooleanUtils;
import org.imeta.orm.base.BizObject;
import org.imeta.orm.base.EntityStatus;
import org.imeta.spring.support.json.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import yonyou.bpm.rest.BpmRest;
import yonyou.bpm.rest.BpmRests;
import yonyou.bpm.rest.RuntimeService;
import yonyou.bpm.rest.exception.RestException;
import yonyou.bpm.rest.param.BaseParam;
import yonyou.bpm.rest.request.AssignCheckParam;
import yonyou.bpm.rest.request.RestVariable;
import yonyou.bpm.rest.request.identity.BasicDataResourceParam;
import yonyou.bpm.rest.request.identity.BasicdataQueryParam;
import yonyou.bpm.rest.request.identity.OrgQueryParam;
import yonyou.bpm.rest.request.repository.ProcessDefinitionQueryParam;
import yonyou.bpm.rest.request.runtime.ProcessInstanceActionParam;
import yonyou.bpm.rest.request.runtime.ProcessInstanceParam;
import yonyou.bpm.rest.request.runtime.ProcessInstanceStartParam;
import yonyou.bpm.rest.request.task.TaskQueryParam;
import yonyou.bpm.rest.response.historic.HistoricProcessInstanceResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/7/8 1:46 下午
 */
@Slf4j
@Service
public class ISVProcessService {

    private static final String BPMRESTURL = "/ubpm-web-rest/";

    @Value("${bpmrest.server:#{null}}")
    private String serverUrl;
    @Value("${bpmrest.tenant:#{null}}")
    private String tenant;// 租户code; 管理端租户管理节点生成的token
    @Value("${bpmrest.token:#{null}}")
    private String token; // 租户对应的token
    @Value("${bpmrest.appsource:#{null}}")// 租户应用 source 租户表
    private String appsource;
    @Value("${bpmrest.completeUrl:#{null}}")
    private String completeUrl;//终审回调
    @Value("${bpmrest.frontUrl:#{null}}")
    private String frontUrl;//详情前端地址
    @Value("${bpmrest.completeRemote:false}")
    private boolean completeRemote;//详情前端地址

    @Getter
    @Value("${bpmrest.callbackToken:#{null}}")
    private String callbackToken;

    @Getter
    @Value("${bpmrest.checkToken:false}")
    private boolean checkToken;

    @Autowired
    private BillMetaBiz billMetaBiz;

    private static String getIdStr(BizObject bill) {
        return bill.get(MddConstants.PARAM_ID) == null ? "" : bill.get(MddConstants.PARAM_ID).toString();
    }

    public ISimpleUser getCurrentUser() {
        ISimpleUser user = null;
        try {
            user = MddBaseContext.getCurrentUser();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (user == null) {
            return new SimpleUser();
        } else {
            return user;
        }
    }

    public BpmRest bpmRestServices(String userId) {
        return bpmRestServices(userId, null, null);
    }

    public BpmRest bpmRestServices() {
        ISimpleUser user = getCurrentUser();
        String userId = user.getUserId() != null ? user.getUserId().toString() : "";
        String limitTenantId = user.getTenantId() != null ? user.getTenantId().toString() : null;
        String orgId = user.getOrgId();

        return bpmRestServices(userId, limitTenantId, orgId);
    }

    public BpmRest bpmRestServices(String userId, String limitTenantId, String org) {
        if (userId == null) {
//			throw new IllegalArgumentException("获取BpmRest时传入的userId[" + userId + "]是空");
            throw new IllegalArgumentException(String.format("获取BpmRest时传入的userId[%s]是空", userId));
        }
        BaseParam baseParam = new BaseParam();
        baseParam.setOperatorID(userId);
        // 1.U审rest服务地址：http://ys.yyuap.com/ubpm-web-rest
        baseParam.setServer(serverUrl + BPMRESTURL);

        // 2.==========rest安全调用=========begin
        // 租户code
        // 管理端租户管理节点生成的token
        baseParam.setTenant(tenant);

        //设置source，用于创建子应用
        baseParam.setSource(appsource);
        baseParam.setClientToken(token);

        //3.租户隔离，可为空，默认取rest安全多对应的token
        if (limitTenantId != null && !"".equals(limitTenantId.trim())) {
            baseParam.setTenantLimitId(limitTenantId);
        }

        String yhtAccessToken = MddBaseContext.getToken();
        if (StringUtils.isEmpty(yhtAccessToken) && null != RequestContextHolder.getRequestAttributes()) {
            yhtAccessToken = CookieUtil.findCookieValue(
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getCookies(),
                    MddConstants.PARAM_YHT_ACCESS_TOKEN);
        }
        baseParam.setYhtAccessToken(yhtAccessToken);

        baseParam.setStartProcessOrg(org); // 开启的ORG 是否通过组织进行控制 貌似和模板设置有关
        log.info("baseParam:{}", JSON.toJSONString(baseParam));
        return BpmRests.getBpmRest(baseParam);
    }


    // 批量开启流程 startProcessBatch

    /**
     * 开启流程
     *
     * @param uiMetaBaseInfo
     * @param bills
     * @throws Exception
     */
    public void startBpm(BillContext uiMetaBaseInfo, List<BizObject> bills) throws Exception {
        int submitCount = 0;
        for (BizObject bill : bills) {
            Object verifystate = bill.get(MddConstants.PARAM_BPM_VERIFYSTATE);
            if (null != verifystate) {
                if (VerifyStateEnum.SUBMITED.getValue() == Short.parseShort(verifystate.toString())) {
                    throw new MddMsgException("已提交", ExceptionSubCode.TIP_HAS_SUBMITED);
                }
                if (VerifyStateEnum.AUDITED.getValue() == Short.parseShort(verifystate.toString())) {
                    throw new MddMsgException("已审核", ExceptionSubCode.TIP_HAS_AUDITED);
                }
            }
            if (!bill.containsKey("mobileBillNo")) {
                bill.put("mobileBillNo", uiMetaBaseInfo.getBillnum() + "MobileArchive");
            }
            if (!bill.containsKey("mobileBillType")) {
                bill.put("mobileBillType", "yyarchive");
            }

            // mdd-bpm中补充单据类型 --yanx于2020/6/16注释
            String billTypeId = bill.get("billTypeId");
            if (null == billTypeId) {
                String billNum = uiMetaBaseInfo.getBillnum();
                String cardKey = uiMetaBaseInfo.getCardKey();
                if (StringUtils.isNotEmpty(cardKey) && !cardKey.equals(billNum)) {// 列表批审的单据类型用cardKey
                    billTypeId = cardKey;
                } else {
                    billTypeId = billNum;
                }

                if (StringUtils.isNotEmpty(appsource)) {
                    billTypeId = appsource + "." + billTypeId;
                }
            }

            List<RestVariable> variables = prepareVariables(bill);
            String keyFeature = JSONObject.toJSONString(bill);

            ISimpleUser user = MddBaseContext.getCurrentUser();
            String userId = user != null && user.getUserId() != null ? MddBaseContext.getCurrentUser().getUserId().toString() : null; //用户ID
            String limitTenantId = MddBaseContext.getTenantId() != null ? MddBaseContext.getTenantId().toString() : null; //租户ID
            String categoryId = getTransactionType(uiMetaBaseInfo, bill); // 交易类型
            String procInstName = getCode(uiMetaBaseInfo, bill);//billName_code
            String businessKey = String.format("%s_%s", uiMetaBaseInfo.getBillnum(), getIdStr(bill)); // billnum_id
            String org = getMasterOrg(uiMetaBaseInfo, bill); //组织

            HistoricProcessInstanceResponse response;
            try {
                response = startProcessByKey(userId, categoryId, billTypeId, procInstName, businessKey, variables, keyFeature, limitTenantId,
                        org);
            } catch (Exception e) {
                log.error("启动流程异常: " + e.getMessage());
                throw e;
            }

            bill.set(MddConstants.PARAM_BPM_IS_WF_CONTROLLED, true);
            bill.set(MddConstants.PARAM_BPM_VERIFYSTATE, VerifyStateEnum.SUBMITED.getValue());
            bill.set(MddConstants.PARAM_BPM_STARTDEPT, this.queryOrgByUserId(userId, "dept"));
            bill.set(MddConstants.PARAM_BPM_STARTORG, this.queryOrgByUserId(userId, "org"));
            bill.set(MddConstants.PARAM_BPM_PROCDEF, response.getProcessDefinitionId());
            bill.set(MddConstants.PARAM_BPM_PROCDEFINS, response.getId());
            bill.set("_status", EntityStatus.Update);
            String instanceId = response.getId();
            log.debug("流程开启 rMesponse : " + GsonHelper.ToJSon(response));
            log.debug("流程开启 response.instanceId : " + instanceId);
            submitCount++;
        }

        if (submitCount == 0) {
            throw new MddMsgException("没有需要提交的数据", ExceptionSubCode.NO_DATA_TO_BE_SUBMIT);
        }

        WorkFlowUtils.mddMetaDaoHelp().update(uiMetaBaseInfo.getFullname(), bills, true);
    }

    /**
     * 撤回
     */
    public void withdraw(BillContext uiMetaBaseInfo, List<BizObject> bills) throws Exception {
        for (BizObject bill : bills) {
            String businessKey = String.format("%s_%s", uiMetaBaseInfo.getBillnum(), getIdStr(bill));
            JsonNode responseNode = (JsonNode) bpmRestServices().getRuntimeService()
                    .withDrawFormUseBusinessKey(businessKey, null, null, null);

            log.debug("返回结果 ：" + GsonHelper.ToJSon(responseNode));
            handleResult(responseNode);
            bill.set(MddConstants.PARAM_BPM_VERIFYSTATE, VerifyStateEnum.INIT.getValue());
        }
    }

    public List<BizObject> abandonAudit(BillContext uiMetaBaseInfo, List<BizObject> bills) throws Exception {

        List<BizObject> list = new ArrayList<>();
        for (BizObject bill : bills) {
            BizObject bizObject = new BizObject();
            bizObject.setId(bill.getId());
            bizObject.setEntityStatus(EntityStatus.Update);
            bizObject.set(MddConstants.PARAM_BPM_VERIFYSTATE, VerifyStateEnum.INIT.getValue());
            if (!BooleanUtils.b(bill.get("isWfControlled"))) {
                log.debug("非流程受控对象continue");
                list.add(bizObject);
                continue;
            }
            String businessKey = String.format("%s_%s", uiMetaBaseInfo.getBillnum(), getIdStr(bill)); // billnum_id
            String processInstanceId = getProcessInstanceID(businessKey);
            String categoryId = getTransactionType(uiMetaBaseInfo, bill); // 交易类型
            if (StringUtils.isBlank(processInstanceId)) {
                continue;
            }

            String taskId = getTaskId(processInstanceId, businessKey, categoryId);
            boolean abandonResult = bpmRestServices().getTaskService().withdrawTask(taskId);
            bizObject.set("_abandonAudited", abandonResult);
            list.add(bizObject);
        }
        return list;
    }

    /**
     * 通过businesskey 查实例
     */
    public String getProcessInstanceID(String businessKey) throws Exception {
        ProcessInstanceParam param = new ProcessInstanceParam();
        param.setBusinessKey(businessKey);
        JsonNode responseNode = (JsonNode) bpmRestServices().getRuntimeService().getProcessInstances(param);

        log.debug("返回结果 ：" + GsonHelper.ToJSon(responseNode));

        return handleResult(responseNode);
    }

    public String getTaskId(String processInstanceId, String businessKey, String categoryId) throws Exception {

        TaskQueryParam param = new TaskQueryParam();
        param.setProcessInstanceId(processInstanceId);
        param.setProcessInstanceBusinessKey(businessKey);
        param.setCategoryId(categoryId);
        JsonNode responseNode = (JsonNode) bpmRestServices().getTaskService().queryTasks(param);
        log.debug("返回结果 ：" + GsonHelper.ToJSon(responseNode));
        return handleResult(responseNode);
    }

    /**
     * 保存时判断是否受审批流控制
     */
    public boolean bpmControl(BillContext uiMetaBaseInfo, BizObject bill) throws Exception {
        String transtype = getTransactionType(uiMetaBaseInfo, bill);
        String billTypeId = "" + bill.get("billTypeId");// 单据类型
        String org = getMasterOrg(uiMetaBaseInfo, bill);
        return existsProcessDefinition(transtype, billTypeId, org);
    }

    public void updateBpmProcess(BillContext uiMetaBaseInfo, BizObject bill) throws Exception {
        List<RestVariable> variables = prepareVariables(bill);
        String businessKey = String.format("%s_%s", uiMetaBaseInfo.getBillnum(), getIdStr(bill));
        HistoricProcessInstanceResponse response = updateProcessVariables(businessKey, variables);

        String instanceId = response.getId();
        log.info("流程开启 response : " + GsonHelper.ToJSon(response));
        log.debug("流程开启 response.instanceId : " + instanceId);

    }

    /**
     * 注册终审接口
     */
    public void registerInterface() throws Exception {
        ISimpleUser user = getCurrentUser();
        registerInterface(user.getUserId(), user.getTenantId(), null);
    }

    /**
     * 注册终审接口
     */
    public String registerInterface(String userId, String limitTenantId, String id) throws Exception {
        BasicDataResourceParam basicDataResouceParam = new BasicDataResourceParam();

        basicDataResouceParam.setCode("complete"); //编码
        basicDataResouceParam.setName("终审"); //描述
        basicDataResouceParam.setType("process_listener");
        if (StringUtils.isNotBlank(limitTenantId)) {
            basicDataResouceParam.setTenantId(String.format("%s_%s", limitTenantId, appsource)); // f8i8k0ut_AM
        }
        basicDataResouceParam.setUrl(completeUrl + "/bpm/complete");

        if (StringUtils.isNotBlank(callbackToken)) {
            basicDataResouceParam.setToken(callbackToken);
        }
        basicDataResouceParam.setSource(appsource);

        JsonNode responseNode;

        if (StringUtils.isNotBlank(id)) {
            basicDataResouceParam.setId(id);
        }

        responseNode = (JsonNode) bpmRestServices(userId, limitTenantId, null).getIdentityService().insertBasicData(basicDataResouceParam);
        log.info("registerInterface 完成 : " + GsonHelper.ToJSon(responseNode));
        return handleResult(responseNode);
    }

    public String QueryRegisterInterface(String userId, String limitTenantId, String category, String queryModelId) throws Exception {
        BasicdataQueryParam basicdataQueryParam = new BasicdataQueryParam();
        basicdataQueryParam.setId(queryModelId);
        basicdataQueryParam.setCode("complete"); //编码
        basicdataQueryParam.setName("终审"); //描述
        basicdataQueryParam.setType("process_listener");
        basicdataQueryParam.setCategory(category);
        if (StringUtils.isNotBlank(limitTenantId)) {
            basicdataQueryParam.setTenantId(String.format("%s_%s", limitTenantId, appsource)); // f8i8k0ut_AM
        }
        basicdataQueryParam.setSource(appsource);

        JsonNode responseNode;
        responseNode = (JsonNode) bpmRestServices(userId, limitTenantId, null).getIdentityService().queryBasicdatas(basicdataQueryParam);
        return handleResult(responseNode);
    }

    /**
     * 详情地址
     *
     * @param userId
     * @param limitTenantId
     * @param isUpdate
     * @param queryModelId
     * @throws Exception
     */
    public String registerDetailAddress(String userId, String limitTenantId, boolean isUpdate, String queryModelId) throws Exception {
        return registerDetailAddress(userId, limitTenantId, isUpdate, queryModelId, null);
    }

    public String registerDetailAddress(String userId, String limitTenantId, boolean isUpdate, String queryModelId, String orgid) throws Exception {
        BasicDataResourceParam basicDataResouceParam = new BasicDataResourceParam();

        basicDataResouceParam.setCode("detail"); //编码
        basicDataResouceParam.setName("详情"); //描述
        basicDataResouceParam.setType("business_url");
        if (StringUtils.isNotBlank(queryModelId)) {
            basicDataResouceParam.setId(queryModelId);
        }
        if (StringUtils.isNotBlank(limitTenantId)) {
            basicDataResouceParam.setTenantId(String.format("%s_%s", limitTenantId, appsource)); // f8i8k0ut_AM
        }

        basicDataResouceParam.setUrl(frontUrl + "/meta/voucher/");

        basicDataResouceParam.setToken(token);
        basicDataResouceParam.setSource(appsource);

        JsonNode responseNode;
        if (isUpdate) {
            responseNode = (JsonNode) bpmRestServices(userId, limitTenantId, orgid).getIdentityService().updateBasicData(basicDataResouceParam);
        } else {
            responseNode = (JsonNode) bpmRestServices(userId, limitTenantId, orgid).getIdentityService().insertBasicData(basicDataResouceParam);
        }

        log.debug(responseNode.toString());
        return handleResult(responseNode);
    }

    //==================================================================================================================

    public String complete(BpmRequestBody params) {
        log.info("bpmComplete param:{}", JSON.toJSONString(params));
        try {
            BaseReqDto dto = checkCompleteIdentity(params);
            UIMetaBaseInfo uiMetaBaseInfo = UIMetaHelper.getUIMetaBaseInfo(dto.getBillnum(), MddBaseContext.getTenantId());
            if (null == uiMetaBaseInfo) {
                throw new MddMsgException("没有对应表单", ExceptionSubCode.BILL_TPL_NULL);
            }

            RuleExecuteResult result;
            //perpar rulecontext
            uiMetaBaseInfo.setAction(OperationTypeEnum.AUDIT.getValue());
            if (StringUtils.isNotBlank(params.getDeleteReason())) {
                uiMetaBaseInfo.setDeleteReason(params.getDeleteReason());
            } else {
                uiMetaBaseInfo.setDeleteReason(params.getEventName());
            }
            RuleContext ruleContext = new RuleContext();
            ruleContext.setOperateType(OperationTypeEnum.AUDIT);
            ruleContext.setUiMetaBaseInfo(uiMetaBaseInfo);
            ruleContext.setCusMapValue(MddConstants.PARAM_PARAM, dto);
            String[] ruleLvs = new String[3];
            ruleLvs[0] = "common";
            ruleLvs[1] = uiMetaBaseInfo.getSubid();
            ruleLvs[2] = uiMetaBaseInfo.getBillnum();
            ruleContext.setRuleLvs(ruleLvs);

            result = RuleEngine.getInstance().execute(ruleContext);

            if (result.getMsgCode() != 1) {
                throw new MddBpmException(result.getMessage());
            }
            return BpmResponse.success();
        } catch (Exception e) {
            log.error("bpm complete error:", e);
            return BpmResponse.fail(e.getMessage());
        }

    }

    /**
     * 构造接口变量
     */
    public List<RestVariable> prepareVariables(BizObject bill) {
        List<RestVariable> variables = new ArrayList<>();
        for (Map.Entry<String, Object> entry : bill.entrySet()) {
            if (!entry.getKey().startsWith("_") && null != entry.getValue()) {
                if (!(entry.getValue() instanceof List)) {
                    RestVariable v2 = new RestVariable();
                    v2.setName(entry.getKey());
                    if (entry.getValue() instanceof Date) {
                        v2.setValue(getISO8601Timestamp((Date) entry.getValue()));
                        v2.setType("date");
                    } else {
                        v2.setValue(entry.getValue());
                    }
                    variables.add(v2);
                }
            }
        }
        return variables;
    }

    /**
     * ISO8601,效果2019-05-23T05:28:38.786Z
     */
    private String getISO8601Timestamp(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return df.format(date);
    }


    /**
     * 根据流程定义、租户ID和业务key启动流程实例
     */
    public HistoricProcessInstanceResponse startProcessByKey(String userId,
                                                             String categoryId,
                                                             String billTypeId,
                                                             String procInstName,
                                                             String businessKey,
                                                             List<RestVariable> variables,
                                                             String keyFeature,
                                                             String limitTenantId,
                                                             String org) throws Exception {
        log.info("启动流程。流程变量数据={}", GsonHelper.ToJSon(variables));
        RuntimeService rt = bpmRestServices(userId, limitTenantId, org).getRuntimeService();
        ProcessInstanceStartParam parm = new ProcessInstanceStartParam();
        parm.setVariables(variables);
        parm.setKeyFeature(keyFeature);
        parm.setProcessInstanceName(procInstName);
        parm.setBusinessKey(businessKey);
        parm.setCategory(categoryId);
        parm.setBillTypeId(billTypeId);
        ObjectNode node = (ObjectNode) rt.startProcess(parm);
        log.info("开启流程 接口调用结果: " + node.toString());

        return JSON.parseObject(node.toString(), HistoricProcessInstanceResponse.class);
    }

    /**
     * @param rtService
     * @param processDefinitionKey
     * @return
     */
    private boolean checkAssign(RuntimeService rtService, String processDefinitionKey) throws RestException {
        AssignCheckParam paramAssignCheckParam = new AssignCheckParam();
        paramAssignCheckParam.setProcessDefinitionKey(processDefinitionKey);
        if (StringUtils.isBlank(processDefinitionKey)) {
            return false;
        }
        JsonNode assignCheckResult;
        try {
            assignCheckResult = (JsonNode) rtService.assignCheck(paramAssignCheckParam);
            return assignCheckResult.get("assignAble").asBoolean(false);
        } catch (RestException e) {
            return false;
        }

    }

    /**
     * 获取主组织
     *
     * @param uiMetaBaseInfo
     * @param bill
     * @return
     */
    private String getMasterOrg(BillContext uiMetaBaseInfo, BizObject bill) {
        String orgField = MetaAttributeUtils.getAttributeField(uiMetaBaseInfo.getFullname(), "isMasterOrg", "org");  //主组织标识
        if (bill.get(orgField) != null) {
            return "" + bill.get(orgField);
        } else {
            return null;
        }
    }

    /**
     * 获取交易类型
     *
     * @param bill
     * @return
     */
    private String getTransactionType(BillContext billContext, BizObject bill) throws Exception {
        Map billBaseMap = billMetaBiz.getBaseBill(billContext.getBillnum(), AppContext.getTenantId());
        if (MapUtils.isNotEmpty(billBaseMap)) {
            // 根据bill_base 表中的 label ，如果包含ignoreTransaction 则 不按交易类型走
            if (billBaseMap.containsKey("label") && billBaseMap.get("label") != null) {
                String label = (String) billBaseMap.get("label");
                if (label.contains("ignoreTransaction")) {
                    return "";
                }
            }
        }
        String transactionTypeField = com.yonyou.ucf.mdd.ext.meta.MetaAttributeUtils.getFieldByAttributeOrName(billContext.getFullname(), "isTransactionType", "bustype");

        if( null == bill.get(transactionTypeField)){
            return "";
        } else {
            return "" + bill.get(transactionTypeField);
        }

    }

    private String getCode(BillContext uiMetaBaseInfo, BizObject bill) {
        String codeField = MetaAttributeUtils.getAttributeField(uiMetaBaseInfo.getFullname(), "isCode", "code");//编码标识
        String codeValue = bill.get(codeField) != null ? bill.get(codeField) : "";
        return uiMetaBaseInfo.getName() + MddConstants.STR_EMPTY + codeValue;
    }

    /**
     * 处理结果
     *
     * @param responseNode
     * @return
     * @throws Exception
     */
    private String handleResult(JsonNode responseNode) throws Exception {
        return getFromJsonNode(responseNode, "id");
    }

    private String getKey(JsonNode responseNode) throws Exception {
        return getFromJsonNode(responseNode, "key");
    }

    private String getFromJsonNode(JsonNode responseNode, String key) throws Exception {
        log.info("handleResult:{}", JsonUtils.toJson(responseNode));
        if (null != responseNode.get("errcode") && 0 != responseNode.get("errcode").intValue()) {
            log.error("error:{}", responseNode);
            throw new MddBpmException(responseNode.get("errmsg").textValue());
        } else {
            if (null == responseNode.get(key)) {
                if (null != responseNode.get("data") && responseNode.get("data").size() > 0) {
                    if (null != responseNode.get("data").get(0) && null != responseNode.get("data").get(0).get(key)) {
                        return responseNode.get("data").get(0).get(key).textValue();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return responseNode.get(key).textValue();
            }
        }
    }


    /**
     * 是否存在已发布流程
     *
     * @param transactionType
     * @return
     * @throws Exception
     */
    public boolean existsProcessDefinition(String transactionType, String org) throws Exception {
        ISimpleUser user = getCurrentUser();
        String userId = user.getUserId() != null ? user.getUserId().toString() : "";
        String limitTenantId = user.getTenantId() != null ? user.getTenantId().toString() : null;
        String result = queryProcessDefinitionId(userId, limitTenantId, transactionType, org);
        return null != result;
    }

    public boolean existsProcessDefinition(String transactionType, String billTypeId, String org) throws Exception {
        ISimpleUser user = getCurrentUser();
        String userId = user.getUserId() != null ? user.getUserId().toString() : "";
        String limitTenantId = user.getTenantId() != null ? user.getTenantId().toString() : null;
        String result = queryProcessDefinitionKey(userId, limitTenantId, transactionType, billTypeId, org);
        return null != result;
    }


    /**
     * 查询流程定义
     */
    public String queryProcessDefinitionId(String userId, String limitTenantId, String categoryId, String org) throws Exception {
        JsonNode responseNode = (JsonNode) bpmRestServices(userId, limitTenantId, org).getRepositoryService()
                .getProcessDefinitionModelsInAuthority(categoryId, org, Boolean.TRUE);
        return handleResult(responseNode);
    }


    public String queryProcessDefinitionKey(String userId, String limitTenantId, String categoryId, String billTypeId, String org) throws Exception {
        ProcessDefinitionQueryParam processDefinitionQueryParam = new ProcessDefinitionQueryParam();
        processDefinitionQueryParam.setCascadeOrg(Boolean.TRUE);
        processDefinitionQueryParam.setCategory(categoryId);
        processDefinitionQueryParam.setBillTypeId(billTypeId);
        //log.info("获取流程定义id  parm: billTypeId=" + billTypeId + "  ,categoryId=" + categoryId);
        JsonNode responseNode = (JsonNode) bpmRestServices(userId, limitTenantId, org).getRepositoryService()
                .queryProcessDefinitionInAuthorityWithCondition(processDefinitionQueryParam);
        //log.info("获取流程定义返回结果: " + responseNode.asText());
        return getKey(responseNode);
    }

    private HistoricProcessInstanceResponse updateProcessVariables(String businessKey, List<RestVariable> variables)
            throws RestException {
        log.info("更新流程。流程变量数据=" + JSON.toJSONString(variables));
        RuntimeService rt = bpmRestServices().getRuntimeService();

        ProcessInstanceActionParam processInstanceActionParam = new ProcessInstanceActionParam();
        processInstanceActionParam.setVariables(variables);

        ObjectNode node = (ObjectNode) rt.updateProcessVariablesByBusinessKey(businessKey, processInstanceActionParam);

        HistoricProcessInstanceResponse resp = JSON.parseObject(
                node.toString(), HistoricProcessInstanceResponse.class);
        return resp;
    }

    /**
     * 转化请求Dto, 模拟登陆
     */
    private BaseReqDto checkCompleteIdentity(BpmRequestBody params) throws Exception {

        if (StringUtils.isBlank(params.getUserId())) {
            throw new MddMsgException("没有用户信息", ExceptionSubCode.PARAM_IS_NULL, new Object[]{"userid"});
        }
        if (StringUtils.isBlank(params.getTenantId())) {
            throw new MddMsgException("没有租户信息", ExceptionSubCode.PARAM_IS_NULL, new Object[]{"tenantid"});
        }
        String yhtTenantId = "";
        if (null != params.getTenantId()) {
            yhtTenantId = params.getTenantId().split("_")[0];
        }

        //模拟login 把userid 、token 和tenantid 设置到上下文，供后续操作获取
        MddBaseContext.setTenantId(yhtTenantId);

        BaseReqDto dto = new BaseReqDto();
        if (StringUtils.isBlank(params.getBusinessKey())) {
            throw new MddMsgException("没有业务号BusinessKey", ExceptionSubCode.PARAM_IS_NULL, new Object[]{"BusinessKey"});
        } else {
            String businessKey = params.getBusinessKey();
            int pos = businessKey.lastIndexOf("_");
            String billnum = "";
            String id = null;
            if (pos > 0) {
                billnum = businessKey.substring(0, pos);
                id = businessKey.substring(pos + 1);
            } else {
                throw new MddBpmException("业务单据错误");
            }
            dto.setBillnum(billnum);

            BizObject bizObject = new BizObject();
            bizObject.set(MddConstants.PARAM_ID, id);
            bizObject.set("_status", EntityStatus.Update);
            dto.setData(bizObject);
        }
        return dto;
    }

    private String queryOrgByUserId(String userId, String userLinkType) {
        OrgQueryParam orgQueryParam = new OrgQueryParam();
        if (StringUtils.isEmpty(userId)) {
            orgQueryParam.setQueryByUser(true);
        } else {
            orgQueryParam.setUserId(userId);
        }
        orgQueryParam.setQueryMainTarget(true);
        orgQueryParam.setUserLinkType(userLinkType);
        try {
            Object obj = bpmRestServices().getIdentityService().queryOrgs(orgQueryParam);
            if (obj != null) {
                JsonNode responseNode = (JsonNode) obj;
                if (null != responseNode.get("data") && responseNode.get("data").size() > 0) {
                    if (null != responseNode.get("data").get(0) && null != responseNode.get("data").get(0).get("id")) {
                        return responseNode.get("data").get(0).get("id").textValue();
                    }
                }
            }
        } catch (RestException e) {
            log.error("exception when invoke bpm rest api", e);
        }
        return null;
    }

}
