package com.yonyou.ucf.mdf.<#=owner.moduleName#>.<#=owner.name#>;

<#?(suppliers._size>0)#>
<#*(supplier:suppliers){#>
import com.yonyou.ucf.mdf.<#=owner.moduleName#>.<#=owner.name#>.<#=name#>;
<#}#>
<#}#>
<#?(parent!=null && parent.name != BizObject){#>
import com.yonyou.ucf.mdf.<#=parent.owner.moduleName#>.<#=parent.owner.name#>.<#=parent.name#>;
<#}#>
<#?(parent==null || parent.name == BizObject){#>
import org.imeta.orm.base.BizObject;
<#}#>

/**
 * <#=title#>实体
 *
 * @author u
 * @version 1.0
 */
public class <#=name#><#=? extends %s,parent.name#><#?(parent==null)#> extends BizObject<#}#><#?(suppliers._size>0)#> implements <#*(supplier:suppliers){#> <#=name#><#?(isLast!=true)#>,<#}#><#}#><#}#> {
	// 实体全称
	public static final String ENTITY_NAME = "<#=owner.moduleName#>.<#=owner.name#>.<#=name#>";

<#?(keyAttribute!=null && keyAttribute.name != id){#>
	/**
	 * 获取<#=keyAttribute.title#>
	 *
	 * @return <#=keyAttribute.title#>
	 */
	public <#/javatype(keyAttribute)#> getId() {
		return get("<#=keyAttribute.name#>");
	}

	/**
	 * 设置<#=keyAttribute.title#>
	 *
	 * @param <#=keyAttribute.name#> <#=keyAttribute.title#>
	 */
	public void setId(<#/javatype(keyAttribute)#> <#=keyAttribute.name#>) {
		set("<#=keyAttribute.name#>", <#=keyAttribute.name#>);
	}

	/**
	 * 获取<#=keyAttribute.title#>
	 *
	 * @return <#=keyAttribute.title#>
	 */
	public <#/javatype(keyAttribute)#> get<#/capital(keyAttribute.name)#>() {
		return get("<#=keyAttribute.name#>");
	}

	/**
	 * 设置<#=keyAttribute.title#>
	 *
	 * @param <#=keyAttribute.name#> <#=keyAttribute.title#>
	 */
	public void set<#/capital(keyAttribute.name)#>(<#/javatype(keyAttribute)#> <#=keyAttribute.name#>) {
		set("<#=keyAttribute.name#>", <#=keyAttribute.name#>);
	}

<#}#>
<#*(attribute:attributes)?((isCompositionAttribute!=true || isRoleA!=true) && isKey!=true && isDerived!=true && isDependencied!=true){#>
<#?(type.isEnumeration == true){#>
    /**
     * 获取<#=title#>
     *
     * @return <#=title#>
     */
	public <#/javatype(this)#> get<#/capital(name)#>() {
		Number v = get("<#=name#>");
		return <#/javatype(this)#>.find(v);
	}

    /**
     * 设置<#=title#>
     *
     * @param <#=name#> <#=title#>
     */
	public void set<#/capital(name)#>(<#/javatype(this)#> <#=name#>) {
		if (<#=name#> != null) {
			set("<#=name#>", <#=name#>.getValue());
		} else {
			set("<#=name#>", null);
		}
	}

<#}#>
<#?(type.isEnumeration != true){#>
    /**
     * 获取<#=title#>
     *
     * @return <#=title#><#?(isAssociationAttribute==true){#>.ID<#}#>

     */
	public <#/javatype(this)#> get<#/capital(name)#>() {
	    <#?(type.name==Short){#>
	    return getShort("<#=name#>");
	    <#}#>
	    <#?(type.name==Byte){#>
	    return getByte("<#=name#>");
        <#}#>
	    <#?(type.name==Boolean){#>
	    return getBoolean("<#=name#>");
	    <#}#>
        <#?(type.name!=Short && type.name!=Byte && type.name!=Boolean){#>
		return get("<#=name#>");
        <#}#>
	}

    /**
     * 设置<#=title#>
     *
     * @param <#=name#> <#=title#><#?(isAssociationAttribute==true){#>.ID<#}#>

     */
	public void set<#/capital(name)#>(<#/javatype(this)#> <#=name#>) {
		set("<#=name#>", <#=name#>);
	}

<#}#>
<#}#>
<#*(attribute:attributes)?(isCompositionAttribute==true && isRoleA==true){#>
<#?(association.roleAMulti==1){#>
    /**
     * 获取<#=type.title#>
     *
     * @return <#=type.title#>
     */
	public <#=type.name#> <#=name#>() {
		return getBizObject("<#=name#>", <#=type.name#>.class);
	}

    /**
     * 设置<#=type.title#>
     *
     * @param <#=name#> <#=type.title#>
     */
	public void set<#/capital(name)#>(<#=type.name#> <#=name#>) {
		setBizObject("<#=name#>", <#=name#>);
	}

<#}#>
<#?(association.roleAMulti!=1){#>
    /**
     * 获取<#=type.title#>集合
     *
     * @return <#=type.title#>集合
     */
	public java.util.List<<#=type.name#>> <#=name#>() {
		return getBizObjects("<#=name#>", <#=type.name#>.class);
	}

    /**
     * 设置<#=type.title#>集合
     *
     * @param <#=name#> <#=type.title#>集合
     */
	public void set<#/capital(name)#>(java.util.List<<#=type.name#>> <#=name#>) {
		setBizObjects("<#=name#>", <#=name#>);
	}

<#}#>
<#}#>
}
