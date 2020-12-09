package com.yonyou.ucf.mdf.<#=owner.moduleName#>.<#=owner.name#>;

<#?(suppliers._size>0)#>
<#*(supplier:suppliers){#>
import com.yonyou.ucf.mdf.<#=owner.moduleName#>.<#=owner.name#>.<#=name#>;
<#}#>
<#}#>
<#?(parent!=null && parent.name != BizObject){#>
import com.yonyou.ucf.mdf.<#=parent.owner.moduleName#>.<#=parent.owner.name#>.<#=parent.name#>;
<#}#>

/**
 * <#=title#>接口
 * 
 * @author u
 * @version 1.0
 */
public interface <#=name#><#?(suppliers._size>0)#> extends <#*(supplier:suppliers){#> <#=name#><#?(isLast!=true)#>,<#}#><#}#><#=?, %s,parent.name#><#}#><#?(suppliers._size<1)#><#=? extends %s,parent.name#><#}#> {
<#*(attribute:attributes)?(isDerived!=true && isImplemented!=true){#>
	/**
	 * 获取<#=title#>
	 * 
	 * @return <#=title#><#?(isAssociationAttribute==true){#>.ID<#}#>

	 */
	public <#/javatype(this)#> get<#/capital(name)#>();

	/**
	 * 设置<#=title#>
	 * 
	 * @param <#=name#> <#=title#><#?(isAssociationAttribute==true){#>.ID<#}#>

	 */
	public void set<#/capital(name)#>(<#/javatype(this)#> <#=name#>);

<#?(isAssociationAttribute==true){#>
	/**
	 * 获取<#=title#>
	 * 
	 * @return <#=title#>
	 */
	public <#=type.name#> get<#/capital(name)#>Object() throws Exception;

<#}#>
<#}#>
}
