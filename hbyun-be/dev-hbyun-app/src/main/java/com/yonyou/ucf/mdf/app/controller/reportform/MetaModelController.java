package com.yonyou.ucf.mdf.app.controller.reportform;

import com.alibaba.fastjson.JSON;
import com.yonyou.ucf.mdd.common.model.model.MetaModel;
import com.yonyou.ucf.mdd.common.utils.Toolkit;
import com.yonyou.ucf.mdf.app.controller.BaseController;
import com.yonyou.ucf.mdf.app.service.impl.MetaModelService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 模型接口
 */
@RestController
@RequestMapping("/v1")
public class MetaModelController extends BaseController {

    @RequestMapping("/model")
    public List<MetaModel> model(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> fullnames = null;
        String entities = request.getHeader("entities");
        if (!Toolkit.isEmpty(entities)) {
            fullnames = JSON.parseObject(entities, List.class);
        }

        Map<String, List<String>> fieldsMap = null;
        String fields = request.getHeader("fields");
        if (!Toolkit.isEmpty(fields)) {
            fieldsMap = JSON.parseObject(fields, Map.class);
        }

        boolean onlyOneLevel = MetaModelService.b(request.getHeader("onlyOneLevel"));
        List<MetaModel> metaModels = new MetaModelService().getModels(fullnames, fieldsMap, onlyOneLevel);

        return metaModels;
    }

}
