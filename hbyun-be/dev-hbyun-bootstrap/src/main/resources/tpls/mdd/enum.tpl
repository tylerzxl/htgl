package com.yonyou.ucf.mdf.<#=owner.moduleName#>.<#=owner.name#>;

import java.util.HashMap;

/**
 * <#=title#>枚举
 *
 * @author u
 * @version 1.0
 */
public enum <#=name#> {
<#*(item:items){#>
	<#=name#>("<#=title#>", (short)<#=value#>)<#?(isLast==false){#>,<#}#><#?(isLast==true){#>;<#}#>

<#}#>

	private String name;
	private short value;

	private <#=name#>(String name, short value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public short getValue() {
		return value;
	}

	private static HashMap<Short, <#=name#>> map = null;

	private synchronized static void initMap() {
		if (map != null) {
			return;
		}
		map = new HashMap<Short, <#=name#>>();
		<#=name#>[] items = <#=name#>.values();
		for (<#=name#> item : items) {
			map.put(item.getValue(), item);
		}
	}

	public static <#=name#> find(Number value) {
		if (value == null) {
			return null;
		}
		if (map == null) {
			initMap();
		}
		return map.get(value.shortValue());
	}
}
