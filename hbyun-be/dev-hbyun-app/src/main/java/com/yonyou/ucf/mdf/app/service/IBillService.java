package com.yonyou.ucf.mdf.app.service;

import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdd.common.model.Pager;
import com.yonyou.ucf.mdd.common.model.ResultList;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdf.app.exceptions.BusinessException;
import java.util.List;
import java.util.Map;

public interface IBillService {

    Pager queryByPage(BaseReqDto queryDto) throws Exception;

    Map detail(BaseReqDto detailDto) throws Exception;

    Map print(BaseReqDto detailDto, String action) throws Exception;

    String add(BaseReqDto addDto) throws Exception;

    String delete(BaseReqDto delDto) throws Exception;

    RuleExecuteResult executeUpdate(String action, BaseReqDto baseReqDto) throws Exception;

    ResultList batchdelete(BaseReqDto baseReqDto) throws Exception;

    ResultList batchDo(BaseReqDto baseReqDto, String action) throws Exception;

    List querytree(BaseReqDto baseReqDto) throws Exception;

//    Map enter(BaseReqDto baseReqDto) throws Exception;

    Map movefirst(BaseReqDto baseReqDto) throws Exception;

    Map movenext(BaseReqDto baseReqDto) throws Exception;

    Map movelast(BaseReqDto baseReqDto) throws Exception;

    Map moveprev(BaseReqDto baseReqDto) throws Exception;

    RuleExecuteResult check(BaseReqDto checkItem) throws Exception;

    ResultList batchsubmit(BaseReqDto baseReqDto) throws BusinessException;

    ResultList batchunsubmit(BaseReqDto baseReqDto) throws BusinessException;

    String copy(BaseReqDto baseReqDto) throws BusinessException;

    Object doAction(BaseReqDto queryParam) throws BusinessException;


    /**
     * 新增接口
     *
     * 只查询分页数据
     *
     * @param baseReqDto
     * @return
     * @throws Exception
     */
    default Pager queryPageCount(BaseReqDto baseReqDto) throws Exception {
        return new Pager();
    }
}
