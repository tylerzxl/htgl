import React from "react";
import { Route, Router as RouterComponent } from "react-router";
import App from "../containers/App";
import axios from "axios";
import mtl from "mtl-js-sdk";

const getUrlParams = () => {
  let res = {};
  let serach = window.location.search;
  if (!serach) {
    serach = window.location.href.split("?").pop();
  }
  let str = decodeURIComponent(serach);
  str = str.trim().replace(/^[?#&]/, "");

  if (!str) {
    return res;
  }
  str.split("&").forEach((strItem) => {
    if (strItem) {
      let parts = strItem.split("=");
      let partKey = parts[0],
        partValue = parts[1] || "";
      res[partKey] = partValue;
    }
  });
  return res;
};

export class Router extends React.Component {
  state = { loading: true };

  componentDidMount() {
    const { s } = getUrlParams();
    if (mtl.upesn && mtl.upesn.getUserYHTInfo) {
      mtl.upesn.settingNavBar({
        hide: s,
        success: function (res) {
          if (s == 1) {
            mtl.upesn.settingNavBar = undefined;
          }
        },
        fail: function (err) {
          console.log("settingNavBar error", err.message);
        },
      });
      mtl.upesn.getUserYHTInfo({
        success: (result) => {
          const { yhtToken, yht_access_token, yht_userid, tenant_id } = result;
          const billno = window.location.pathname.split('/')[3];//单据固定URL  view/billtype/billno
          const token = yhtToken || yht_access_token;
          if (token && yht_userid && tenant_id) {
            axios
              .get(
                  "/mobile/app/index/yht/token/context",
                {
                  params: {
                    yhtAccessTokenCipher: token,
                    userId: yht_userid,
                    tenantId: tenant_id,
                    billno
                  },
                  withCredentials: true,
                }
              )
              .then((response) => {
                console.log(response);
                if(response.data?.mobileExists || response?.data.data?.mobileExists) {//移动端返回结果多一层
                  this.setState({ loading: false });
                }else{
                  cb.utils.confirm("暂无移动模版",()=>mtl.navigateBack(),()=>mtl.navigateBack())
                }
              })
              .catch((error) => {
                console.log(error);
                this.setState({ loading: false });
              });
          } else {
            this.setState({ loading: false });
          }
        },
        fail: (err) => {
          console.log(err);
          this.setState({ loading: false });
        },
      });
    } else {
      const { userAgent } = window.navigator;
      if (
        userAgent.indexOf("miniProgram") !== -1 ||
        userAgent.indexOf("wxwork") !== -1 ||
        userAgent.indexOf("mtlAndroid") !== -1 ||
        userAgent.indexOf("Android_") !== -1 ||
        userAgent.indexOf("mtlIOS") !== -1 ||
        userAgent.indexOf("QYios") !== -1
      ) {
        // TODO: 微信小程序演示用
        axios
          .get("https://mock.yonyoucloud.com/mock/6672/mockcookie")
          .then((response) => {
            const { data } = response;
            Object.values(data).forEach((item) => {
              document.cookie = item;
            });
            this.setState({ loading: false });
          })
          .catch((error) => {
            console.log(error);
            this.setState({ loading: false });
          });
      } else {
        this.setState({ loading: false });
      }
    }
  }

  render() {
    if (this.state.loading) return null;
    return (
      <RouterComponent history={this.props.history}>
        <Route path={`/view/:billtype/:billno`} component={App} />
      </RouterComponent>
    );
  }
}
