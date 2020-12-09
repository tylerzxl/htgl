import "@babel/polyfill";

import React from "react";
import ReactDOM from "react-dom";
import { Provider } from "react-redux";
import cb from "@mdf/cube/lib/cube";
import "./init";
import "@mdf/cube/lib/helpers/polyfill";
import { configureStore, createHistory } from "../common/store";
import { Router } from "../common/route";
// import { push, replace, goBack } from "react-router-redux";
import env from "@mdf/cube/lib/helpers/env";
import { initCache } from "@mdf/metaui-mobile/lib/helper/injectCache";
// import "@mdf/theme-mobile/theme";
import "./client.css";
const businessContext = require.context("business");
cb.registerBusinessContext(businessContext);

const { pathname } = window.location;
export const store = configureStore();
const history = createHistory(store, pathname);
initCache(cb);
env.INTERACTIVE_MODE = "mobile";
cb.rest.nodeEnv = process.env.NODE_ENV;
cb.rest.interMode = env.INTERACTIVE_MODE;
cb.rest.terminalType = 1; // TODO: 由于 terminalType == 3 (移动)，请求元数据时会丢失 toolbar，所以暂时使用 PC 的。

const getPathWith = (page) => {
  const elements = history.location.pathname.split("/").slice(0, 4);
  if (page) {
    elements.push(page);
  }
  return elements.join("/");
};

cb.route = {
  pushPage: function (route) {
    history.push(route);
  },
  push: function (page) {
    history.push(getPathWith(page));
  },
  replacePage: function (route) {
    history.replace(route);
  },
  replace: function (page) {
    history.replace(getPathWith(page));
  },
  goBack: function () {
    history.goBack();
  },
};

cb.utils.loading = function (status) {
  store.dispatch({
    type: "PLATFORM_UI_TOGGLE_LOADING_BAR_STATUS",
    status,
  });
};

// 兼容处理fetch问题
cb.rest.mode = "xhr";
const renderDom = () => {
  ReactDOM.render(
    <Provider store={store}>
      <div>
        {/* <Loading /> */}
        <Router history={history} />
      </div>
    </Provider>,
    document.getElementById("container")
  );
};
renderDom();

if (module.hot) {
  module.hot.accept();
}
