/**
 * 规则模块脚本
 */
(function() {
	// 规则集合
	var ruleMap = $rulemap
	//联查集合
	var jointQuery = $jointquery
	// 数据源模型
	var data = $datasources
	
	//数据未改变标志
	data.unchange = {}
	//获取数据对象
	data.get = function(ds, id) {
        return data[ds].items[id].data
    }
	data.getValue = function(ds,id,field){
		var obj = data.get(ds, id)
        var ret = null
        if(obj){
            if(obj[field])
                ret = obj[field]
            else if(obj.get){
            	ret = obj.get(field)
            	if(ret.getValue)
            		ret = ret.getValue()
            }
        }
        return ret || 0
	}
	//设置值
	data.setValue = function(ds, id, field, value,point) {
        if (value == data.unchange) {
            return
        }

        value = data.formatFloat(value)
        if (data[ds].ismain) {
            data[ds].items["0"].data.get(field).setValue(value)
        } else {
            var ids = id.split(":")
            data[ds].items[id].__uiModel.setCellValue(ids[ids.length -1],field,value)
        }
    }
	
	data.formatFloat = function(value){
		if(isNaN(value))
			return value;
		var m = Math.pow(10, 10); 
	    return parseInt(value * m, 10) / m; 
	}
	
	function init(para){
		// 构造对象关系访问
		buildRelation(para.vm)
		
		//如果未设置触发参数，默认为主实体触发
		para.trigger = para.trigger || {}
		if(!para.trigger.ds){
			for(var ds in data){
				if(data[ds].ismain){
					para.trigger.ds = ds
					para.trigger.lineid = "0"
					break
				}
			}
		}else{
			//trigger.ds可能是childrenField，规则引擎解析时需要替换为DataSource
			for(var ds in data){
				if(data[ds].field == para.trigger.ds){
					para.trigger.ds = ds
					break;
				}
			}
		}
	}
	
	//运行规则，para为规则调用参数
	function runRuleItem(para) {
		// 参数有效性检查
		if (!para) // 参数对象
			return
		if (!para.ruleid) // 规则ID,可以是多个，使用逗号分隔
			return
		if (!para.vm) // viewmodel
			return
			
		init(para)
		
		// 解析规则id列表
		var ruleids = para.ruleid.split(",")
		for (var i = 0; i < ruleids.length; i++) {
			var rule = ruleMap[ruleids[i]]
			
			// 判断实体关系
			var r = getDsRelation(rule.target.ds, para.trigger.ds)
			//同一实体，只在当前行执行
			if(r == "s"){
				var value = exec(rule, para.trigger.lineid)
				data.setValue(rule.target.ds,para.trigger.lineid,rule.target.item,value,rule.point)
			}
			// 目标字段是父级实体
			else if (r == "p") {
				var key = getParentKey(para.trigger.ds,para.trigger.lineid,rule.target.ds)
				var value = exec(rule,key)
				data.setValue(rule.target.ds,key,rule.target.item,value,rule.point)
			} else if (r == "c") {
				// 目标实体是子实体，对目标实体内外键关联的全部对象进行处理
				var keys = getChildrenKeys(para.trigger.ds,para.trigger.lineid,rule.target.ds)
				for(var key in keys){
					var value = exec(rule, keys[key])
					data.setValue(rule.target.ds,keys[key],rule.target.item,value,rule.point)
				}
			}
			else{
				//无关实体，不处理
			}
		}
		
	}
	
	//公式计算
	function exec(rule, lineid) {
		if(!rule.condition || calc(rule, rule.condition, lineid)){
			return calc(rule, rule.expression, lineid)
		}
		return data.unchange
	}
	
	//表达式计算
	function calc(rule, expression, rulelineid, varlineid){
		var reg = /#[0-9]+/
		var jsexp = expression
		var match = jsexp.match(reg)
		var single = false
		while(match){
			var vname = match[0]
			if(vname == trimStr(jsexp))
				single = true
			var value = calcVariant(rule,vname,rulelineid,varlineid)
			jsexp = jsexp.replace(vname,value)
			match = jsexp.match(reg)
		}
		if(single) //当表达式为取单个变量值时，不执行eval以免发生类型转换
			return jsexp
		return eval(jsexp)
	}
	
	function calcVariant(rule, variant, rulelineid,varlineid){
		var v = rule.variant[variant]
		if(v.func){ //函数调用
			return calcFunc(rule,v,rulelineid)
		}
		else if(v.ds){ //属性变量，对象取值
			//确定变量实体与目标实体的关系
			var r = getDsRelation(rule.target.ds,v.ds)
			if(r == "p"){ //目标实体是变量实体的父级,需要依赖变量行ID参数，该逻辑只应在聚合函数循环取值过程中
				//return data[v.ds].items[varlineid][v.item]
				return data.getValue(v.ds,varlineid,v.item)
			}
			else if(r == "s"){ //变量与规则目标处于同一实体，取目标行的值
				//return data[v.ds].items[rulelineid][v.item]
				return data.getValue(v.ds,rulelineid,v.item)
			}
			else if(r == "c"){ //规则目标实体是变量实体的子级，递归取父级实体行
				var cur = data[rule.target.ds].items[rulelineid]
				var ds = rule.target.ds
				while(ds && ds != v.ds){
					ds = data[ds].parent
					cur = cur.__ruleParent
				}
				return cur.data[v.item] || 0
			}
		}
		return 0
	}
	
	//聚合函数计算
	function calcFunc(rule, f, rulelineid){
		//如果聚合目标实体是规则目标实体的子实体，则过滤子级数据，否则取全部数据
		var ds = data[f.ds]
		var r = getDsRelation(rule.target.ds,f.ds)
		var target = undefined
		if(r == "p")
			target = data[rule.target.ds].items[rulelineid]
		var array = new Array()
		for(var pk in ds.items){
			if(r == "p"){
				if(!isParent(target,ds.items[pk]))
					continue
			}
			array[array.length] = calc(rule, f.exp,rulelineid, pk)
		}
		return funcMap[f.func](array)
	}

	function buildRelation(vm) {
		for (var dsName in data) {
			var ds = data[dsName]
			if (ds.ismain) {
				ds.items = {}
				ds.items["0"] = {
					data: vm
				}
				//ds.items.root = vm
				for (var i = 0; i < ds.sub.length; i++) {
					buildDataRelation(ds.items["0"],"0", ds.sub[i])
				}
			}
		}
	}
	// 构造对象数据关系
	function buildDataRelation(parent, parentKey, childName) {
		var ds = data[childName]
		ds.items = ds.items || {}
		var child = null
		if(ds.field)
			child = ds.field
		else
			child = childName
		if(parent.data[child])
			child = parent.data[child]
		else if(parent.data.get)
			child = parent.data.get(child)
		if (child) {
			var rows = []
			if(child instanceof Array)
				rows = child
			else if(child.getRows)
				rows = child.getRows()
			for (var i = 0; i < rows.length; i++) {
				var o = rows[i]
				// 保存对象索引
				var childKey = parentKey + ":" + i
				ds.items[childKey] = {
					data: o,
					__ruleParent: parent,
					__uiModel: child
				}
				
				if (ds.sub) {
					for (var j = 0; j < ds.sub.length; j++) {
						buildDataRelation(ds.items[childKey], childKey, ds.sub[j])
					}
				}							
			} 
		}
	}

	// 获取数据源关系，p=parent,c=child,s=same,n=none
	function getDsRelation(dsthis, ds) {
		if (dsthis == ds)
			return "s"
		var parent = data[ds].parent
		while (parent) {
			if (parent == dsthis)
				return "p"
			parent = data[parent].parent
		}
		var child = data[dsthis].parent
		while (child) {
			if (child == ds)
				return "c"
			child = data[child].parent
		}
		return "n"
	}
	
	function getParentKey(dsName,id,parentDsName){
		var path = data[dsName].parent
		var pk = id
		while(path){
			pk = pk.substr(0, pk.lastIndexOf(":"))
			if(path == parentDsName)
				break
			path = data[path].parent
		}
		return pk
	}
	
	function getChildrenKeys(dsName,id,childDsName){
		var keys = [];
		var ds = data[childDsName]
		for(var key in ds.items){
			if(key.indexOf(id+":") == 0)
				keys[keys.length] = key
		}
		
		return keys
	}
	
	function isParent(objP,objC){
		var p = objC.__ruleParent
		while(p){
			if(p == objP)
				return true
			p = p.__ruleParent
		}
	}
	
	var funcMap = {
		"sum":function(array){
			return array.reduce(function(a, b) { return a + b }, 0)
		},
		"avg":function(array){
			return funcMap["sum"](array) / array.length
		},
		"max":function(array){
			return Math.max(array)
		},
		"min":function(array){
			return Math.min(array)
		},
		"count":function(array){
			return array.length
		}
	}
	
	var rules = []
    for(var rule in ruleMap){
        rules[rules.length] = {
        	id:rule,
            target:ruleMap[rule].target,
            triggers:ruleMap[rule].triggers
        }
    }
	
	//栏目联查表单
	function runJointQuery(para){
		//debugger
		// 参数有效性检查
		if (!para || !para.vm) // 参数对象
			return undefined
		
		init(para)
		
		//获取触发字段的联查集合
		if(!jointQuery[para.trigger.ds]){
			return undefined
		}
		
		var qryList = jointQuery[para.trigger.ds][para.trigger.item]
		if(!qryList){
			return undefined
		}
		var query
		var act = para.action || 'nav'; //联查类型(nav=详情联查,ref=参照过滤)
		for(var id in qryList){
			var qry = qryList[id]
			if(act != qry.actionType)
				continue;
			if(!qry.action.condition && !query){
				query = qry //使用第1个未设置条件的规则作为默认值
				continue
			}
			else if(qry.action.condition){
				//如果条件满足，则使用该联查配置
				if(hitCondition(qry.action, para.trigger.lineid)){
					query = qry
					break;
				}
			}
		}
		var result = undefined
		if(query){
			result = buildJointQuery(query, para.trigger.lineid)
		}
		return result
	}
	
	function hitCondition(query,lineid){
		if(!query.condition){
			return true;
		}
		return calc(query, query.condition, lineid)
	}
	
	function buildJointQuery(query,lineid){
		var ret = {}
		ret.target = query.action.id
		ret.paras = []
		for(var p in query.param){
			var para = query.param[p]
			var pVal = calc(para, para.expression,lineid)
			if(pVal){
				if(query.actionType == "ref"){
					ret.paras[ret.paras.length] = {
						field: para.id,
						value1: pVal,
						op: para.condition
					}
				}
				else{
					ret.paras[para.id] = pVal
				}
			}
		}
		return ret
	}
	
	var jointFields = []
	var refFields = []
	for(var ent in jointQuery){
		var ds = data[ent].field
		for(var item in jointQuery[ent]){
			var fld = {
				ds: ds,
				item: item
			}
			var joint = false
			var ref = false
			var qryList = jointQuery[ent][item]
			for(var i in qryList){
				if(qryList[i].actionType == "ref")
					ref = true
				else
					joint = true
			}
			if(joint)
				jointFields[jointFields.length] = fld
			if(ref)
				refFields[refFields.length] = fld
		}
	}
	
	function trimStr(str){
		return str.replace(" ","")
	}
	
    return {
    	jointQuery: runJointQuery,
    	jointFields: jointFields,
		refFields: refFields,
        run: runRuleItem,
        rules: rules
    }
	
})()//# sourceURL=bill-dynamic-[$billno].js
