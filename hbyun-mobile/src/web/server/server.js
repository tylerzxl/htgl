import 'ignore-styles'
import path from 'path';
import Koa from 'koa';
import serve from 'koa-static';
import compress from 'koa-compress';
import bodyParser from 'koa-bodyparser';
import chalk from 'chalk';
import viewhook from './middlewares/viewhook';
import log4js from '@mdf/middlewares-log4js';
import auth from '@mdf/middlewares-auth';
import router from './router';
import env from './env';
import '@mdf/cube/lib/helpers/polyfill';
import envConfig from './env';
require('isomorphic-fetch');

new Koa()
  .use(log4js()) // 日志不能删除@mdf/metaui-web有调用
  .use(auth({config: envConfig}))  //token校验
  .use(viewhook({ beautify: env.HTTP_HTML_BEAUTIFY })) // 处理模板
  .use(compress()) // gzip
  .use(bodyParser({ enableTypes: ['json'], jsonLimit: '10mb' })) // 上传
  .use(router.routes()) // 路由表
  .use(router.allowedMethods()) // 访问模式
  .use(serve(path.join(process.cwd(), 'static', 'public'), { maxage: 365 * 24 * 60 * 60 * 1000 }))
  .use(serve(path.join(process.cwd(), 'static'))) // , { maxage: 365 * 24 * 60 * 60 * 1000 }
  .listen(env.HTTP_SERVER_PORT) // 端口

  console.log(chalk.blue(`MDF后端Node服务启动成功，端口：${env.HTTP_SERVER_PORT} 当前环境：${process.env.NODE_ENV}`))
