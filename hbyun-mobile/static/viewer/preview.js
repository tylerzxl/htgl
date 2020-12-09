/**
 * 初始化数据
 */
const source = {
  "0": {
    label: "东风移动",
    value: "https://ucf-mdd-mobile-node-daily.yyuap.com",
  },
  "1": {
    label: "设计器默认数据源",
    value: "https://mdf-mobile-daily.yyuap.com",
  },
  "2": {
    label: "预发布环境数据源",
    value: "https://mdf-mobile-runtime-pre.diwork.com"
  }
};
const mobileType = [
  { key: "0", value: "iPhone 8 - 375x667", width: 360, height: 700 },
  { key: "1", value: "iPhone 8 plus - 414x736", width: 414, height: 736 },
  { key: "2", value: "iPhone SE - 320x568", width: 320, height: 568 },
  { key: "3", value: "iPhone11/11 Pro Max - 414x896", width: 414, height: 896 },
  { key: "4", value: "iPhone11 Pro - 375x812", width: 375, height: 812 },
  { key: "5", value: "Nexus 5 - 360x640", width: 360, height: 640 },
  { key: "6", value: "Nexus 5x - 411x731", width: 411, height: 731 },
  { key: "7", value: "华为 nova -  412x732", width: 412, height: 732 },
];
const phone = document.getElementById("phone_1"),
  iframe = document.getElementById("frame_1");
const qrcode = new QRCode(document.getElementById("qrcode"), {
  width: 240,
  height: 240,
});
var param = getUrlParams() || {};
var titleColor = "white";
var url = `${param.url}` || "http://mobile.yyuap.com/mtl/home/index.html";
const { billType, billNo, tenantId, hideTitle } = param;
const _DEF_URL_KEY = "__DEF_URL_KEY";
/**
 * 调用初始化信息
 */

function init() {
  //let org = window.localStorage.getItem(_DEF_URL_KEY);
  // let origin = org ? source[org].value :window.location.origin;//有缓存去缓存，无缓存获取url上的地址
  if (billType && billNo) {
    const origin = window.location.origin;
    url = `${origin}/view/${billType}/${billNo}?tenantId=${tenantId}&s=1&terminalType=3`;
  }

  if(hideTitle && hideTitle=='true') {
    document.getElementById("header").style.display="none";
  }
  //初始化调用
  //org && (document.getElementById("selectKey")[org].selected = true);
  console.log("url:========>", url);

  document.getElementById("selectPhone").value = 4;
  const item = mobileType[4];
  setNewHW(item.width, item.height);
}

/*View*/
function updateView(view) {
  if (view) {
    phone.className = "phone view_" + view;
  }
}

function changeTitleColor() {
  if(titleColor=="white") {
    titleColor="red";
  } else if(titleColor=="red") {
    titleColor="black";
  } else if(titleColor=="black") {
    titleColor="white";
  }

  // switch(titleColor) {
  //   case "while"
  // }
  let bar = document.getElementById("phone-bar");
  bar.style.backgroundColor = titleColor;
}

/*Controls*/
function updateIframe() {
  iframe.src = url;
  makeCode();
  phone.style.width = document.getElementById("iframeWidth").value + "px";
  phone.style.height = document.getElementById("iframeHeight").value + "px";
}

function getUrlParams() {
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
}
document.getElementById("views").addEventListener("click", function (evt) {
  updateView(evt.target.value);
});

document
  .getElementById("selectPhone")
  .addEventListener("change", function (evt) {
    const index = evt.target.value;
    const item = mobileType[index];
    setNewHW(item.width, item.height);
  });
document.getElementById("status").addEventListener("change", function (evt) {
  const status = evt.target.checked;
  if (status === true) {
    url = url.replace("s=0", "s=1");
  } else {
    url = url.replace("s=1", "s=0");
  }
  makeCode();
});

//处理数据源
// document.getElementById("selectKey").addEventListener("change", function (evt) {
//   const value = evt.target.value;
//   const newUrl = source[value].value;
//   window.localStorage.setItem(_DEF_URL_KEY, value);
//   url = `${newUrl}/view/${billType}/${billNo}?tenantId=${tenantId}&s=1&terminalType=3`;
//   updateIframe();
// });
function makeCode() {
  if (url) {
    qrcode && qrcode.makeCode(parseURL(url));
  }
}

function changeHW() {
  const nw = document.getElementById("iframeWidth").value;
  const nh = document.getElementById("iframeHeight").value;
  setNewHW(nh, nw);
}
function setNewHW(w, h) {
  document.getElementById("iframeWidth").value = w;
  document.getElementById("iframeHeight").value = h;
  updateIframe();
}

function parseURL(url) {
  // const cookie = getCookies();
  // const yht_access_token = cookie["yht_access_token"];
  // const yht_userid = cookie["yonyou_uid"]; //待确认是否为用户id
  // const yonyou_uname = cookie["yonyou_uname"];
  // if (yht_access_token && yht_userid && yonyou_uname) {
  //   return `${url}&isPreview=true&yht_access_token=${yht_access_token}&yht_userid=${yht_userid}&yonyou_uname=${yonyou_uname}`;
  // }
  return url;
}
function getCookies() {
  //"key=value;key=value"
  //["key=value","key=value"]
  var cookies = document.cookie.split(";");
  var result = {};

  for (var i = 0; i < cookies.length; i++) {
    //"key=value"
    //["key", "value"]
    var keyvaluepair = cookies[i].split("=");
    result[keyvaluepair[0].trim()] = keyvaluepair[1];
  }
  return result;
}
function back() {
  window.history.back();
}

function copy() {
  let transfer = document.createElement("input");
  document.body.appendChild(transfer);
  transfer.value = parseURL(url);
  transfer.select();
  document.execCommand("copy");
  document.body.removeChild(transfer);
  alert("复制成功");
}

init();
updateIframe();
