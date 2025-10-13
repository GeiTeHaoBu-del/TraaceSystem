/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 90001 (9.0.1)
 Source Host           : localhost:3306
 Source Schema         : food_traceability_platform

 Target Server Type    : MySQL
 Target Server Version : 90001 (9.0.1)
 File Encoding         : 65001

 Date: 13/10/2025 19:58:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for biz_confirmation
-- ----------------------------
DROP TABLE IF EXISTS `biz_confirmation`;
CREATE TABLE `biz_confirmation`  (
  `confirm_id` bigint NOT NULL AUTO_INCREMENT COMMENT '确认请求唯一标识',
  `initiate_enterprise_id` bigint NOT NULL COMMENT '发起请求的下游企业ID',
  `receive_enterprise_id` bigint NOT NULL COMMENT '接收请求的上游企业ID',
  `batch_id` bigint NOT NULL COMMENT '关联下游企业批号ID',
  `confirm_status` tinyint NOT NULL COMMENT '请求状态：0-待确认，1-已确认，2-已拒绝',
  `initiate_time` datetime NOT NULL COMMENT '请求发起时间',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认/拒绝时间（非“待确认”时非NULL）',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因（状态为“已拒绝”时非NULL）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`confirm_id`) USING BTREE,
  INDEX `idx_initiate_enterprise`(`initiate_enterprise_id` ASC) USING BTREE,
  INDEX `idx_receive_enterprise`(`receive_enterprise_id` ASC) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_confirm_status`(`confirm_status` ASC) USING BTREE,
  CONSTRAINT `fk_batch_confirmation` FOREIGN KEY (`batch_id`) REFERENCES `prod_batch` (`batch_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_initiate_enterprise` FOREIGN KEY (`initiate_enterprise_id`) REFERENCES `sys_enterprise` (`enterprise_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_receive_enterprise` FOREIGN KEY (`receive_enterprise_id`) REFERENCES `sys_enterprise` (`enterprise_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '确认请求表（上下游批号确认流程记录）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biz_confirmation
-- ----------------------------

-- ----------------------------
-- Table structure for prod_batch
-- ----------------------------
DROP TABLE IF EXISTS `prod_batch`;
CREATE TABLE `prod_batch`  (
  `batch_id` bigint NOT NULL AUTO_INCREMENT COMMENT '批号记录唯一标识',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品批号（如“养殖_20250924_001”，唯一）',
  `enterprise_id` bigint NOT NULL COMMENT '创建批号的企业ID',
  `upstream_batch_id` bigint NULL DEFAULT NULL COMMENT '上游批号ID（养殖企业为NULL，其他节点关联上游）',
  `product_variety` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品品种（如“猪肉”“牛肉”）',
  `cert_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对应节点证件编号：养殖-动物检疫合格证，屠宰-肉品品质检验合格证，批发/零售-NULL',
  `batch_status` tinyint NOT NULL COMMENT '批号状态：\n养殖：0-待发布，1-已发布，2-已下架\n屠宰/批发/零售：0-新建，1-待确认，2-已确认，3-已下架',
  `offline_time` datetime NULL DEFAULT NULL COMMENT '下架时间（状态为“已下架”时非NULL）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '批号创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '批号更新时间',
  PRIMARY KEY (`batch_id`) USING BTREE,
  UNIQUE INDEX `uk_batch_no`(`batch_no` ASC) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id` ASC) USING BTREE,
  INDEX `idx_upstream_batch_id`(`upstream_batch_id` ASC) USING BTREE,
  INDEX `idx_batch_status`(`batch_status` ASC) USING BTREE,
  CONSTRAINT `fk_enterprise_batch` FOREIGN KEY (`enterprise_id`) REFERENCES `sys_enterprise` (`enterprise_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_upstream_batch` FOREIGN KEY (`upstream_batch_id`) REFERENCES `prod_batch` (`batch_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '产品批号表（全流通节点批号记录）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of prod_batch
-- ----------------------------

-- ----------------------------
-- Table structure for prod_trace_code
-- ----------------------------
DROP TABLE IF EXISTS `prod_trace_code`;
CREATE TABLE `prod_trace_code`  (
  `trace_id` bigint NOT NULL AUTO_INCREMENT COMMENT '溯源码记录唯一标识',
  `trace_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '溯源码（如UUID，唯一，支持后续二维码生成）',
  `batch_id` bigint NOT NULL COMMENT '关联零售商批号ID（一对一）',
  `generate_time` datetime NOT NULL COMMENT '溯源码生成时间',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '溯源码状态：0-无效（批号下架），1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`trace_id`) USING BTREE,
  UNIQUE INDEX `uk_trace_code`(`trace_code` ASC) USING BTREE,
  INDEX `idx_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_batch_trace_code` FOREIGN KEY (`batch_id`) REFERENCES `prod_batch` (`batch_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '溯源码表（零售商批号生成的消费者溯源标识）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of prod_trace_code
-- ----------------------------

-- ----------------------------
-- Table structure for sys_cert_type_apply_enterprise
-- ----------------------------
DROP TABLE IF EXISTS `sys_cert_type_apply_enterprise`;
CREATE TABLE `sys_cert_type_apply_enterprise`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识',
  `cert_type_id` int NOT NULL COMMENT '关联证件类型ID',
  `apply_enterprise_type` tinyint NOT NULL COMMENT '适用企业类型：1-养殖，2-屠宰，3-批发，4-零售',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cert_enterprise_type`(`cert_type_id` ASC, `apply_enterprise_type` ASC) USING BTREE,
  INDEX `idx_cert_type_id`(`cert_type_id` ASC) USING BTREE,
  INDEX `idx_apply_enterprise_type`(`apply_enterprise_type` ASC) USING BTREE,
  CONSTRAINT `fk_cert_type_apply` FOREIGN KEY (`cert_type_id`) REFERENCES `sys_certificate_type` (`cert_type_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '证件类型-适用企业关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_cert_type_apply_enterprise
-- ----------------------------
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (1, 1, 1, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (2, 1, 2, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (3, 1, 3, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (4, 1, 4, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (5, 2, 1, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (6, 3, 1, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (7, 3, 2, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (8, 4, 1, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (9, 5, 2, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (10, 6, 3, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (11, 7, 3, '2025-09-25 15:23:46');
INSERT INTO `sys_cert_type_apply_enterprise` VALUES (12, 7, 4, '2025-09-25 15:23:46');

-- ----------------------------
-- Table structure for sys_certificate_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_certificate_type`;
CREATE TABLE `sys_certificate_type`  (
  `cert_type_id` int NOT NULL AUTO_INCREMENT COMMENT '证件类型唯一标识',
  `cert_type_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '证件类型名称（如“营业执照”）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '证件说明（如“所有企业必须提交”）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`cert_type_id`) USING BTREE,
  UNIQUE INDEX `uk_cert_type_name`(`cert_type_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '证件类型表（统一资质证件分类）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_certificate_type
-- ----------------------------
INSERT INTO `sys_certificate_type` VALUES (1, '营业执照', '所有流通节点企业必须提交的基础资质证件', '2025-09-25 15:23:46');
INSERT INTO `sys_certificate_type` VALUES (2, '动物防疫条件合格证', '仅养殖企业需提交的防疫资质证件', '2025-09-25 15:23:46');
INSERT INTO `sys_certificate_type` VALUES (3, '环境影响评价资质证书', '养殖企业、屠宰企业需提交的环保资质证件', '2025-09-25 15:23:46');
INSERT INTO `sys_certificate_type` VALUES (4, '动物检疫合格证', '养殖企业产品出场需提交的产品证件', '2025-09-25 15:23:46');
INSERT INTO `sys_certificate_type` VALUES (5, '肉品品质检验合格证', '屠宰企业产品出场需提交的产品证件', '2025-09-25 15:23:46');
INSERT INTO `sys_certificate_type` VALUES (6, '食品流通许可证', '仅批发企业需提交的流通资质证件', '2025-09-25 15:23:46');
INSERT INTO `sys_certificate_type` VALUES (7, '食品经营许可证', '批发企业、零售企业需提交的经营资质证件', '2025-09-25 15:23:46');

-- ----------------------------
-- Table structure for sys_enterprise
-- ----------------------------
DROP TABLE IF EXISTS `sys_enterprise`;
CREATE TABLE `sys_enterprise`  (
  `enterprise_id` bigint NOT NULL AUTO_INCREMENT COMMENT '企业唯一标识',
  `enterprise_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '企业名称（唯一）',
  `enterprise_type` tinyint NOT NULL COMMENT '企业类型：1-养殖，2-屠宰，3-批发，4-零售',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '企业所在省份（支撑省统计）',
  `register_time` date NOT NULL COMMENT '企业注册时间（支撑月趋势统计）',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '企业联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '企业详细地址',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`enterprise_id`) USING BTREE,
  UNIQUE INDEX `uk_enterprise_name`(`enterprise_name` ASC) USING BTREE,
  INDEX `idx_enterprise_type`(`enterprise_type` ASC) USING BTREE,
  INDEX `idx_province`(`province` ASC) USING BTREE,
  INDEX `idx_register_time`(`register_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '企业表（养殖/屠宰/批发/零售节点）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_enterprise
-- ----------------------------

-- ----------------------------
-- Table structure for sys_enterprise_certificate
-- ----------------------------
DROP TABLE IF EXISTS `sys_enterprise_certificate`;
CREATE TABLE `sys_enterprise_certificate`  (
  `cert_id` bigint NOT NULL AUTO_INCREMENT COMMENT '证件记录唯一标识',
  `enterprise_id` bigint NOT NULL COMMENT '关联企业ID',
  `cert_type_id` int NOT NULL COMMENT '关联证件类型ID',
  `cert_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '证件编号（如营业执照注册号，唯一）',
  `valid_until` date NOT NULL COMMENT '证件有效期（校验合规性）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '证件状态：0-过期/无效，1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`cert_id`) USING BTREE,
  UNIQUE INDEX `uk_cert_no`(`cert_no` ASC) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id` ASC) USING BTREE,
  INDEX `idx_cert_type_id`(`cert_type_id` ASC) USING BTREE,
  CONSTRAINT `fk_cert_type_cert` FOREIGN KEY (`cert_type_id`) REFERENCES `sys_certificate_type` (`cert_type_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_enterprise_cert` FOREIGN KEY (`enterprise_id`) REFERENCES `sys_enterprise` (`enterprise_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '企业资质证件表（存储企业合规证件信息）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_enterprise_certificate
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `user_type` tinyint NOT NULL COMMENT '用户类型：0-系统管理员，1-养殖企业，2-屠宰企业，3-批发企业，4-零售企业',
  `login_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录编码（唯一，如“admin”或“企业编号_角色”）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（BCrypt加密存储）',
  `enterprise_id` bigint NULL DEFAULT NULL COMMENT '关联企业ID（系统管理员为NULL）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_login_code`(`login_code` ASC) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表（系统管理员+企业用户）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 0, 'admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', NULL, 1, '2025-09-25 15:23:46', NULL);

SET FOREIGN_KEY_CHECKS = 1;
