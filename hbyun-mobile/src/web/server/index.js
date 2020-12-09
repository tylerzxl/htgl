require('@babel/polyfill')
const envConfig = require('./env').default;
const extendConfig = require('../common/config.comp').default;
const { setEnvConfig, setCompConfig } = require('@mdf/cube/lib/extend')
// const extendComp = require('../common/registerMetaComp').default;
// const basicComponents = require('../common/extends').default
setEnvConfig(envConfig)
setCompConfig(extendConfig)

require('./server')