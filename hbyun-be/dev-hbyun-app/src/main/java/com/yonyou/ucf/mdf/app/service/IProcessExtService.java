package com.yonyou.ucf.mdf.app.service;

import com.yonyou.ucf.mdd.ext.model.BillContext;

public interface IProcessExtService {

  String getBillTypeId(String subId, String billNumber, BillContext billContext) throws Exception;
}
