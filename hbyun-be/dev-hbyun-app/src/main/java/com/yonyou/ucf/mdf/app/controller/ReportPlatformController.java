package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.model.uimeta.MetaInfo;
import com.yonyou.ucf.mdd.uimeta.dao.IUimetaBaseMapperDao;
import com.yonyou.ucf.mdd.uimeta.util.BillUtils;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yonyou.bpm.rest.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/report") // 前端已经和此路径进行绑定，抽取的path 暂时也改不动
public class ReportPlatformController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ReportPlatformController.class);

    /**
     * 通过billno获取物理实体信息
     *  - 查询逻辑为根据billNo 先关联bill_base表和billentity_base 表查询到fullname,
     *  - 然后根据fullname 查询物理元数据 并进行封装返回给前端
     * @param billno
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/getEntityInfoByBillNo")
    public void getEntityInfoByBillNo(@RequestParam String billno, HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            List<MetaInfo> metaInfos =	new ArrayList<>();

            Map<String, Object> params = new HashMap<String, Object>();
            params.put(MddConstants.PARAM_TENANT_ID, MddBaseContext.getTenantId());
            params.put(MddConstants.PARAM_BILL_NUMB, billno);
            String fullname = MddBaseContext.getMapperDao(IUimetaBaseMapperDao.class).getEntityInfoByBillNo(params);
            if(StringUtils.isNotBlank(fullname)){
                metaInfos = BillUtils.getEntityInfoByName(fullname);
            }
            renderJson(response, ResultMessage.data(metaInfos));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("通过billno获取实体信息失败{}", e);
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }
}
