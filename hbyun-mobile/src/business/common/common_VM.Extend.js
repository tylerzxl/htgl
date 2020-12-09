const env = require('../../web/common/config.env');
cb.define(function () {
  var common_VM_Extend = function () {
    // 云打印
    var print = function (viewmodel, serverUrl, serverParams) {
      viewmodel.on('beforePrintpreview', function (params) {
        let cmdParameter = JSON.parse( params.cmdParameter);
        //动态的修改cmdParameter内容
        const _getSelectRowIds = (gridModel, para, distinct = true) => { //默认去重
          if (!gridModel) return
          let selectDatas = para && para.params && para.params.index != null ?
            [gridModel.getRow(para.params.index)] :
            gridModel.getSelectedRows();
          let ids = []
          if (selectDatas && selectDatas.length > 0) {
            selectDatas.forEach(function (row) {
              if (row.id) {
                ids.push(row.id)
              }
            })
          }
          return distinct ? [...new Set(ids)] : ids
        }
        const ids = _getSelectRowIds(viewmodel.getGridModel());
        if (ids && ids.length > 0) {
          let url = `${env.HTTP_SERVICE_BASEURL}/uniform/bill/print`;
          let billNo = viewmodel.getParams().cardKey;
          url = url + '?billnum='+billNo+'&ids='+ids.join(',');
          cmdParameter.serverUrl = encodeURIComponent(url);
        } else {
          cb.utils.alert("请先选择数据",'warning');
          return false;
          // const condition = viewmodel.getCache('lastSearchCondition') ? JSON.stringify(viewmodel.getCache('lastSearchCondition').condition) : null;
          // params = Object.assign(params, {
          //   condition
          // })
        }

        console.log(JSON.stringify(cmdParameter));
        //最后将修改后的内容赋值给params；
        params.cmdParameter = JSON.stringify(cmdParameter);
     
      })
    }

    return {
      print
    }
  }();
  try {
    module.exports = common_VM_Extend;
  } catch (error) { }
  return common_VM_Extend;
});
