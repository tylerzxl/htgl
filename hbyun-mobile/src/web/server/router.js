import Router from 'koa-router'
import sourcemapController from './controllers/sourceMap';
import ueditorController from './controllers/ueditor';
import pageController from './controllers/page';
import fetchController from './controllers/fetch';
const router = Router()
import routesMap from '../common/routes/config.route'
require('@mdf/plugin-meta/lib/router').default(router);
//sourceMap
router.get('/sourcemap/:name/:pwd', sourcemapController.sourcemapByNamePwd);
router.get('/scripts/*.min.js.map', sourcemapController.scripts);

//ueditor
router.get('/basictest', ueditorController.basictest);
router.all('/ueditor/ue', ueditorController.ue);
// page
router.get('/login', pageController.login);
router.get('/menu', pageController.menu);
router.get('/register', pageController.register);
router.get('/wechat', pageController.wechat);
router.get('/forget', pageController.forget);
router.get('/expire', pageController.expire);
router.get('/page/**', pageController.page);
router.get('/portal', pageController.portal);
//添加代理扩展脚本文件
router.get('/gentenextcode/getExtFileContent', pageController.extscripturls);

//TODO 用户配置的页面
  Array.isArray(routesMap) && routesMap.length>0 && routesMap.map(route=>{
  const path=route.path;
  if(path){
    let controllerKey='';
    const pathArr=path.split('/');
    if(pathArr.length==1){
      controllerKey=pathArr[0]
    }else if(pathArr.length>1){
      controllerKey=pathArr[1]
    }
        return router.get(path, pageController[controllerKey]);
  }else {
    return null;
  }

})




// 测试fetch是否可用
router.get('/test/fetch', fetchController.fetch);

// 跳到门户页面
router.get('/', async function (ctx) {
  const redirectUrl = (ctx.host.indexOf('yonyoucloud') === -1 && ctx.host.indexOf('yonyouup') === -1) ? '/menu' : '/index.html';
  ctx.redirect(ctx.entryPoint === 'touch' ? '/billing' : redirectUrl);
});


export default router
