package com.yonyou.ucf.mdf.app.controller;


import com.google.common.collect.Sets;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user/")
public class UserController extends BaseController {
    static org.slf4j.Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * @return void
     * @Description 功能权限校验
     * @Param [code, request, response]
     * @Author DaeDalu$
     * @Date
     **/
    @RequestMapping("checkAuthByCode")
    public void checkAuthByCode(@RequestParam String code, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            //TODO 完善获取userAuth数据的方法
//            ISimpleUser user = CommonUtil.getCurrentUser();
//            Set<String> auths = user.getUserAuth();
            Set<String> auths = Sets.newHashSet("userdef_filterItem", "userdef_schemaSetting");
            if (CollectionUtils.isEmpty(auths)) {
                renderJson(response, ResultMessage.data(false));
                return;
            }

            String[] codes = code.split(",");
            if (codes.length <= 1) {
                boolean flag = false;
                if (auths.contains(code)) {
                    flag = true;
                }
                renderJson(response, ResultMessage.data(flag));
                return;

            }
            Map<String, Object> map = new HashMap<>();
            for (String c : codes) {
                map.put(c, auths.contains(c));
            }
            renderJson(response, ResultMessage.data(map));
        } catch (Exception e) {
            logger.error("检查权限是否存在", e);
            renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }
}
