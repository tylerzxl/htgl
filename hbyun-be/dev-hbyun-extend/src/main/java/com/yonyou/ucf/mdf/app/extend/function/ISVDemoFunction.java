package com.yonyou.ucf.mdf.app.extend.function;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.yonyou.cloud.yonscript.plugin.YonScriptNativeFunction;

public class ISVDemoFunction implements YonScriptNativeFunction {

  @Override
  public String getName() {
    return "isvdemofunction";
  }


  public Object invoke(V8Object v8Object, V8Array v8Array) {
    String str = v8Array.get(0).toString();
    return ("从 ISVDemoFunction返回信息 = " + str);
  }
}

