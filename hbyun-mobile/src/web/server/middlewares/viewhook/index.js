import React from 'react'
import ReactDOMServer from 'react-dom/server'
import beautify from 'js-beautify'

import html from './html'
import Isomorph from 'src/common/redux/Isomorph'
import routes from 'src/common/routes'
import customRoutesMap from '../../../common/routes/config.route'
const routesMap = {
  index: routes
};

let rebuildPaths = ['/', '/portal', '/register', '/wechat', '/forget','/menu'];
//TODO 用户自定义
let customPaths=[];
Array.isArray(customRoutesMap) && customRoutesMap.length>0 && customRoutesMap.forEach(route=>{
  const path=route.path;
  if(path){
     customPaths.push(path);
  }
});
rebuildPaths=Array.from(new Set(rebuildPaths.concat(...customPaths)));






const directNext = function (ctx) {
  if (rebuildPaths.indexOf(ctx.path) > -1
    || ctx.path.startsWith('/login')
    || ctx.path.startsWith('/billing')
    || (process.env.PREFIX && ctx.path.startsWith(process.env.PREFIX))
    || ctx.path.startsWith('/meta')
    || ctx.path.startsWith('/platform')
    || ctx.path.startsWith('/echartcarousel'))
    return false;
  return true;
}

export default function viewhook(_options = { beautify: true, internals: true }) {
  const options = Object.assign({}, _options)

  return async function (ctx, next) {

    //TODO 请求总有/json /json/version
    if(ctx.path.includes('/json')) return;

    if (directNext(ctx)) {
      await next();
      return;
    }
    const isTouch = ctx.header['user-agent'].match(/(Android);?[\s\/]+([\d.]+)?/) || ctx.path === '/billing/touch' || ctx.path === '/login/touch';
    if (isTouch)
      ctx.entryPoint = 'touch';
    else if (ctx.path === '/billing')
      ctx.entryPoint = 'billing';
    else
      ctx.entryPoint = 'index';
    ctx.store = Isomorph.createStore(ctx.entryPoint)
    ctx.history = Isomorph.createHistory(ctx.store, ctx.path)
    ctx.render = function (pageInfo, internals = options.internals || true) {
      const render = internals
        ? ReactDOMServer.renderToString
        : ReactDOMServer.renderToStaticMarkup

      let markup = render(<Isomorph store={ctx.store} history={ctx.history}
        routes={routesMap[ctx.entryPoint]} />)

      if (options.beautify) {
        markup = beautify.html(markup)
      }

      ctx.type = 'html';
      // 判断页面是否为billing, 传入html加载不同的依赖
      ctx.body = html(Object.assign({ entryPoint: ctx.entryPoint }, pageInfo), markup, ctx.store.getState())
    }

    await next()
  }
}
