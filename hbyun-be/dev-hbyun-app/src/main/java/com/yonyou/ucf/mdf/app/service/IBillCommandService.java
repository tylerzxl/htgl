package com.yonyou.ucf.mdf.app.service;

import com.yonyou.ucf.mdd.common.model.uimeta.ui.Action;
import com.yonyou.ucf.mdf.app.exceptions.BusinessException;

import java.util.Set;

public interface IBillCommandService {

    Set<Action> getBillCommands(String billNo) throws BusinessException;
}
