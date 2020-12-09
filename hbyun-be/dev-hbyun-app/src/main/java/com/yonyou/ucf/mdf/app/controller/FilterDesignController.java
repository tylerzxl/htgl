package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.exceptions.MddUIMetaException;
import com.yonyou.ucf.mdd.common.model.uimeta.*;
import com.yonyou.ucf.mdd.common.model.uimeta.filter.vo.FilterVO;
import com.yonyou.ucf.mdd.common.model.uimeta.filter.vo.SolutionSaveVO;
import com.yonyou.ucf.mdd.common.model.uimeta.filter.vo.SolutionVO;
import com.yonyou.ucf.mdd.uimeta.api.UIMetaEngine;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import com.yonyou.ucf.mdf.app.util.CommonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 过滤设计Controller
 *
 * @author wufengBpmController
 */

//@Controller
//@RequestMapping("/filterDesign")
@Deprecated
public class FilterDesignController extends BaseController {

    Logger logger = LoggerFactory.getLogger(FilterDesignController.class);

    /**
     * 获取过滤方案列表
     *
     * @param
     * @param request
     * @param response
     */
    @RequestMapping(value = "/getSolutionList", method = RequestMethod.POST)
    public void getSolutionList(@RequestBody HashMap<String, Integer> paramMap, HttpServletRequest request, HttpServletResponse response) {

        try {
            if (null == paramMap || null == paramMap.get("filterId")) {
                renderJson(response, ResultMessage.error("过滤id为空！"));
            } else {
                int filterId = paramMap.get("filterId");
                String terminalType = request.getParameter("terminalType");
                if (null == terminalType || "2".equals(terminalType)) {
                    terminalType = "1";
                }
                List<FilterSolution> solutionList = UIMetaEngine.getInstance()
                        .getSolutionList(filterId, terminalType, CommonUtil.getUserId(), CommonUtil.getTenantId(), null);
                renderJson(response, ResultMessage.data(solutionList, true));
            }
        } catch (MddUIMetaException e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    /**
     * 获取常用方案列表
     *
     * @param
     * @param request
     * @param response
     */
    @RequestMapping("/getSolutionCommonList")
    public void getSolutionCommonList(@RequestBody HashMap<String, Integer> paramMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            int solutionId = paramMap.get("solutionId");
            List<FilterSolutionCommon> solutionCommonList = UIMetaEngine.getInstance()
                    .getSolutionCommonList(solutionId, CommonUtil.getTenantId(), null);
            renderJson(response, ResultMessage.data(solutionCommonList, true));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    @RequestMapping("/getFilterBase")
    public void getFilterBase(int filterId, HttpServletRequest request, HttpServletResponse response) {
        try {
            String loadDefaultSolution = request.getParameter("loadDefaultSolution");
            if ("1".equals(loadDefaultSolution)) {
                String terminalType = request.getParameter("terminalType");
                if (null == terminalType || "2".equals(terminalType)) {
                    terminalType = "1";
                }
                String viewid = request.getParameter("viewid");
                CombinFilterInfo combinFilterInfo =
                        UIMetaEngine.getInstance().loadAllFilterInfo(filterId, terminalType, viewid);
                List<Object> resultList = new ArrayList<>();
                if (combinFilterInfo.getMetaFilters() != null) {
                    resultList.add(ResultMessage.getIns(combinFilterInfo.getMetaFilters()));
                } else {
                    resultList.add(ResultMessage.error(combinFilterInfo.getMetaFilterMsg()));
                }
                if (combinFilterInfo.getSolutionList() != null) {
                    resultList.add(ResultMessage.getIns(combinFilterInfo.getSolutionList()));
                } else {
                    resultList.add(ResultMessage.error(combinFilterInfo.getSolutionMsg()));
                }
                if (combinFilterInfo.getSolutionFilterMap() != null) {
                    resultList.add(ResultMessage.getIns(combinFilterInfo.getSolutionFilterMap()));
                } else {
                    resultList.add(ResultMessage.error(combinFilterInfo.getSolutionFilterMapMsg()));
                }
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("data", resultList);
                resultData.put("loadDefaultSolution", 1);// 平滑升级，升级完毕后删除
                renderJson(response, ResultMessage.data(resultData));
            } else {
                MetaFilters metaFilters = UIMetaEngine.getInstance().getFilterBase(filterId, null);
                renderJson(response, ResultMessage.data(metaFilters));
            }
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    @RequestMapping("/getFilterOption")
    public void getFilterOption(int filterId, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Object> resultList = new ArrayList<>();
            MetaFilters metaFilters = UIMetaEngine.getInstance().getFilterBase(filterId, null);
            resultList.add(metaFilters);
            String terminalType = request.getParameter("terminalType");
            if (null == terminalType || "2".equals(terminalType)) {
                terminalType = "1";
            }
            List<FilterSolution> solutionList = UIMetaEngine.getInstance()
                    .getSolutionList(filterId, terminalType, CommonUtil.getUserId(), CommonUtil.getTenantId(), null);
            resultList.add(solutionList);
            Integer solutionId = null;
            if (CollectionUtils.isNotEmpty(solutionList)) {
                Optional<FilterSolution> defaultSolution = solutionList.stream().filter(solution -> 1 == solution.getIsDefault()).findFirst();
                if (defaultSolution.isPresent()) {
                    FilterSolution filterSolution = defaultSolution.get();
                    solutionId = filterSolution.getId();
                }
            }
            if (null != solutionId) {
                String viewid = request.getParameter("viewid");
                Map<String, Object> allCommonFilters = UIMetaEngine.getInstance()
                        .getAllCommonFilters(solutionId, viewid, CommonUtil.getUserId(), CommonUtil.getTenantId(), null);
                resultList.add(allCommonFilters);
            }
            renderJson(response, ResultMessage.data(resultList));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    /**
     * 获取过滤栏目信息
     *
     * @param filtersId
     * @param request
     * @param response
     */
    @RequestMapping("/getFiltersInfo")
    public void getFiltersInfo(@RequestParam int filtersId, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<MetaFilterItem> list = UIMetaEngine.getInstance().getFiltersInfo(filtersId, CommonUtil.getTenantId(), null);
            renderJson(response, ResultMessage.data(list));
        } catch (Exception e) {
            logger.error("[getFiltersInfo] 异常", e);
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    /**
     * 保存过滤方案
     *
     * @param solution
     * @param request
     * @param response
     */
    @RequestMapping({"/saveSolution"})
    public void saveSolution(@RequestBody Map<String, Object> solution, HttpServletRequest request, HttpServletResponse response) {
        try {
            String terminalType = request.getParameter("terminalType");
            if (null == terminalType) {
                terminalType = "1";
            }
            solution.put("terminalType", terminalType);
            Object userid = MddBaseContext.getCurrentUser().getUserId();
            UIMetaEngine.getInstance().saveSolution(solution, MddBaseContext.getTenantId(), null, userid);
            this.renderJson(response, ResultMessage.success());
        } catch (Exception e) {
            logger.error("[saveSolution] 异常", e);
            this.renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    /**
     * @param solutionId
     * @param terminalType
     * @param request
     * @param response
     */
    @RequestMapping({"/setDefaultFilter"})
    public void setDefaultFilter(@RequestParam int solutionId, @RequestParam int isPublic, Integer terminalType, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            UIMetaEngine.getInstance().setDefaultFilter(terminalType, solutionId, isPublic, CommonUtil.getTenantId(), null);
            this.renderJson(response, ResultMessage.success());
        } catch (Exception e) {
            logger.error("[setDefaultFilter] 异常", e);
            this.renderJson(response, ResultMessage.error(e.getMessage()));
        }

    }

    /**
     * 删除过滤方案
     *
     * @param solutionId
     * @param request
     * @param response
     */
    @RequestMapping("/delSolution")
    public void delSolution(@RequestParam int solutionId, HttpServletRequest request, HttpServletResponse response) {
        try {
            UIMetaEngine.getInstance().delSolution(solutionId, CommonUtil.getTenantId(), null);
            renderJson(response, ResultMessage.success());
        } catch (Exception e) {
            logger.error("[delSolution] 异常", e);
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    /**
     * 保存过滤方案
     * 快捷保存过滤方案，应该把commonVOs的isCommon设置成1
     *
     * @param
     * @param request
     * @param response
     */
    @RequestMapping("/addSolutionAll")
    public void addSolutionAll(@RequestBody SolutionSaveVO solutionSaveVO, HttpServletRequest request, HttpServletResponse response) {
        FilterVO filterVO = solutionSaveVO.getFilterVO();
        SolutionVO solutionVO = solutionSaveVO.getSolutionVO();
        if (null != solutionVO) {
            //赋值方案所属用户
            solutionVO.setUserId(CommonUtil.getUserId());
        }
        try {
            FilterSolution solution = UIMetaEngine.getInstance().insertSolutionAllExt(solutionVO, filterVO, CommonUtil.getTenantId());
            renderJson(response, ResultMessage.data(solution));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

    /**
     * 更新过滤方案，将过滤总信息和内容一起传回来
     *
     * @param solutionSaveVO
     * @param request
     * @param response
     */
    @RequestMapping("/updateSolutionAll")
    public void updateSolutionAll(@RequestBody SolutionSaveVO solutionSaveVO, HttpServletRequest request, HttpServletResponse response) {
        FilterVO filterVO = solutionSaveVO.getFilterVO();
        SolutionVO solutionVO = solutionSaveVO.getSolutionVO();
        try {
            // isshopreleated默认false
            UIMetaEngine.getInstance().updateSolutionAll(solutionVO, filterVO, CommonUtil.getUserId(), false);
            renderJson(response, ResultMessage.data("更新成功！"));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
        }
    }

}
