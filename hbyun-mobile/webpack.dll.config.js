const path = require('path');
const webpack = require('webpack');

const isProduction = process.env.NODE_ENV === 'production'

const vendors = [
  'async-validator',
  'cookies-js',
  'classnames',
  'immutable',
  'isomorphic-fetch',
  'fixed-data-table-2',
  'keymirror',
  'redux',
  'redux-logger',
  'redux-thunk',
  // 'react',
  // 'react-dom',
  'react-redux',
  'react-router',
  'react-router-redux',
  'warning',
  'echarts-for-react',
  'react-multi-crops',
  'react-amap',
  'react-color',
  'react-draggable',
  'react-sortablejs'
];

const libraryName = 'vendorLibrary';
const manifestFileName = isProduction ?
  `manifest.production.json` :
  `manifest.development.json`

const config = {
  mode: isProduction ? 'production' : 'development',
  entry: {
    vendor: vendors
  },
  output: {
    path: path.join(__dirname, 'static/scripts'),
    filename: isProduction ? `vendor.min.js` : `vendor.js`,
    library: libraryName,
  },
  module: {
    noParse: [/src/]
  },
  plugins: [
    new webpack.DllPlugin({
      path: path.join(__dirname, 'static/scripts', manifestFileName),
      name: libraryName,
      context: __dirname,
    })
  ],
  devtool: 'source-map',
  externals: {
    "react": "React",
    "react-dom": "ReactDOM"
  }
};

module.exports = config
