INSERT INTO `billruleregister`( `billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`, `isSync`, `isAsyn`, `config`, `domain`, `dataRule`, `mock`) VALUES ( 'common', 'add', 'mobileMddAddRule', 32.00, NULL, '0', NULL, b'1', NULL, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `billruleregister`( `billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`, `isSync`, `isAsyn`, `config`, `domain`, `dataRule`, `mock`) VALUES ( 'common', 'save', 'mobileMddUpdateBillCodeRule', 29.00, NULL, '0', NULL, b'1', NULL, 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `billruleregister`( `billnum`, `action`, `ruleId`, `iorder`, `overrule`, `tenant_id`, `key`, `isSystem`, `url`, `isSync`, `isAsyn`, `config`, `domain`, `dataRule`, `mock`) VALUES ( 'common', 'delete', 'mobileMddReturnBillCodeRule', 41.00, NULL, '0', NULL, b'1', NULL, 0, 0, NULL, NULL, NULL, NULL);


DELETE FROM `billruleregister` WHERE `ruleId` = 'mddUpdateBillCodeRule' AND `action` = 'save' LIMIT 10000;
