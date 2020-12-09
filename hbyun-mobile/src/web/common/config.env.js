/*
* 全局配置的环境变量
*
* @param HTTP_SERVICE_BASEURL
* 请求的后台地址，需要配置
* 根据工程环境配置不通变量
*
* 打印地址，需配置（具体需要跟打印对接，当前不同环境下的地址如下：pre：http://print-y3me-pre.diwork.com，daily：https://u8cprint-daily.yyuap.com，test：http://u8cprint.test.app.yyuap.com）
*
* @param HTTP_USER_LOGIN
* 用户登陆接口，用于前端调试，获取token，需要配置
*
* @param USER_LOGIN_PARAMS
* 配置登陆的参数，用于前端调试，获取token，需要配置
*
* @param AUTH_WHITELIST
* 不做token校验的白名单，接口，页面等，需要配置
*
* @param HTTP_CONTENT_TYPE
* 接口请求的content-type类型，可扩展，内置 XLS JSON PDF FORM
*
*
* @param IS_REDIRECT_LOGIN
* BOOLEAN token失效是否重定向登陆页
* */

/*根据不同的环境，配置地址*/
let base_url='',
    workflow_url = '', //审批url
    print_url='',
    file_url='';//附件url
const config_env=process.env.SERVER_ENV;
switch (config_env) {//根据当前环境类型定义不同变量值
  case 'prod':
    base_url='http://pf-ysdemo1216-be.daily.app.yyuap.com';
    print_url="http://u8cprint-daily.yyuap.com";
    workflow_url = 'https://ys-u8c-daily.yyuap.com';
    file_url = 'https://ezone-u8c-daily.yyuap.com';
    break;
  case 'daily':
    base_url='http://pf-ysdemo1216-be.daily.app.yyuap.com';
    print_url="http://u8cprint-daily.yyuap.com";
    workflow_url = 'https://ys-u8c-daily.yyuap.com';
    file_url = 'https://ezone-u8c-daily.yyuap.com';
    break;
  default:
    base_url='http://127.0.0.1:8080';
    print_url="http://u8cprint-daily.yyuap.com";
    workflow_url = 'https://ys-u8c-daily.yyuap.com';
    file_url = 'https://ezone-u8c-daily.yyuap.com';
}
export default {
  HTTP_SERVICE_BASEURL:base_url,
  HTTP_PRINT_SERVER:print_url,
  HTTP_WORKFLOW_SERVER:workflow_url,
  HTTP_FILE_SERVER:file_url,
  HTTP_PRINT_DATA_SERVERURL:'http://pf-ysdemo1216-be.daily.app.yyuap.com/bill/print', //打印的回调的数据地址
  HTTP_USER_LOGIN: '/user/authorize',
  domainCode:'PF',//打印需要的域code值
  appCode:'u8c',//审批流需要的appsource
  USER_LOGIN_PARAMS:{
    username : 'zhaolnc@yonyou.com',
    password :'zhao2013',
  },
  AUTH_WHITELIST:['/demo','/menu'],

  HTTP_CONTENT_TYPE:{
    //JSON: 'application/json',
  },
  IS_REDIRECT_LOGIN:true

}
