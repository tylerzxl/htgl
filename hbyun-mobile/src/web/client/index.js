require('@babel/polyfill')
const cb = require('@mdf/cube/lib/cube')
const envConfig = require('../common/config.env').default;
const extendConfig = require('../common/config.comp').default;
const { setEnvConfig, setCompConfig, setExtendComp } = require('@mdf/cube/lib/extend')
const extendComp = require('../common/registerMetaComp').default;
const basicComponents = require('../common/extends').default
extendConfig.iconfont && require('@mdf/theme/theme-ncc/font_ncc/iconfont');
setEnvConfig(envConfig)
setCompConfig(extendConfig)
// register extend components
setExtendComp(basicComponents)
setExtendComp(extendComp)

// register businessContext
const businessContext = require.context('business')
cb.registerBusinessContext(businessContext)

cb.rest.nodeEnv = process.env.NODE_ENV;

require('./client')