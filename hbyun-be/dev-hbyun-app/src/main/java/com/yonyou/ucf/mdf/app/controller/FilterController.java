package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.common.exceptions.MddBaseException;
import com.yonyou.ucf.mdd.uimeta.api.UIMetaEngine;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import com.yonyou.ucf.mdf.app.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询方案
 *
 * @author yantao
 */
//@Controller(value = "FilterController")
//@RequestMapping("/filter/")
public class FilterController extends BaseController {

    @RequestMapping(value = "/{solutionId}/solutionFilters",method = RequestMethod.GET)
    public void getAllCommonFilters(@PathVariable int solutionId, HttpServletRequest request, HttpServletResponse response){
        try {
            String viewid=request.getParameter("viewid");
            Map<String, Object> allCommonFilters = UIMetaEngine.getInstance().getAllCommonFilters(solutionId, viewid, CommonUtil.getUserId(), CommonUtil.getTenantId(), null);
            //转化结果，区分过滤和排序
            List<HashMap<String, Object>> commonModel = (List<HashMap<String, Object>>) allCommonFilters.get("CommonModel");
            if(commonModel!=null&&commonModel.size()>0) {
                List<HashMap<String, Object>> allFilters = new ArrayList<>();
                List<HashMap<String, Object>> allOrders = new ArrayList<>();
                for (HashMap<String, Object> item : commonModel) {
                    //iType为null或0搜索；1排序；2搜索和排序
                    if (!item.containsKey("iType") || null == item.get("iType") || StringUtils.isEmpty(item.get("iType").toString())
                            || "0".equals(item.get("iType").toString())) {
                        allFilters.add(item);
                    } else if ("1".equals(item.get("iType").toString())) {
                        allOrders.add(getOrderItem(item));
                    } else if ("2".equals(item.get("iType").toString())) {
                        allFilters.add(item);
                        allOrders.add(getOrderItem(item));
                    }
                }
                allCommonFilters.remove("CommonModel");
                allCommonFilters.put("CommonModel", allFilters);
                HashMap<String, Object> order = new HashMap<>();
                order.put("OrderModel", allOrders);
                order.put("multiple", false);//暂只支持单字段排序，将来在solution表中增加字段标记
                allCommonFilters.put("Order", order);
            }

            renderJson(response, ResultMessage.data(allCommonFilters));
        }catch (Exception e){
            String msg = e.getMessage();
            if(e instanceof MddBaseException && StringUtils.isNotBlank(((MddBaseException) e).getMsg())){
                msg = ((MddBaseException) e).getMsg();
            }
            renderJson(response, ResultMessage.error(msg));
        }
    }

    private HashMap<String, Object> getOrderItem(HashMap<String, Object> item) {
        HashMap<String, Object> orderItem=new HashMap<>();
        orderItem.put("itemName",item.get("itemName"));
        orderItem.put("itemTitle",item.get("itemTitle"));
        orderItem.put("defaultOrder",item.get("defaultOrder"));
        if(orderItem.get("defaultOrder")==null) {
            orderItem.put("defaultOrder", "asc");
        }
        orderItem.put("bAutoflt",item.get("bAutoflt"));
        return orderItem;
    }


}
