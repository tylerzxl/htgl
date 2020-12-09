import env from "../../../env";

const isDev = process.env.NODE_ENV === "development";
const baseUrl = env.HTTP_SCRIPT_BASEURL;
const suffix = env.HTTP_SCRIPT_SUFFIX;
const random = isDev ? "" : `?_=${env.STATIC_RANDOM_SUFFIX}`;
// 开发环境使用样式热更新, 不再用打包后的独立css文件
const loadCss =
  process.env.NODE_ENV === "development"
    ? ""
    : `<link href="${baseUrl}/styles/default/mobile.index.min.css" rel="stylesheet" type="text/css" />`;

export default function html() {
  return `<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no, minimal-ui">
    <title>MDF-Mobile</title>
    <script src="${baseUrl}/react/umd/react.production.min.js"></script>
    <script src="${baseUrl}/react-dom/umd/react-dom.production.min.js"></script>
    <script src="${baseUrl}/componentLib/index.js"></script>
    <link rel="stylesheet" type="text/css" href="${baseUrl}/componentLib/index.css" />
    ${loadCss}
    <script src="${baseUrl}/vconsole/vconsole.min.js"></script>
    <link
    rel="stylesheet"
    type="text/css"
    href="${baseUrl}/styles/fonts/iconfont.css"
  />
  </head>
  <body>
    <div id="container"></div>
    <div id="popup-container"></div>
    <script src="https://at.alicdn.com/t/font_304307_jezcocolkm.js"></script>
    <script>
      (function(doc, win) {
       window.__fontUnit = 0
       var docEl = doc.documentElement,
           recalc = function() {
               var clientWidth = docEl.clientWidth;
               if (!clientWidth) return;
               if (clientWidth >= 750) { //750这个值，根据设计师的psd宽度来修改，是多少就写多少，现在手机端一般是750px的设计稿，如果设计师给的1920的psd，自己用Photoshop等比例缩小
                   window.__fontUnit = 100;
                   docEl.style.fontSize = window.__fontUnit + 'px';

               } else {
                   window.__fontUnit = 100 * (clientWidth / 750);
                   docEl.style.fontSize = window.__fontUnit + 'px'; //750这个值，根据设计师的psd宽度来修改，是多少就写多少，现在手机端一般是750px的设计稿，如果设计师给的1920的psd，自己用Photoshop等比例缩小
               }
           };

       if (!doc.addEventListener) return;
    //   win.addEventListener(resizeEvt, recalc, false);
       doc.addEventListener('DOMContentLoaded', recalc, false);

   })(document, window);
    </script>
    <script>
      // init vConsole
      if (window.location.search.includes('debug=true')) {
        var vConsole = new VConsole();
      }
    </script>
    <script src="${baseUrl}/scripts/vendor${suffix}.js${random}"></script>
    <script src="${baseUrl}/javascripts/mobile.index${suffix}.js${random}"></script>
  </body>
</html>`;
}
