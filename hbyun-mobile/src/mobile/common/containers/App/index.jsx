import React, { useEffect, useState } from "react";
import { Loading } from "tinper-bee";
import imgsrc from "../../../../../static/viewer/images/mdf_loading.gif";
import queryString from "query-string";
import "./style.css";
const { render } = require("@mdf/renderer");
const staterule = require("@mdf/cube/lib/staterule/rule");

export default function App(props) {
  const { billtype, billno, tplid } = props.match.params;
  const [state, setState] = useState({ isLoading: true });
  const [isLoading, setLoading] = useState(true);
  useEffect(() => {
    window.cb.invoker.subscribeLoadData((loading) => {
      setLoading(loading)
    })
  })
  useEffect(() => {
    if (billno !== state.billno) {
      const params = {};
      // TODO:
      const { _id, mode, readOnly = false,tplid } = queryString.parse(
        props.location.search
      );
      params.metaType = billtype;
      params.mode = mode || "add";
      params.id = _id;
      params.readOnly = readOnly;
      params.tplid = tplid;
      const data = {
        billtype,
        billno,
        params,
        terminalType: 3,
      };
      cb.loader.runCommandLine("bill", data, null, (viewModel, uiData) => {
        setLoading(false)
        setState({
          billno,
          viewModel,
          uiData,
        });
        // TODO: 现有逻辑，点击列表行会请求详情页的 ui 元数据，应改成直接跳转
        viewModel.addListener({
          communication: (data) => {
            if (billtype.toLowerCase() === "yylist") {
              const {
                payload: {
                  metaData,
                  vm,
                  params: {
                    billno,
                    billtype,
                    params: { id, readOnly, mode },
                  },
                },
              } = data;
              let url = `/view/yyarchive/${billno}?terminalType=3&mode=${
                mode === "edit" ? "browse" : mode
              }`;
              if (id) {
                url += "&_id=" + id;
              }
              if (readOnly) {
                url += "&readOnly=" + readOnly;
              }
              cb.route.pushPage(url);
            }
          },
        });
        // TODO: 审批状态存在时，监听审批状态的变化，控制按钮状态
        if (viewModel && viewModel.get("verifystate")) {
          viewModel.get("verifystate").addListener({
            setValue: function () {
              // 当单据按钮存在时，通过 staterule 控制显示状态
              staterule.runState(viewModel);
              // 兼容其他状态变回开立态按钮显示问题
              if (viewModel.get("verifystate").getValue() === 0) {
                if (viewModel.get('btnSubmit')) {
                  viewModel.get('btnSubmit').setVisible(true)
                }
                if (viewModel.get('btnEdit')) {
                  viewModel.get('btnEdit').setVisible(true)
                }
                if (viewModel.get('btnSaveAndAdd')) {
                  viewModel.get('btnSaveAndAdd').setVisible(false)
                }
                if (viewModel.get('btnSave')) {
                  viewModel.get('btnSave').setVisible(false)
                }
                if (viewModel.get('btnAbandon')) {
                  viewModel.get('btnAbandon').setVisible(false)
                }
              }
            },
          });
        } else {
          // 无审批状态时，也要改变按钮状态 TODO staterule 需和PC同步
          if (billtype.toLowerCase() !== "yylist") {
            staterule.runState(viewModel);
            if (
              params.mode &&
              params.mode == "browse" &&
              viewModel.get("btnSubmit")
            ) {
              viewModel.get("btnSubmit").setVisible(false);
            }
          }
        }
      });
    }
  }, [billno]);
  const { uiData, viewModel } = state;

  // TODO:
  if (uiData && uiData.cBillName) {
    document.title = uiData.cBillName;
    mtl.upesn &&
      mtl.upesn.settingNavBar &&
      mtl.upesn.settingNavBar({
        centerItems: [
          {
            title: uiData.cBillName,
          },
        ],
      });
  }
  const beeIcon = <img src={imgsrc} className="beeIcon" />;
  const beeTip = <div className="beeTip">加载中...</div>;
  return (
    <div>
      <Loading
        className="beeLoading"
        tip={beeTip}
        container={this}
        show={isLoading}
        loadingType="custom" //启用自定义图标
        indicator={beeIcon} //自定义图标的内容
      />
      {uiData &&
        uiData.view &&
        render({ ...uiData.view, cControlType: "view" }, viewModel, props)}
    </div>
  );
}
