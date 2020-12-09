
# MDF 2.0


1. 文档地址：http://tinper.org/mdf


1. 安装用友内网ynpm工具 `npm install ynpm-tool -g` 来安装.
2. 正常使用`ynpm install`来安装依赖包，一些@mdf/**类的包全部私有化，所有不能使用外网npm
3. 启动方式还是使用npm即可

1. 相关扩展组件在common下的extends中扩展。

1. package.json 的script中添加了不同环境的快捷启动命令
2. common/config.env.js 根据需要根据不同环境配置后台地址

1. 增加前端路由，需要在commom/routes 下添加页面和逻辑
2. 在common/routes/config.route.js 下添加配置（页面、reducer、路由配置）

1. yxyweb框架按照功能拆分成不同的包来维护；
2. 如果遇到功能修改，需要修改对应的依赖包；

编译打包统一执行 npm run build
开发者中心环境部署，内置四个环境：
测试环境执行命令：  npm run start:test
调试环境执行命令：  npm run start:dev
日常环境环境执行命令：  npm run start:daily
生产环境执行命令：  npm run start

用户可根据部署环境不同，自由扩展；需要在common/config.env.js 中配置对应的服务地址
client 端统一执行命令 npm run debug:client
node 服务根据环境分为三个环境：
线上测试环境：npm run debug:server
线上日常环境：npm run debug:server:daily
本地测试环境：npm run debug:server:dev





SVG图标说明文档:  [Link](https://www.showdoc.cc/page/15308269)
