<#*(component:components){#>
####################
--	<#=title#>
####################
<#*(entity:classes)?(tableName!=null && isView!=true){#>
-- ----------------------------
-- Table structure for <#=tableName#>
-- ----------------------------
DROP TABLE IF EXISTS `<#=tableName#>`;
CREATE TABLE `<#=tableName#>` (
<#*(attribute:attributes)?((isCompositionAttribute!=true || isRoleA!=true) && (isDependencied!=true) && (mode!=None)){#>
	<#@TableColumnBuilder#><#?(isSyncKey==true){#> NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP<#}#>,
<#}#>
<#*(attribute:attributes){#>
<#?(unionKey!=null){#>
	<#@TableIndexBuilder#>,
<#}#>
<#?(unionKey==null && isRoleB==true && isKey!=true){#>
	<#@TableIndexBuilder#>,
<#}#>
<#}#>
	<#@TableIndexBuilder,keyAttribute#>

) ENGINE=InnoDB CHARSET=utf8;
<#}#>
<#}#>
