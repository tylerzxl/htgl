package com.yonyou.ucf.mdf.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdd.common.enums.OperationTypeEnum;
import com.yonyou.ucf.mdd.common.model.Pager;
import com.yonyou.ucf.mdd.common.model.model.ReportResult;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyou.ucf.mdd.common.model.uimeta.filter.vo.FilterVO;
import com.yonyou.ucf.mdd.common.utils.Toolkit;
import com.yonyou.ucf.mdd.core.meta.MddMetaDaoHelper;
import com.yonyou.ucf.mdd.rule.api.RuleEngine;
import com.yonyou.ucf.mdd.uimeta.util.UIMetaUtils;
import com.yonyou.ucf.mdf.app.exceptions.BusinessException;
import com.yonyou.ucf.mdf.app.util.CommonUtil;
import com.yonyou.ucf.mdf.app.util.RuleEngineUtils;
import lombok.extern.slf4j.Slf4j;
import org.imeta.orm.base.Json;
import org.imeta.orm.query.parser.QuerySchemaBuilder;
import org.imeta.orm.schema.QueryConditionGroup;
import org.imeta.orm.schema.QueryField;
import org.imeta.orm.schema.QuerySchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class BillReportService {

    public static ReportResult query(Map<String, Object> param)
            throws Exception {
        try {
            long l = System.currentTimeMillis();
            String billnum = (String) param.get("billnum");
            BaseReqDto dto = new BaseReqDto();
            if (null != billnum) {
                dto.setBillnum(billnum);
            }
            QueryConditionGroup queryConditionGroup = null;
            if (param.containsKey("filterVo")) {
                Map<String, Object> paramConditon = (Map<String, Object>) param.get("filterVo");
                String conditionJson = JSON.toJSONString(paramConditon);
                FilterVO filterVo = JSON.parseObject(conditionJson, FilterVO.class);
                if (null != billnum) {
                    dto.setCondition(filterVo);
                } else {
                    queryConditionGroup = UIMetaUtils.handleQueryCondtion(filterVo, CommonUtil.getTenantId());
                }
            }
            String json = JSON.toJSONString(param);
            log.info("queryForReport json:{}", json);
            QuerySchema schema = QuerySchemaBuilder.fromJson(new Json(json));
            UIMetaBaseInfo billContext = new UIMetaBaseInfo();
            billContext.setFullname(param.get("entity").toString());
            billContext.setbRowAuthControl(true);
            if (null != billnum) {
                dto.setQuerySchema(schema);
            } else {
                if (null != queryConditionGroup) {
                    schema.queryConditionGroup(queryConditionGroup);
                }
            }
            Pager pager = null;
            if (null == billnum) {
                MddMetaDaoHelper helper = MddBaseContext.getBean(MddMetaDaoHelper.class);
                pager = helper.queryByPage(billContext, schema);
            } else {
                RuleContext ruleContext =  RuleEngineUtils.prepareRuleContext(dto, OperationTypeEnum.QUERY);
                RuleExecuteResult ruleResult = RuleEngine.getInstance().execute(ruleContext);

                if (ruleResult.getMsgCode() != 1) {
                    throw new BusinessException(ruleResult.getMessage());
                } else {
                    pager = (Pager) ruleResult.getData();
                }
            }
            if (null == pager){
                return new ReportResult();
            }
            List<Map<String, Object>> results = pager.getRecordList();
            Object[] result = null;
            if (null != results && results.size() > 0) {
                List<Object> values = new ArrayList<>();
                for (Map<String, Object> ret : results) {
                    List<Object> value = new ArrayList<>();
                    for (QueryField field : schema.selectFields()) {
                        String fieldname = "";
                        if (!Toolkit.isEmpty(field.alias())) {
                            fieldname = field.alias();
                        } else {
                            fieldname = field.name().replaceAll(".", "_");
                        }
                        value.add(ret.get(fieldname));
                    }
                    values.add(value);
                }
                result = values.toArray();
            }
            log.info("queryForReportTime:{}", System.currentTimeMillis() - l);
            return new ReportResult(result, pager);
        } catch (Exception e) {
            log.error("queryForReport", e);
            throw new Exception(e);
        }
    }
}
