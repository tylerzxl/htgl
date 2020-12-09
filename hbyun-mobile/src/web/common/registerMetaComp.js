// ncc扩展
import  webNcc from '@mdf/metaui-web-ncc';
const {basic={},filter={},refer={}} = webNcc;
// 扩展 cControlType
const extendComp = {
    // 'basic': Object.assign({},{Input,Select,Refer,DatePicker,Filter,PageIcon,ReferToolbar,ReferModal,SearchTree}),
    'basic': Object.assign({},basic),
    'filter':Object.assign({},filter),
    'refer':Object.assign({},refer)
    
}

// ys扩展风格扩展 cControlType
// const extendComp = {
//     'basic': {},
//     'meta': {
//         NCCTreeRefer: '',

//     }
// }


export default extendComp;
