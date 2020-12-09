import env from '../env'
import { combine, uniformProxy,catchException, genFetchOptions, toJSON } from '@mdf/cube/lib/helpers/util'

export const getLoginUser = async (ctx) => {
  let url = combine(env.HTTP_SERVICE_BASEURL, `user/getOrgsAndStores?token=${ctx.token}`);
  let config = {
    url,
    method: 'GET'
  };
  let json = await uniformProxy(config);
  if (json.code !== 200) {
    ctx.logger.error(`获取登录用户失败：【接口】${url} 【异常】${json.message}`);
    // ctx.redirect('/login');
    // return;
  }
  const loginUser = json.data || {};
  url = combine(env.HTTP_SERVICE_BASEURL, `tenant/find?token=${ctx.token}`);
  config = {
    url,
    method: 'GET'
  };
  json = await uniformProxy(config);
  if (json.code !== 200) {
    ctx.logger.error(`获取租户信息失败：【接口】${url} 【异常】${json.message}`);
    return;
  }
  loginUser.logo = json.data.logo;
  loginUser.tenant = json.data;
  url = combine(env.HTTP_SERVICE_BASEURL, `option/getOptionData?token=${ctx.token}`);
  config = {
    url,
    method: 'POST',
    params: {
      optionId: 'sys_option'
    }
  };
  json = await uniformProxy(config);
  const option = {};
  if (json.code !== 200) {
    ctx.logger.error(`获取系统参数失败：【接口】${url} 【异常】${json.message}`);
  } else {
    Object.assign(option, json.data);
  }
  url = combine(env.HTTP_SERVICE_BASEURL, `option/getOptionData?token=${ctx.token}`);
  config = {
    url,
    method: 'POST',
    params: {
      optionId: 'business_option'
    }
  };
  json = await uniformProxy(config);
  if (json.code !== 200) {
    ctx.logger.error(`获取业务参数失败：【接口】${url} 【异常】${json.message}`);
  } else {
    Object.assign(option, json.data);
  }
  loginUser.option = option;
  return loginUser;
}

export const doFetch = function (url) {
  const options = genFetchOptions('get');
  return fetch(url, options)
    .then(toJSON, catchException)
    .then(json => {
      return {
        code: 200,
        data: json
      }
    })
}


