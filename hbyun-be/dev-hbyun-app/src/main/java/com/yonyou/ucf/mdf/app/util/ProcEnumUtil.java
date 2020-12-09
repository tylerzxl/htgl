package com.yonyou.ucf.mdf.app.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Administrator
 * 2020/4/8 10:03
 */
public class ProcEnumUtil {

    /**
     * 根据cenumString 字段中的key 找到对应 value 显示值
     * [{"nameType":"text","value":"水果","key":"s001"}]
     *
     * @param enumMap
     * @param entry
     * @return
     */
    public static Object procEnum(Map<String, String> enumMap, Map.Entry<String, Object> entry) {
    	if (null == entry.getValue()) {
			return "";
		}
    	return procEnum(enumMap, entry.getKey(), entry.getValue().toString());
    }

    public static Object procEnum(Map<String, String> enumMap, String key, String value) {
        if (enumMap.containsKey(key)) {
            String enumString = enumMap.get(key);
            if (StringUtils.isEmpty(enumString) || StringUtils.isEmpty(value)) {
                return value;
            }
            Object object = JSON.parse(enumString);
            if (object instanceof JSONArray) {
            	JSONArray enumJson = JSONArray.parseArray(enumString);
            	for (int i = 0; i < enumJson.size(); i++) {
            		JSONObject jsonObject1 = enumJson.getJSONObject(i);
            		String key1 = jsonObject1.getString("key");
            		if (key1.equals(value)) {
            			return jsonObject1.getString("value");
            		}
            	}
			} else if (object instanceof JSONObject) {
            	JSONObject jsonObject = (JSONObject) object;
        		return jsonObject.getString(value);
			}
            
        } else {
            return value;
        }
        return "";
    }

}
