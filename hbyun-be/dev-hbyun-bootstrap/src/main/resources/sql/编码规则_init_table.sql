--
-- Create table `aa_billreturn`
--
CREATE TABLE aa_billreturn (
  autoid INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT(20) DEFAULT 0 COMMENT '租户',
  orgId INT(11) NOT NULL DEFAULT -1 COMMENT '组织',
  cbillnum VARCHAR(50) DEFAULT NULL COMMENT '表单编码',
  ownerorg BIGINT(20) DEFAULT NULL COMMENT '所属组织',
  cglide VARCHAR(250) DEFAULT NULL COMMENT '前缀名称',
  cgliderule VARCHAR(250) DEFAULT NULL COMMENT '取值规则',
  cseed VARCHAR(250) NOT NULL COMMENT '编号前缀',
  inumber INT(11) NOT NULL COMMENT '流水号',
  totalBasis VARCHAR(500) NOT NULL COMMENT '总依据(依据1|依据2|依据3|...)',
  billnumberid INT(11) DEFAULT NULL COMMENT '规则id（外键）',
  dr TINYINT(1) DEFAULT NULL COMMENT '删除标记',
  pubts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
  yhtTenantId VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (autoid)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '退号表',
ROW_FORMAT = COMPACT;

--
-- Create table `aa_billprefix`
--
CREATE TABLE aa_billprefix (
  autoid INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT(20) DEFAULT 0 COMMENT '租户',
  orgId INT(11) NOT NULL DEFAULT -1 COMMENT '组织',
  cprefix VARCHAR(50) NOT NULL COMMENT '编码段名称：如门店代码，手工输入等',
  iprefixlen INT(11) NOT NULL COMMENT '长度',
  cprefixrule VARCHAR(50) DEFAULT NULL COMMENT '取值规则(例如yyyymmdd)',
  cprefixseed VARCHAR(50) DEFAULT NULL COMMENT '依据参数：如y，ym，ymd',
  iorder INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
  bfix TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否依据',
  cprefixid VARCHAR(50) DEFAULT NULL COMMENT '关联前缀预设的id',
  cprefixtype INT(11) NOT NULL DEFAULT 1 COMMENT '前缀类型 0(字符字段)、1（日期字段）、2（常量）、3(系统时间)，4（单据参照属性），5 随机码',
  cprefixsep VARCHAR(10) DEFAULT NULL COMMENT '分隔符',
  cfieldname VARCHAR(50) DEFAULT NULL COMMENT '取值字段',
  csourcename VARCHAR(50) DEFAULT NULL,
  ipurpose TINYINT(4) DEFAULT NULL,
  fillstyle TINYINT(4) DEFAULT NULL COMMENT '补位方式（0-不补位，1-左补位，2-右补位）',
  fillsign VARCHAR(4) DEFAULT NULL COMMENT '补位符（最长4位）  ',
  billnumberid INT(11) DEFAULT NULL COMMENT '规则id',
  dr TINYINT(1) DEFAULT NULL COMMENT '删除标记',
  pubts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
  cbillnum VARCHAR(50) DEFAULT NULL,
  yhtTenantId VARCHAR(20) DEFAULT NULL,
  formula VARCHAR(500) DEFAULT NULL COMMENT '公式',
  formuladisplay VARCHAR(500) DEFAULT NULL COMMENT '公式显示',
  PRIMARY KEY (autoid)
)
ENGINE = INNODB,
AUTO_INCREMENT = 24663,
AVG_ROW_LENGTH = 334,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '编码规则子表',
ROW_FORMAT = COMPACT;

--
-- Create index `ix_aa_billprefix_cbillnum_cprefix` on table `aa_billprefix`
--
ALTER TABLE aa_billprefix 
  ADD INDEX ix_aa_billprefix_cbillnum_cprefix(cbillnum, cprefix, orgId);

--
-- Create table `aa_billprefabricate`
--
CREATE TABLE aa_billprefabricate (
  autoid INT(11) NOT NULL AUTO_INCREMENT,
  cbillnum VARCHAR(50) NOT NULL COMMENT '表单编码',
  cprefix VARCHAR(50) NOT NULL COMMENT '前缀名字',
  cprefixid VARCHAR(50) NOT NULL COMMENT '唯一uid',
  cprefixtype INT(11) NOT NULL COMMENT '前缀类型 0=字符串 1=date 2=文本',
  iprefixtype TINYINT(4) DEFAULT NULL,
  ipurpose TINYINT(4) DEFAULT NULL,
  csourcename VARCHAR(50) DEFAULT NULL,
  cfieldname VARCHAR(50) DEFAULT NULL COMMENT '取值字段',
  carchname VARCHAR(50) DEFAULT NULL,
  carchfieldname VARCHAR(50) DEFAULT NULL,
  carchclsfieldname VARCHAR(50) DEFAULT NULL,
  ckeyword VARCHAR(50) DEFAULT NULL,
  ckeywordnamefield VARCHAR(50) DEFAULT NULL,
  tenant_id BIGINT(20) DEFAULT NULL,
  yhtTenantId VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (autoid)
)
ENGINE = INNODB,
AUTO_INCREMENT = 17667,
AVG_ROW_LENGTH = 4096,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '预置表单',
ROW_FORMAT = COMPACT;

--
-- Create index `ix_aa_billprefabricate_cbillnum_cprefix` on table `aa_billprefabricate`
--
ALTER TABLE aa_billprefabricate 
  ADD INDEX ix_aa_billprefabricate_cbillnum_cprefix(cbillnum, cprefix);

--
-- Create table `aa_billprecode`
--
CREATE TABLE aa_billprecode (
  autoid INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT(20) DEFAULT 0 COMMENT '租户',
  orgId INT(11) NOT NULL DEFAULT -1 COMMENT '组织',
  cbillnum VARCHAR(50) DEFAULT NULL COMMENT '表单编码',
  ownerorg BIGINT(20) DEFAULT NULL COMMENT '所属组织',
  cglide VARCHAR(250) DEFAULT NULL COMMENT '前缀名称',
  cgliderule VARCHAR(250) DEFAULT NULL COMMENT '取值规则',
  cseed VARCHAR(250) NOT NULL COMMENT '编号前缀',
  inumber INT(11) NOT NULL COMMENT '流水号',
  totalBasis VARCHAR(500) NOT NULL COMMENT '总依据(依据1|依据2|依据3|...)',
  billnumberid INT(11) DEFAULT NULL COMMENT '规则id（外键）',
  billcode VARCHAR(250) DEFAULT NULL COMMENT '编码号',
  dr TINYINT(1) DEFAULT NULL COMMENT '删除标记',
  pubts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
  yhtTenantId VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (autoid)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '预取表',
ROW_FORMAT = COMPACT;

--
-- Create table `aa_billnumber`
--
CREATE TABLE aa_billnumber (
  autoid INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT(20) DEFAULT 0 COMMENT '租户',
  orgId INT(11) NOT NULL DEFAULT -1 COMMENT '组织',
  cbillnum VARCHAR(50) NOT NULL COMMENT '作为规则编码，表单模式下是表单编码，元数据模式下是元数据ID，自定义实体模式下是自定义实体主键ID',
  cbillname VARCHAR(50) NOT NULL COMMENT '表单名称',
  csubid VARCHAR(20) DEFAULT NULL,
  ballowhandwork TINYINT(4) NOT NULL COMMENT '允许手动编码',
  brepeatredo TINYINT(4) NOT NULL COMMENT '允许手工可改',
  istartnumber INT(11) NOT NULL COMMENT '流水号初始值',
  iseriallen TINYINT(4) NOT NULL COMMENT '流水号长度',
  billnumLen INT(11) NOT NULL DEFAULT 8 COMMENT '流水号长度',
  billnumInit INT(11) NOT NULL DEFAULT 1 COMMENT '流水号初始值',
  billnumTruncatType INT(11) NOT NULL DEFAULT 0 COMMENT '截断类型0 = 左截断 1 = 右截断',
  billnumFillType INT(11) NOT NULL DEFAULT 0 COMMENT '补位类型0=不补位 1=左补位 2=右补位',
  billnumFillMark VARCHAR(20) NOT NULL DEFAULT '0' COMMENT '补位符',
  billnumMode INT(11) NOT NULL DEFAULT 0 COMMENT '0=手工编号 1=自动编号 2=自动编号 手工可改',
  billnumRule INT(11) NOT NULL DEFAULT 0 COMMENT '使用规则 0 企业默认 1 自定义规则',
  isReuse TINYINT(1) DEFAULT 0 COMMENT '是否开启退号补号',
  sysid VARCHAR(40) NOT NULL COMMENT '系统ID',
  datatype TINYINT(4) NOT NULL DEFAULT 2 COMMENT '编码实体类型，1：表单，2：元数据，3：自定义实体',
  rulecode VARCHAR(100) NOT NULL COMMENT '规则编码',
  rulename VARCHAR(255) DEFAULT NULL COMMENT '规则名称',
  dr TINYINT(4) NOT NULL DEFAULT 0 COMMENT '删除标记',
  pubts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
  yhtTenantId VARCHAR(20) DEFAULT NULL,
  sntype TINYINT(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (autoid)
)
ENGINE = INNODB,
AUTO_INCREMENT = 47,
AVG_ROW_LENGTH = 496,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '编码规则-主表',
ROW_FORMAT = COMPACT;

--
-- Create index `ix_aa_billnumber_cbillnum` on table `aa_billnumber`
--
ALTER TABLE aa_billnumber 
  ADD INDEX ix_aa_billnumber_cbillnum(cbillnum);

--
-- Create index `ix_aa_billnumber_cbillnum_orgid` on table `aa_billnumber`
--
ALTER TABLE aa_billnumber 
  ADD UNIQUE INDEX ix_aa_billnumber_cbillnum_orgid(cbillnum, orgId, tenant_id);

--
-- Create index `ix_aa_billnumber_rulecode` on table `aa_billnumber`
--
ALTER TABLE aa_billnumber 
  ADD INDEX ix_aa_billnumber_rulecode(rulecode);

--
-- Create table `aa_billhistory`
--
CREATE TABLE aa_billhistory (
  autoid INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  tenant_id BIGINT(20) DEFAULT 0 COMMENT '租户',
  orgId INT(11) NOT NULL DEFAULT -1 COMMENT '组织',
  cbillnum VARCHAR(50) NOT NULL COMMENT '表单编码',
  ownerorg BIGINT(20) DEFAULT NULL COMMENT '所属组织',
  cglide VARCHAR(100) DEFAULT NULL COMMENT '前缀名称',
  cgliderule VARCHAR(100) DEFAULT NULL COMMENT '取值规则',
  cseed VARCHAR(100) NOT NULL COMMENT '编号前缀',
  inumber INT(11) NOT NULL COMMENT '流水号',
  totalBasis VARCHAR(100) NOT NULL COMMENT '总依据(依据1|依据2|依据3|...)',
  billnumberid int(11) DEFAULT NULL COMMENT '规则id（外键）',
  dr TINYINT(1) DEFAULT NULL COMMENT '删除标记',
  pubts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间戳',
  yhtTenantId VARCHAR(20) DEFAULT NULL COMMENT '友户通租户Id',
  PRIMARY KEY (autoid)
)
ENGINE = INNODB,
AUTO_INCREMENT = 5580,
AVG_ROW_LENGTH = 606,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '流水号',
ROW_FORMAT = COMPACT;

--
-- Create index `cbillnum` on table `aa_billhistory`
--
ALTER TABLE aa_billhistory 
  ADD INDEX cbillnum(tenant_id, cbillnum, cseed);

--
-- Create index `ix_aa_billhistory_cbillnum_cglide` on table `aa_billhistory`
--
ALTER TABLE aa_billhistory 
  ADD INDEX ix_aa_billhistory_cbillnum_cglide(cbillnum, cglide);

--
-- Create table `aa_billcode_obj`
--
CREATE TABLE aa_billcode_obj (
  pk_billobj VARCHAR(40) NOT NULL,
  code VARCHAR(40) DEFAULT NULL,
  name VARCHAR(40) DEFAULT NULL,
  name2 VARCHAR(40) DEFAULT NULL,
  name3 VARCHAR(40) DEFAULT NULL,
  name4 VARCHAR(40) DEFAULT NULL,
  name5 VARCHAR(40) DEFAULT NULL,
  name6 VARCHAR(40) DEFAULT NULL,
  name_ext VARCHAR(40) DEFAULT NULL,
  createdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  tenantid VARCHAR(64) DEFAULT NULL COMMENT '租户id，当租户值为''system''时，表示该实体为系统级',
  sysid VARCHAR(64) DEFAULT NULL,
  classify_code VARCHAR(64) DEFAULT NULL COMMENT '该编码实体所属的二级分类树code',
  service_code VARCHAR(64) DEFAULT NULL COMMENT '关联diwork的原子服务code',
  PRIMARY KEY (pk_billobj)
)
ENGINE = INNODB,
AVG_ROW_LENGTH = 103,
CHARACTER SET utf8,
COLLATE utf8_general_ci,
ROW_FORMAT = COMPACT;

--
-- Create table `aa_billcode_mappingchild`
--
CREATE TABLE aa_billcode_mappingchild (
  autoid INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  pk_map_id INT(11) DEFAULT NULL COMMENT '映射主表主键值',
  pk_ref VARCHAR(200) DEFAULT NULL COMMENT '属性值（参照属性主键值）',
  map_val VARCHAR(200) DEFAULT NULL COMMENT '编码',
  ref_code VARCHAR(255) DEFAULT NULL,
  ref_name VARCHAR(255) DEFAULT NULL,
  pubts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  ref_code_display VARCHAR(200) DEFAULT NULL,
  tenantid VARCHAR(200) DEFAULT NULL,
  sysid VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (autoid)
)
ENGINE = INNODB,
AUTO_INCREMENT = 132,
AVG_ROW_LENGTH = 252,
CHARACTER SET utf8,
COLLATE utf8_general_ci,
ROW_FORMAT = COMPACT;

--
-- Create table `aa_billcode_mapping`
--
CREATE TABLE aa_billcode_mapping (
  autoid INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  name VARCHAR(200) DEFAULT NULL COMMENT '名称',
  domain VARCHAR(200) NOT NULL COMMENT '应用编码',
  ref_code VARCHAR(200) NOT NULL COMMENT '参照编码',
  md_en_id VARCHAR(200) NOT NULL COMMENT '元数据实体ID',
  map_length INT(11) NOT NULL COMMENT '映射值长度',
  fill_style TINYINT(1) NOT NULL DEFAULT 0 COMMENT '补位方式，0不补位 1左补位 2右补位',
  fill_char VARCHAR(1) DEFAULT NULL COMMENT '补位符号',
  map_prop_type TINYINT(1) NOT NULL DEFAULT 0 COMMENT '映射的值是mappingchild中的pk，code还是name，pk为0 code为1  name为2',
  pubts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  tenantid VARCHAR(255) NOT NULL,
  sysid VARCHAR(255) DEFAULT NULL,
  default_map_value VARCHAR(255) DEFAULT NULL COMMENT '默认映射值',
  label VARCHAR(255) DEFAULT NULL COMMENT '标签 (为了处理组织类的元数据通过标签来匹配)',
  PRIMARY KEY (autoid)
)
ENGINE = INNODB,
AUTO_INCREMENT = 20,
AVG_ROW_LENGTH = 1820,
CHARACTER SET utf8,
COLLATE utf8_general_ci,
COMMENT = '编码映射主表';

--
-- Create table `aa_billcode_candidateprop`
--
CREATE TABLE aa_billcode_candidateprop (
  autoid INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  pk_bcr_obj VARCHAR(200) DEFAULT NULL COMMENT '编码对象主键',
  en_prop_name VARCHAR(200) DEFAULT NULL COMMENT '实体属性名',
  display_name VARCHAR(200) DEFAULT NULL COMMENT '显示名称',
  elem_type VARCHAR(200) DEFAULT NULL COMMENT '类型（时间，字符，参照）',
  mapping_entity VARCHAR(200) DEFAULT NULL COMMENT '所属编码映射实体',
  pubts TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (autoid)
)
ENGINE = INNODB,
AVG_ROW_LENGTH = 1260,
CHARACTER SET utf8,
COLLATE utf8_general_ci,
COMMENT = '候选属性表',
ROW_FORMAT = COMPACT;
