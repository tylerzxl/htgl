package com.yonyou.ucf.mdf.app.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdd.common.enums.OperationTypeEnum;
import com.yonyou.ucf.mdd.common.model.Pager;
import com.yonyou.ucf.mdd.common.model.ResultList;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.utils.json.GsonHelper;
import com.yonyou.ucf.mdd.core.i18n.utils.MddMultilingualUtil;
import com.yonyou.ucf.mdd.rule.api.RuleEngine;
import com.yonyou.ucf.mdf.app.exceptions.BusinessException;
import com.yonyou.ucf.mdf.app.service.IBillService;
import com.yonyou.ucf.mdf.app.util.RuleEngineUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class BillServiceImp implements IBillService {

    private static Logger logger = LoggerFactory.getLogger(BillServiceImp.class);

    @Override
    public Pager queryByPage(BaseReqDto qyeryParam) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils
                .prepareRuleContext(qyeryParam, OperationTypeEnum.QUERY);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (Pager) result.getData();
            }
        } catch (Exception e) {
            logger.error("queryByPage", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Map detail(BaseReqDto detailDto) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils
                .prepareRuleContext(detailDto, OperationTypeEnum.DETAIL);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (Map) result.getData();
            }
        } catch (Exception e) {
            logger.error("getBillDetail", e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Map print(BaseReqDto detailDto, String action) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils.prepareRuleContext(detailDto, action);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (Map) result.getData();
            }
        } catch (Exception e) {
            logger.error("getPrintBill", e);
            throw new BusinessException(e.getMessage());
        }
    }


    @Override
    public String add(BaseReqDto addDto) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils.prepareRuleContext(addDto, OperationTypeEnum.ADD);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return result.getData() == null ? "" : GsonHelper.ToJSon(result.getData());
            }
        } catch (Exception e) {
            logger.error("bill add 异常",e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public String delete(BaseReqDto delDto) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils.prepareRuleContext(delDto, OperationTypeEnum.DELETE);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (String) result.getData();
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public RuleExecuteResult executeUpdate(String action, BaseReqDto bill) throws Exception {
        try {
            if (null == action)
                throw new BusinessException(
                    MddMultilingualUtil.getFWMessage("P_YS_FW-PUB_MDD-BACK_0001065510","没有定义动作") /* "没有定义动作" */);

            String[] actions = action.split("&");
            RuleExecuteResult result = null;
            for (String act : actions) {
                RuleContext ruleContext = RuleEngineUtils.prepareRuleContext(bill, act);
                result = RuleEngine.getInstance().execute(ruleContext);
                if (result.getMsgCode() != 1) {
                    logger.error(String.format("executeUpdate MsgCode() != 1: %s",result.getMessage()));
                    throw new BusinessException(result.getMessage());
                }
            }
            return result;
        } catch (Exception e) {
            logger.error(String.format("executeUpdate ERROR: %s",e.getMessage()), e);
            throw new BusinessException(e.getMessage(),e);
        }
    }

    @Override
    public ResultList batchdelete(BaseReqDto bill) throws Exception {
//        try {
//            bill.setAction("delete");
//            JsonArray list = (JsonArray) GsonHelper.FromJSon((String) bill.getData(),
//                    JsonArray.class);
//            ResultList resultList = new ResultList(list.size());
//            Iterator iterator = list.iterator();
//            while (iterator.hasNext()) {
//                //TODO 事务注掉，可能会有问题
//                // TransactionStatus status = SqlHelper.getTransactionStatus();
//                try {
//                    JsonObject item = (JsonObject) iterator.next();
//                    BaseReqDto oneBill = new BaseReqDto();
//                    oneBill.setBillnum(bill.getBillnum());
//                    oneBill.setAction(bill.getAction());
//                    oneBill.setData(GsonHelper.ToJSon(item));
//                    oneBill.setMapCondition(bill.getMapCondition());
//                    RuleContext ruleContext = RuleEngineUtils.prepareRuleContext(oneBill, oneBill.getAction());
//                    RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
//                    if (result.getMsgCode() != 1) {
//                        throw new BusinessException(result.getMessage());
//                    }
//                    if (result.getMsgCode() == 1) {
//                        resultList.addInfo(result.getData());
//                        resultList.incSucessCount();
//                    } else {
//                        resultList.incFailCount();
//                        resultList.addMessage(result.getMessage());
//                    }
//                } catch (Exception e) {
//                    logger.error(e.getMessage(), e);
//                    resultList.incFailCount();
//                    resultList.addMessage(e.getMessage());
//                }
//            }
//            return resultList;
//        } catch (Exception e) {
//            throw new BusinessException(e.getMessage());
//        }

        try {
            return batchDo(bill,"delete");
        } catch (BusinessException e) {
            logger.error(e.getMessage(),e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public List querytree(BaseReqDto baseReqDto) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils
                .prepareRuleContext(baseReqDto, OperationTypeEnum.QUERYTREE);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (List) result.getData();
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

//    @Override
//    public Map enter(BaseReqDto baseReqDto) throws Exception {
//        try {
//            RuleContext ruleContext = RuleEngineUtils.prepareRuleContext(baseReqDto, OperationTypeEnum.ENTER);
//            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
//            if (result.getMsgCode() != 1) {
//                throw new BusinessException(result.getMessage());
//            } else {
//                return (Map)result.getData();
//            }
//        } catch (Exception e) {
//            throw new BusinessException(e.getMessage());
//        }
//    }

    @Override
    public Map movefirst(BaseReqDto baseReqDto) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils
                .prepareRuleContext(baseReqDto, OperationTypeEnum.MOVEFIRST);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (Map) result.getData();
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Map movelast(BaseReqDto baseReqDto) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils
                .prepareRuleContext(baseReqDto, OperationTypeEnum.MOVELAST);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (Map) result.getData();
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Map movenext(BaseReqDto baseReqDto) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils
                .prepareRuleContext(baseReqDto, OperationTypeEnum.MOVENEXT);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (Map) result.getData();
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Map moveprev(BaseReqDto baseReqDto) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils
                .prepareRuleContext(baseReqDto, OperationTypeEnum.MOVEPREV);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (Map) result.getData();
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public RuleExecuteResult check(BaseReqDto checkItem) throws Exception {
        try {
            RuleContext ruleContext = RuleEngineUtils
                .prepareRuleContext(checkItem, OperationTypeEnum.CHECK);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return result;
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public ResultList batchDo(BaseReqDto bill, String action) throws BusinessException {
        bill.setAction(action);
        JsonArray list = (JsonArray) GsonHelper.FromJSon((String) bill.getData(),
                JsonArray.class);
        ResultList resultList = new ResultList(list.size());
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            //TODO 事务注掉，可能会有问题
            try {
                TransactionStatus status = MddBaseContext.bizDataDao().getTransactionStatus();
                JsonObject item = (JsonObject) iterator.next();
                BaseReqDto oneBill = new BaseReqDto();
                oneBill.setBillnum(bill.getBillnum());
                oneBill.setAction(bill.getAction());
                oneBill.setData(GsonHelper.ToJSon(item));
                oneBill.setMapCondition(bill.getMapCondition());
                oneBill.setExternalData(bill.getExternalData());
                oneBill.setExtendCondition(bill.getExtendCondition());
                RuleContext ruleContext = RuleEngineUtils.prepareRuleContext(oneBill, oneBill.getAction());
                RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
                if (result.getMsgCode() != 1) {
                    throw new BusinessException(result.getMessage());
                }
                if (result.getMsgCode() == 1) {
                    resultList.addInfo(result.getData());
                    resultList.incSucessCount();
                } else {
                    resultList.incFailCount();
                    resultList.addMessage(result.getMessage());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                resultList.incFailCount();
                resultList.addMessage(e.getMessage());
            }
        }
        return resultList;
    }


    @Override
    public ResultList batchsubmit(BaseReqDto bill) throws BusinessException {
        try {
            return batchDo(bill,"submit");
        } catch (BusinessException e) {
            logger.error(e.getMessage(),e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public ResultList batchunsubmit(BaseReqDto bill) throws BusinessException {
        try {
            return batchDo(bill,"unsubmit");
        } catch (BusinessException e) {
            logger.error(e.getMessage(),e);
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public String copy(BaseReqDto baseReqDto) throws BusinessException {
        try {
            RuleContext ruleContext = RuleEngineUtils
                .prepareRuleContext(baseReqDto, OperationTypeEnum.COPY);
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return (String) result.getData();
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public Object doAction(BaseReqDto queryParam) throws BusinessException {
        try {
            RuleContext ruleContext = RuleEngineUtils.prepareRuleContext(queryParam, queryParam.getAction());
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            if (result.getMsgCode() != 1) {
                throw new BusinessException(result.getMessage());
            } else {
                return result.getData();
            }
        } catch (Exception e) {
            logger.error("doAction Error: " + e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }


    /**
     * 与queryByPage使用同样的实现类，只是参数略有不同
     *
     * 参数取决于传入Pager对象中的totalCount
     *
     * 设置totalCount为2只查询分页信息
     *
     * @param billDataDto
     * @return
     * @throws Exception
     */
    @Override
    public Pager queryPageCount(BaseReqDto baseReqDto) throws Exception {
        baseReqDto.getPage().setTotalCount(-1);
        return queryByPage(baseReqDto);
    }
}
