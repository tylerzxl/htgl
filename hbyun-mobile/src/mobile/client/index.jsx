import "@babel/polyfill";
// import { addComponnets } from '../ys/convert-design/regist-ys-componnets';

const hoistNonReactStatic = require("hoist-non-react-statics");
const lodash = require("lodash");

const Sentry = require("@sentry/browser");
if (process.env.SENTRY_DSN) {
  Sentry.init({ dsn: process.env.SENTRY_DSN });
}

const cb = require("@mdf/cube/lib/cube");
const envConfig = require("../common/config.env").default;
const extendConfig = require("../common/config.comp").default;
const {
  setEnvConfig,
  setCompConfig,
  setExtendComp,
} = require("@mdf/cube/lib/extend");

const { createConnect } = require("@mdf/renderer");

// 拦截器，调试用
function interceptor(transformer) {
  return (props) => {
    const nextProps = transformer(props);
    // if (nextProps.cControlType === "refer") {
      // console.log("log:", nextProps);
    // }
    return nextProps;
  };
}

const connect = createConnect({
  interceptors: [interceptor],
});

setEnvConfig(envConfig);
setCompConfig(extendConfig);

const library = window.__MetauiMobile__ || window.__TinperLibraui__;

/**
 * 组件二级状态通过修改 manifest 默认值来处理
 */
const setPropDefault = (Component, propName, defaultValue) => {
  Component.manifest = lodash.cloneDeep(Component.manifest);
  Component.manifest.props.some((prop) => {
    if (prop.name === propName) {
      prop.defaultValue = defaultValue;
      return true;
    }
    return false;
  });
};
window.SERVER_ENV = process.env.SERVER_ENV
// TODO: 临时处理
library["Card"] = library["CardBox"]; // TODO: 原有的 Card 废弃，使用 CardBox 替代
library["toolbar"] = library["ToolBar"]; // TODO: 存在 _extend.extendComp.toolbar
library["inputmultilang"] = library["Language"];
library["map"] = library["Map"];
library["hyperlinks"] = library["Input"];
library["mobile"] = library["Mobile"];
library["inputidentity"] = library["Input"];
library["filelist"] = library["UploadFile"];
library["pictureupload"] = library["UploadPicture"];
library["number"] = library["Input"];
library["rate"] = library["Rate"];
library["numberwidget"] = library["InputNumber"];

const Radio = library["Radio"];
library["optionwidget"] = Radio;

const Select = (props) => <Radio {...props} />;
hoistNonReactStatic(Select, Radio);
setPropDefault(Select, "mode", "list");
library["Select"] = Select;

const DateTimePicker = library["DateTimePicker"];

const DatePicker = (props) => <DateTimePicker {...props} />;
hoistNonReactStatic(DatePicker, DateTimePicker);
setPropDefault(DatePicker, "dateMode", "picker-date");
library["DatePicker"] = DatePicker;

const TimePicker = (props) => <DateTimePicker {...props} />;
hoistNonReactStatic(TimePicker, DateTimePicker);
setPropDefault(TimePicker, "dateMode", "picker-time");
library["TimePicker"] = TimePicker;

// const Search = library["Search"];
// library["convenientquery"] = Search;
// addComponnets(library);
const comps = {};
Object.keys(library).forEach((name) => {
  const Comp = library[name];
  if (typeof Comp !== "string" && !Comp.manifest) {
    // 加个 name 好调试
    library[name].manifest = { name };
  }
  comps[name] = connect({ manifest: Comp.manifest })(Comp);
});

setExtendComp(comps);

// register businessContext
const businessContext = require.context("business");
cb.registerBusinessContext(businessContext);

cb.rest.nodeEnv = process.env.NODE_ENV;

require("./client");
