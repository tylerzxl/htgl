
CREATE TABLE `billruleregister`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `billnum` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `action` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ruleId` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `iorder` float(10, 2) NULL DEFAULT NULL,
  `overrule` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `tenant_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `key` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `isSystem` bit(1) NULL DEFAULT NULL,
  `url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `isSync` tinyint(1) NOT NULL DEFAULT 0,
  `isAsyn` tinyint(1) NOT NULL DEFAULT 0,
  `config` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `domain` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_billruleregister_tenantid`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1397063 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for rulelog
-- ----------------------------
CREATE TABLE `rulelog`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ruleid` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `billnum` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ruleaction` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `iorder` float(10, 2) NULL DEFAULT NULL,
  `tenant_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pubts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
  `issuccess` int(5) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;