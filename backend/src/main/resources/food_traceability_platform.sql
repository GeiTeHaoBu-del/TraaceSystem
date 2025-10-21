-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: 2025-10-21 05:04:04
-- 服务器版本： 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `food_traceability_platform`
--

-- --------------------------------------------------------

--
-- 表的结构 `biz_confirmation`
--
create database food_traceability_platform;
use food_traceability_platform;
CREATE TABLE `biz_confirmation` (
  `confirm_id` bigint(20) NOT NULL COMMENT '确认请求唯一标识',
  `initiate_enterprise_id` bigint(20) NOT NULL COMMENT '发起请求的下游企业ID',
  `receive_enterprise_id` bigint(20) NOT NULL COMMENT '接收请求的上游企业ID',
  `batch_id` bigint(20) NOT NULL COMMENT '关联下游企业批号ID',
  `confirm_status` tinyint(4) NOT NULL COMMENT '请求状态：0-待确认，1-已确认，2-已拒绝',
  `initiate_time` datetime NOT NULL COMMENT '请求发起时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认/拒绝时间',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='确认请求表' ROW_FORMAT=DYNAMIC;

--
-- 转存表中的数据 `biz_confirmation`
--

INSERT INTO `biz_confirmation` (`confirm_id`, `initiate_enterprise_id`, `receive_enterprise_id`, `batch_id`, `confirm_status`, `initiate_time`, `confirm_time`, `reject_reason`, `create_time`, `update_time`) VALUES
(1, 3, 1, 3, 1, '2025-10-11 09:00:00', '2025-10-11 09:30:00', NULL, '2025-10-14 20:45:18', NULL),
(2, 4, 2, 4, 1, '2025-10-11 10:00:00', '2025-10-11 10:20:00', NULL, '2025-10-14 20:45:18', NULL),
(3, 5, 3, 5, 1, '2025-10-12 08:00:00', '2025-10-12 08:15:00', NULL, '2025-10-14 20:45:18', NULL),
(4, 6, 4, 6, 1, '2025-10-12 08:30:00', '2025-10-12 08:40:00', NULL, '2025-10-14 20:45:18', NULL),
(5, 7, 3, 7, 1, '2025-10-12 09:00:00', '2025-10-12 09:10:00', NULL, '2025-10-14 20:45:18', NULL),
(6, 8, 5, 8, 1, '2025-10-13 07:00:00', '2025-10-13 07:05:00', NULL, '2025-10-14 20:45:18', NULL),
(7, 9, 6, 9, 1, '2025-10-13 07:30:00', '2025-10-13 07:35:00', NULL, '2025-10-14 20:45:18', NULL),
(8, 10, 7, 10, 1, '2025-10-13 08:00:00', '2025-10-13 08:08:00', NULL, '2025-10-14 20:45:18', NULL),
(9, 3, 1, 3, 0, '2025-10-13 10:00:00', NULL, NULL, '2025-10-14 20:45:18', NULL),
(10, 5, 3, 5, 2, '2025-10-13 11:00:00', '2025-10-13 11:05:00', '批号信息有误，重新提交', '2025-10-14 20:45:18', NULL);

-- --------------------------------------------------------

--
-- 表的结构 `prod_batch`
--

CREATE TABLE `prod_batch` (
  `batch_id` bigint(20) NOT NULL COMMENT '批号唯一标识',
  `batch_no` varchar(50) NOT NULL COMMENT '产品批号（唯一）',
  `enterprise_id` bigint(20) NOT NULL COMMENT '创建批号的企业ID',
  `upstream_batch_id` bigint(20) DEFAULT NULL COMMENT '上游批号ID（养殖企业为NULL）',
  `downstream_enterprise_id` bigint(20) DEFAULT NULL COMMENT '指定下游企业ID（可选）',
  `product_variety` varchar(50) NOT NULL COMMENT '产品品种',
  `cert_no` varchar(100) DEFAULT NULL COMMENT '证件编号',
  `batch_status` tinyint(4) NOT NULL COMMENT '批号状态',
  `offline_time` datetime DEFAULT NULL COMMENT '下架时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品批号表' ROW_FORMAT=DYNAMIC;

--
-- 转存表中的数据 `prod_batch`
--

INSERT INTO `prod_batch` (`batch_id`, `batch_no`, `enterprise_id`, `upstream_batch_id`, `downstream_enterprise_id`, `product_variety`, `cert_no`, `batch_status`, `offline_time`, `create_time`, `update_time`) VALUES
(1, '养殖_20251010_001', 1, NULL, 3, '猪肉', '检疫京字20251010001', 1, NULL, '2025-10-14 20:45:17', NULL),
(2, '养殖_20251010_002', 2, NULL, 4, '牛肉', '检疫鲁字20251010001', 1, NULL, '2025-10-14 20:45:17', NULL),
(3, '屠宰_20251011_001', 3, 1, 5, '猪肉', '肉品沪字20251011001', 2, NULL, '2025-10-14 20:45:17', NULL),
(4, '屠宰_20251011_002', 4, 2, 6, '牛肉', '肉品豫字20251011001', 2, NULL, '2025-10-14 20:45:17', NULL),
(5, '批发_20251012_001', 5, 3, 8, '猪肉', NULL, 2, NULL, '2025-10-14 20:45:17', NULL),
(6, '批发_20251012_002', 6, 4, 9, '牛肉', NULL, 2, NULL, '2025-10-14 20:45:17', NULL),
(7, '批发_20251012_003', 7, 3, 10, '猪肉', NULL, 2, NULL, '2025-10-14 20:45:17', NULL),
(8, '零售_20251013_001', 8, 5, NULL, '猪肉', NULL, 2, NULL, '2025-10-14 20:45:17', NULL),
(9, '零售_20251013_002', 9, 6, NULL, '牛肉', NULL, 2, NULL, '2025-10-14 20:45:17', NULL),
(10, '零售_20251013_003', 10, 7, NULL, '猪肉', NULL, 2, NULL, '2025-10-14 20:45:17', NULL),
(13, '养殖_20251021_001', 1, NULL, 3, '测试品种', '饿啊0001', 1, NULL, '2025-10-21 11:09:45', '2025-10-21 11:09:45'),
(15, '屠宰_20251021_001', 3, 13, 5, '测试品种', '饿啊0002', 2, NULL, '2025-10-21 11:27:54', '2025-10-21 11:27:54'),
(18, '批发_20251021_001', 5, 15, 8, '测试品种', '饿啊0003', 2, NULL, '2025-10-21 11:42:26', '2025-10-21 11:42:26'),
(20, '零售_20251021_001', 8, 18, NULL, '测试品种', '饿啊0004', 2, NULL, '2025-10-21 12:00:30', '2025-10-21 12:00:30'),
(21, '养殖_20251021_002', 1, NULL, 3, '测试2', '养殖01', 1, NULL, '2025-10-21 12:09:59', '2025-10-21 12:09:59');

-- --------------------------------------------------------

--
-- 表的结构 `prod_trace_code`
--

CREATE TABLE `prod_trace_code` (
  `trace_id` bigint(20) NOT NULL COMMENT '溯源码记录唯一标识',
  `trace_code` varchar(64) NOT NULL COMMENT '溯源码（唯一）',
  `batch_id` bigint(20) NOT NULL COMMENT '关联零售商批号ID',
  `generate_time` datetime NOT NULL COMMENT '溯源码生成时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-无效，1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源码表' ROW_FORMAT=DYNAMIC;

--
-- 转存表中的数据 `prod_trace_code`
--

INSERT INTO `prod_trace_code` (`trace_id`, `trace_code`, `batch_id`, `generate_time`, `status`, `create_time`, `update_time`) VALUES
(1, '550e8400-e29b-41d4-a716-446655440001', 8, '2025-10-13 09:00:00', 1, '2025-10-14 20:45:18', NULL),
(2, '550e8400-e29b-41d4-a716-446655440002', 9, '2025-10-13 09:10:00', 1, '2025-10-14 20:45:18', NULL),
(3, '550e8400-e29b-41d4-a716-446655440003', 10, '2025-10-13 09:20:00', 1, '2025-10-14 20:45:18', NULL),
(4, '550e8400-e29b-41d4-a716-446655440004', 8, '2025-10-13 09:30:00', 1, '2025-10-14 20:45:18', NULL),
(5, '550e8400-e29b-41d4-a716-446655440005', 9, '2025-10-13 09:40:00', 1, '2025-10-14 20:45:18', NULL),
(6, '550e8400-e29b-41d4-a716-446655440006', 10, '2025-10-13 09:50:00', 1, '2025-10-14 20:45:18', NULL),
(7, '550e8400-e29b-41d4-a716-446655440007', 8, '2025-10-13 10:00:00', 1, '2025-10-14 20:45:18', NULL),
(8, '550e8400-e29b-41d4-a716-446655440008', 9, '2025-10-13 10:10:00', 1, '2025-10-14 20:45:18', NULL),
(9, '550e8400-e29b-41d4-a716-446655440009', 10, '2025-10-13 10:20:00', 1, '2025-10-14 20:45:18', NULL),
(10, '550e8400-e29b-41d4-a716-446655440010', 8, '2025-10-13 10:30:00', 1, '2025-10-14 20:45:18', NULL),
(13, '4b2aca6d6b5b4c23b9efe90e91483532', 20, '2025-10-21 12:00:43', 1, '2025-10-21 12:00:43', '2025-10-21 12:00:43');

-- --------------------------------------------------------

--
-- 表的结构 `sys_certificate_type`
--

CREATE TABLE `sys_certificate_type` (
  `cert_type_id` int(11) NOT NULL COMMENT '证件类型唯一标识',
  `cert_type_name` varchar(100) NOT NULL COMMENT '证件类型名称',
  `description` varchar(255) DEFAULT NULL COMMENT '证件说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证件类型表' ROW_FORMAT=DYNAMIC;

--
-- 转存表中的数据 `sys_certificate_type`
--

INSERT INTO `sys_certificate_type` (`cert_type_id`, `cert_type_name`, `description`, `create_time`) VALUES
(1, '营业执照', '所有企业必须提交的基础资质', '2025-09-25 15:23:46'),
(2, '动物防疫条件合格证', '仅养殖企业需提交', '2025-09-25 15:23:46'),
(3, '环境影响评价资质证书', '养殖、屠宰企业需提交', '2025-09-25 15:23:46'),
(4, '动物检疫合格证', '养殖企业产品出场证件', '2025-09-25 15:23:46'),
(5, '肉品品质检验合格证', '屠宰企业产品出场证件', '2025-09-25 15:23:46'),
(6, '食品流通许可证', '仅批发企业需提交', '2025-09-25 15:23:46'),
(7, '食品经营许可证', '批发、零售企业需提交', '2025-09-25 15:23:46');

-- --------------------------------------------------------

--
-- 表的结构 `sys_cert_type_apply_enterprise`
--

CREATE TABLE `sys_cert_type_apply_enterprise` (
  `id` bigint(20) NOT NULL COMMENT '关联记录唯一标识',
  `cert_type_id` int(11) NOT NULL COMMENT '关联证件类型ID',
  `apply_enterprise_type` tinyint(4) NOT NULL COMMENT '适用企业类型：1-养殖，2-屠宰，3-批发，4-零售',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证件类型-适用企业关联表' ROW_FORMAT=DYNAMIC;

--
-- 转存表中的数据 `sys_cert_type_apply_enterprise`
--

INSERT INTO `sys_cert_type_apply_enterprise` (`id`, `cert_type_id`, `apply_enterprise_type`, `create_time`) VALUES
(1, 1, 1, '2025-09-25 15:23:46'),
(2, 1, 2, '2025-09-25 15:23:46'),
(3, 1, 3, '2025-09-25 15:23:46'),
(4, 1, 4, '2025-09-25 15:23:46'),
(5, 2, 1, '2025-09-25 15:23:46'),
(6, 3, 1, '2025-09-25 15:23:46'),
(7, 3, 2, '2025-09-25 15:23:46'),
(8, 4, 1, '2025-09-25 15:23:46'),
(9, 5, 2, '2025-09-25 15:23:46'),
(10, 6, 3, '2025-09-25 15:23:46'),
(11, 7, 3, '2025-09-25 15:23:46'),
(12, 7, 4, '2025-09-25 15:23:46');

-- --------------------------------------------------------

--
-- 表的结构 `sys_enterprise`
--

CREATE TABLE `sys_enterprise` (
  `enterprise_id` bigint(20) NOT NULL COMMENT '企业唯一标识',
  `enterprise_name` varchar(100) NOT NULL COMMENT '企业名称（唯一）',
  `enterprise_type` tinyint(4) NOT NULL COMMENT '企业类型：1-养殖，2-屠宰，3-批发，4-零售',
  `province` varchar(50) NOT NULL COMMENT '企业所在省份',
  `register_time` date NOT NULL COMMENT '企业注册时间',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业表' ROW_FORMAT=DYNAMIC;

--
-- 转存表中的数据 `sys_enterprise`
--

INSERT INTO `sys_enterprise` (`enterprise_id`, `enterprise_name`, `enterprise_type`, `province`, `register_time`, `contact_phone`, `address`, `status`, `create_time`, `update_time`) VALUES
(1, '北京绿源生态养殖场', 1, '北京市', '2020-05-12', '13800138001', '北京市大兴区养殖产业园1号', 1, '2025-10-14 20:45:17', NULL),
(2, '山东牧原养殖有限公司', 1, '山东省', '2019-08-23', '13900139002', '山东省潍坊市诸城市养殖路88号', 1, '2025-10-14 20:45:17', NULL),
(3, '上海食品屠宰加工厂', 2, '上海市', '2018-11-05', '13700137003', '上海市宝山区屠宰加工园5号', 1, '2025-10-14 20:45:17', NULL),
(4, '河南双汇屠宰分公司', 2, '河南省', '2017-03-18', '13600136004', '河南省漯河市源汇区屠宰路12号', 1, '2025-10-14 20:45:17', NULL),
(5, '广州江南食品批发市场', 3, '广东省', '2016-07-30', '13500135005', '广州市白云区江南批发市场A区201号', 1, '2025-10-14 20:45:17', NULL),
(6, '深圳海吉星农产品批发中心', 3, '广东省', '2015-09-15', '13400134006', '深圳市龙岗区海吉星批发城B3栋', 1, '2025-10-14 20:45:17', NULL),
(7, '杭州勾庄农副产品批发市场', 3, '浙江省', '2014-12-08', '13300133007', '杭州市余杭区勾庄批发市场C区108号', 1, '2025-10-14 20:45:17', NULL),
(8, '北京物美超市（中关村店）', 4, '北京市', '2021-02-14', '13200132008', '北京市海淀区中关村大街1号', 1, '2025-10-14 20:45:17', NULL),
(9, '上海盒马鲜生（陆家嘴店）', 4, '上海市', '2022-04-20', '13100131009', '上海市浦东新区陆家嘴环路1000号', 1, '2025-10-14 20:45:17', NULL),
(10, '成都永辉超市（春熙路店）', 4, '四川省', '2023-01-05', '13000130010', '成都市锦江区春熙路东段15号', 1, '2025-10-14 20:45:17', NULL);

-- --------------------------------------------------------

--
-- 表的结构 `sys_enterprise_certificate`
--

CREATE TABLE `sys_enterprise_certificate` (
  `cert_id` bigint(20) NOT NULL COMMENT '证件记录唯一标识',
  `enterprise_id` bigint(20) NOT NULL COMMENT '关联企业ID',
  `cert_type_id` int(11) NOT NULL COMMENT '关联证件类型ID',
  `cert_no` varchar(100) NOT NULL COMMENT '证件编号（唯一）',
  `valid_until` date NOT NULL COMMENT '证件有效期',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-过期/无效，1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业资质证件表' ROW_FORMAT=DYNAMIC;

--
-- 转存表中的数据 `sys_enterprise_certificate`
--

INSERT INTO `sys_enterprise_certificate` (`cert_id`, `enterprise_id`, `cert_type_id`, `cert_no`, `valid_until`, `status`, `create_time`, `update_time`) VALUES
(1, 1, 1, '91110000MA01234567', '2030-05-11', 1, '2025-10-14 20:45:17', '2025-10-21 02:42:23'),
(2, 1, 2, '防疫京字20200001', '2028-05-11', 1, '2025-10-14 20:45:17', NULL),
(3, 1, 3, '环评京字20200001', '2029-05-11', 1, '2025-10-14 20:45:17', NULL),
(4, 3, 1, '91310000MA06789012', '2030-11-04', 1, '2025-10-14 20:45:17', NULL),
(5, 3, 3, '环评沪字20180001', '2028-11-04', 1, '2025-10-14 20:45:17', NULL),
(6, 3, 7, '沪食经证20180001', '2029-11-04', 1, '2025-10-14 20:45:17', NULL),
(7, 5, 1, '91440100MA03456789', '2030-07-29', 1, '2025-10-14 20:45:17', NULL),
(8, 5, 6, '粤食流证20160001', '2028-07-29', 1, '2025-10-14 20:45:17', NULL),
(9, 5, 7, '粤食经证20160001', '2029-07-29', 1, '2025-10-14 20:45:17', NULL),
(10, 8, 1, '91110000MA09876543', '2031-02-13', 1, '2025-10-14 20:45:17', NULL),
(11, 8, 7, '京食经证20210001', '2029-02-13', 1, '2025-10-14 20:45:17', NULL);

-- --------------------------------------------------------

--
-- 表的结构 `sys_user`
--

CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL COMMENT '用户唯一标识',
  `user_type` tinyint(4) NOT NULL COMMENT '用户类型：0-系统管理员，1-养殖，2-屠宰，3-批发，4-零售',
  `login_code` varchar(50) NOT NULL COMMENT '登录编码（唯一）',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `enterprise_id` bigint(20) DEFAULT NULL COMMENT '关联企业ID（管理员为NULL）',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表' ROW_FORMAT=DYNAMIC;

--
-- 转存表中的数据 `sys_user`
--

INSERT INTO `sys_user` (`user_id`, `user_type`, `login_code`, `password`, `enterprise_id`, `status`, `create_time`, `update_time`) VALUES
(1, 0, 'admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', NULL, 1, '2025-09-25 15:23:46', NULL),
(2, 1, 'farm_1_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 1, 1, '2025-09-25 15:23:46', NULL),
(3, 1, 'farm_2_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 2, 1, '2025-09-25 15:23:46', NULL),
(4, 2, 'slaughter_1_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 3, 1, '2025-09-25 15:23:46', NULL),
(5, 2, 'slaughter_2_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 4, 1, '2025-09-25 15:23:46', NULL),
(6, 3, 'wholesale_1_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 5, 1, '2025-09-25 15:23:46', NULL),
(7, 3, 'wholesale_2_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 6, 1, '2025-09-25 15:23:46', NULL),
(8, 3, 'wholesale_3_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 7, 1, '2025-09-25 15:23:46', NULL),
(9, 4, 'retail_1_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 8, 1, '2025-09-25 15:23:46', NULL),
(10, 4, 'retail_2_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 9, 1, '2025-09-25 15:23:46', NULL),
(11, 4, 'retail_3_admin', '$2a$10$EixZaYb051F7k28H5y7Jc.9GQ5H3t7aK8B7D6F5S4A3L2Q1Z0', 10, 1, '2025-09-25 15:23:46', NULL),
(15, 0, 'wjc', '$2a$10$BikyfTkQT8TT6leMDaHabuAcdE2fMzaAzE8WxgX9ebuGBCArtQ1Ym', NULL, 1, '2025-10-14 21:49:31', '2025-10-14 21:49:31'),
(16, 1, 'wjc1', '$2a$10$WnpThBV2eSQO6P1dNCrK4.fH.epwmrqc552FZekDuVMIkwH3sLtQO', 1, 1, '2025-10-14 22:20:30', '2025-10-14 22:20:30'),
(18, 2, 'wjc2', '$2a$10$Q/0goEfMs0mEFR/Cz2FnHewWhwAK2QT83eu8tHgkxqg43fLDG1TdW', 3, 1, '2025-10-21 11:05:05', '2025-10-21 11:05:05'),
(19, 3, 'wjc3', '$2a$10$vHcDO9PY/4WOD.8VuG.y9uiN61en3tUN5RvRWuCdSgTADw1OaIUJK', 5, 1, '2025-10-21 11:17:30', '2025-10-21 11:17:30'),
(20, 4, 'wjc4', '$2a$10$BtCEsP9RrS6efONKqJpMIeKK1vQYh.IurVrhAFTh026qQhKDERxwW', 8, 1, '2025-10-21 11:43:16', '2025-10-21 11:43:16');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `biz_confirmation`
--
ALTER TABLE `biz_confirmation`
  ADD PRIMARY KEY (`confirm_id`) USING BTREE,
  ADD KEY `idx_initiate_enterprise` (`initiate_enterprise_id`) USING BTREE,
  ADD KEY `idx_receive_enterprise` (`receive_enterprise_id`) USING BTREE,
  ADD KEY `idx_batch_id` (`batch_id`) USING BTREE;

--
-- Indexes for table `prod_batch`
--
ALTER TABLE `prod_batch`
  ADD PRIMARY KEY (`batch_id`) USING BTREE,
  ADD UNIQUE KEY `uk_batch_no` (`batch_no`) USING BTREE,
  ADD KEY `idx_enterprise_id` (`enterprise_id`) USING BTREE,
  ADD KEY `idx_upstream_batch_id` (`upstream_batch_id`) USING BTREE;

--
-- Indexes for table `prod_trace_code`
--
ALTER TABLE `prod_trace_code`
  ADD PRIMARY KEY (`trace_id`) USING BTREE,
  ADD UNIQUE KEY `uk_trace_code` (`trace_code`) USING BTREE,
  ADD KEY `idx_batch_id` (`batch_id`) USING BTREE;

--
-- Indexes for table `sys_certificate_type`
--
ALTER TABLE `sys_certificate_type`
  ADD PRIMARY KEY (`cert_type_id`) USING BTREE,
  ADD UNIQUE KEY `uk_cert_type_name` (`cert_type_name`) USING BTREE;

--
-- Indexes for table `sys_cert_type_apply_enterprise`
--
ALTER TABLE `sys_cert_type_apply_enterprise`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD UNIQUE KEY `uk_cert_enterprise_type` (`cert_type_id`,`apply_enterprise_type`) USING BTREE,
  ADD KEY `idx_cert_type_id` (`cert_type_id`) USING BTREE;

--
-- Indexes for table `sys_enterprise`
--
ALTER TABLE `sys_enterprise`
  ADD PRIMARY KEY (`enterprise_id`) USING BTREE,
  ADD UNIQUE KEY `uk_enterprise_name` (`enterprise_name`) USING BTREE,
  ADD KEY `idx_enterprise_type` (`enterprise_type`) USING BTREE;

--
-- Indexes for table `sys_enterprise_certificate`
--
ALTER TABLE `sys_enterprise_certificate`
  ADD PRIMARY KEY (`cert_id`) USING BTREE,
  ADD UNIQUE KEY `uk_cert_no` (`cert_no`) USING BTREE,
  ADD KEY `idx_enterprise_id` (`enterprise_id`) USING BTREE,
  ADD KEY `idx_cert_type_id` (`cert_type_id`) USING BTREE;

--
-- Indexes for table `sys_user`
--
ALTER TABLE `sys_user`
  ADD PRIMARY KEY (`user_id`) USING BTREE,
  ADD UNIQUE KEY `uk_login_code` (`login_code`) USING BTREE,
  ADD KEY `idx_enterprise_id` (`enterprise_id`) USING BTREE;

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `biz_confirmation`
--
ALTER TABLE `biz_confirmation`
  MODIFY `confirm_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '确认请求唯一标识', AUTO_INCREMENT=12;
--
-- 使用表AUTO_INCREMENT `prod_batch`
--
ALTER TABLE `prod_batch`
  MODIFY `batch_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '批号唯一标识', AUTO_INCREMENT=22;
--
-- 使用表AUTO_INCREMENT `prod_trace_code`
--
ALTER TABLE `prod_trace_code`
  MODIFY `trace_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '溯源码记录唯一标识', AUTO_INCREMENT=14;
--
-- 使用表AUTO_INCREMENT `sys_certificate_type`
--
ALTER TABLE `sys_certificate_type`
  MODIFY `cert_type_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '证件类型唯一标识', AUTO_INCREMENT=8;
--
-- 使用表AUTO_INCREMENT `sys_cert_type_apply_enterprise`
--
ALTER TABLE `sys_cert_type_apply_enterprise`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联记录唯一标识', AUTO_INCREMENT=13;
--
-- 使用表AUTO_INCREMENT `sys_enterprise`
--
ALTER TABLE `sys_enterprise`
  MODIFY `enterprise_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '企业唯一标识', AUTO_INCREMENT=12;
--
-- 使用表AUTO_INCREMENT `sys_enterprise_certificate`
--
ALTER TABLE `sys_enterprise_certificate`
  MODIFY `cert_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '证件记录唯一标识', AUTO_INCREMENT=13;
--
-- 使用表AUTO_INCREMENT `sys_user`
--
ALTER TABLE `sys_user`
  MODIFY `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识', AUTO_INCREMENT=21;
--
-- 限制导出的表
--

--
-- 限制表 `biz_confirmation`
--
ALTER TABLE `biz_confirmation`
  ADD CONSTRAINT `fk_batch_confirmation` FOREIGN KEY (`batch_id`) REFERENCES `prod_batch` (`batch_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_initiate_enterprise` FOREIGN KEY (`initiate_enterprise_id`) REFERENCES `sys_enterprise` (`enterprise_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_receive_enterprise` FOREIGN KEY (`receive_enterprise_id`) REFERENCES `sys_enterprise` (`enterprise_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `prod_batch`
--
ALTER TABLE `prod_batch`
  ADD CONSTRAINT `fk_enterprise_batch` FOREIGN KEY (`enterprise_id`) REFERENCES `sys_enterprise` (`enterprise_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_upstream_batch` FOREIGN KEY (`upstream_batch_id`) REFERENCES `prod_batch` (`batch_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- 限制表 `prod_trace_code`
--
ALTER TABLE `prod_trace_code`
  ADD CONSTRAINT `fk_batch_trace_code` FOREIGN KEY (`batch_id`) REFERENCES `prod_batch` (`batch_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `sys_cert_type_apply_enterprise`
--
ALTER TABLE `sys_cert_type_apply_enterprise`
  ADD CONSTRAINT `fk_cert_type_apply` FOREIGN KEY (`cert_type_id`) REFERENCES `sys_certificate_type` (`cert_type_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `sys_enterprise_certificate`
--
ALTER TABLE `sys_enterprise_certificate`
  ADD CONSTRAINT `fk_cert_type_cert` FOREIGN KEY (`cert_type_id`) REFERENCES `sys_certificate_type` (`cert_type_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_enterprise_cert` FOREIGN KEY (`enterprise_id`) REFERENCES `sys_enterprise` (`enterprise_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `sys_user`
--
ALTER TABLE `sys_user`
  ADD CONSTRAINT `fk_user_enterprise` FOREIGN KEY (`enterprise_id`) REFERENCES `sys_enterprise` (`enterprise_id`) ON DELETE SET NULL ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
