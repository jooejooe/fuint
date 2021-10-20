/*
SQLyog Professional v13.1.1 (64 bit)
MySQL - 8.0.21 : Database - fuint-db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`fuint-db` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `fuint-db`;

/*Table structure for table `mt_banner` */

DROP TABLE IF EXISTS `mt_banner`;

CREATE TABLE `mt_banner` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TITLE` varchar(100) DEFAULT '' COMMENT '标题',
  `URL` varchar(100) DEFAULT '' COMMENT '链接地址',
  `IMAGE` varchar(200) DEFAULT '' COMMENT '图片地址',
  `DESCRIPTION` text COMMENT '描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `mt_banner` */

insert  into `mt_banner`(`ID`,`TITLE`,`URL`,`IMAGE`,`DESCRIPTION`,`CREATE_TIME`,`UPDATE_TIME`,`OPERATOR`,`STATUS`) values 
(1,'测试001','sdfsd','/static/defaultImage/banner-1.png','对对对','2021-04-14 09:38:20','2021-10-07 07:40:43',NULL,'A'),
(2,'测试002','dsfsds','/static/defaultImage/banner-2.png','顶顶顶顶','2021-04-14 09:38:36','2021-10-07 07:40:32',NULL,'A');

/*Table structure for table `mt_cart` */

DROP TABLE IF EXISTS `mt_cart`;

CREATE TABLE `mt_cart` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '会员ID',
  `GOODS_ID` int DEFAULT '0' COMMENT '商品ID',
  `NUM` int DEFAULT '1' COMMENT '数量',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='购物车';

/*Data for the table `mt_cart` */

/*Table structure for table `mt_confirm_log` */

DROP TABLE IF EXISTS `mt_confirm_log`;

CREATE TABLE `mt_confirm_log` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `CODE` varchar(32) NOT NULL DEFAULT '' COMMENT '编码',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '核销金额',
  `COUPON_ID` int DEFAULT '0' COMMENT '卡券ID',
  `USER_COUPON_ID` int NOT NULL DEFAULT '0' COMMENT '用户券ID',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '卡券所属用户ID',
  `OPERATOR_USER_ID` int DEFAULT NULL COMMENT '核销者用户ID',
  `STORE_ID` int NOT NULL DEFAULT '0' COMMENT '核销店铺ID',
  `STATUS` varchar(1) NOT NULL COMMENT '状态，A正常核销；D：撤销使用',
  `CANCEL_TIME` datetime DEFAULT NULL COMMENT '撤销时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `OPERATOR_FROM` varchar(30) DEFAULT 'mt_user' COMMENT '操作来源user_id对应表t_account 还是 mt_user',
  `REMARK` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='核销记录表';

/*Data for the table `mt_confirm_log` */

/*Table structure for table `mt_confirmer` */

DROP TABLE IF EXISTS `mt_confirmer`;

CREATE TABLE `mt_confirmer` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int DEFAULT NULL COMMENT '用户ID',
  `MOBILE` varchar(16) NOT NULL DEFAULT '' COMMENT '手机号码',
  `REAL_NAME` varchar(30) DEFAULT '' COMMENT '真实姓名',
  `WECHAT` varchar(64) DEFAULT NULL COMMENT '微信号',
  `STORE_ID` int DEFAULT NULL COMMENT '对应的核销店铺id',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `AUDITED_STATUS` char(1) DEFAULT 'U' COMMENT '审核状态，A：审核通过；U：未审核；D：无效; ',
  `AUDITED_TIME` datetime DEFAULT NULL COMMENT '审核时间',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uix_mobile` (`MOBILE`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='酒店核销人员表';

/*Data for the table `mt_confirmer` */

insert  into `mt_confirmer`(`ID`,`USER_ID`,`MOBILE`,`REAL_NAME`,`WECHAT`,`STORE_ID`,`CREATE_TIME`,`UPDATE_TIME`,`AUDITED_STATUS`,`AUDITED_TIME`,`DESCRIPTION`) values 
(1,1,'18976679980','陈经理',NULL,2,'2020-04-17 18:01:38','2021-06-04 20:06:46','A','2020-04-17 18:01:53',NULL),
(2,2,'18616982980','王超',NULL,2,'2021-07-05 15:53:44','2021-07-05 15:53:50','A','2021-07-05 15:53:56',NULL);

/*Table structure for table `mt_coupon` */

DROP TABLE IF EXISTS `mt_coupon`;

CREATE TABLE `mt_coupon` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `GROUP_ID` int NOT NULL DEFAULT '0' COMMENT '券组ID',
  `TYPE` char(1) DEFAULT 'C' COMMENT '券类型，C优惠券；P预存卡；T集次卡',
  `NAME` varchar(100) NOT NULL DEFAULT '' COMMENT '券名称',
  `IS_GIVE` tinyint(1) DEFAULT '0' COMMENT '是否允许转赠',
  `POINT` int DEFAULT '0' COMMENT '获得卡券所消耗积分',
  `RECEIVE_CODE` varchar(32) DEFAULT '' COMMENT '领取码',
  `BEGIN_TIME` datetime DEFAULT NULL COMMENT '开始有效期',
  `END_TIME` datetime DEFAULT NULL COMMENT '结束有效期',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '面额',
  `SEND_WAY` varchar(20) DEFAULT 'backend' COMMENT '发放方式',
  `SEND_NUM` int DEFAULT '1' COMMENT '每次发放数量',
  `TOTAL` int DEFAULT '0' COMMENT '发行数量',
  `LIMIT_NUM` int DEFAULT '1' COMMENT '每人拥有数量限制',
  `EXCEPT_TIME` varchar(500) DEFAULT '' COMMENT '不可用日期，逗号隔开。周末：weekend；其他：2019-01-02_2019-02-09',
  `STORE_IDS` varchar(100) DEFAULT '' COMMENT '所属店铺ID,逗号隔开',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '描述信息',
  `IMAGE` varchar(100) DEFAULT '' COMMENT '效果图片',
  `REMARKS` varchar(1000) DEFAULT '' COMMENT '后台备注',
  `IN_RULE` varchar(1000) DEFAULT '' COMMENT '获取券的规则',
  `OUT_RULE` varchar(1000) DEFAULT '' COMMENT '核销券的规则',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='卡券信息表';

/*Data for the table `mt_coupon` */

insert  into `mt_coupon`(`ID`,`GROUP_ID`,`TYPE`,`NAME`,`IS_GIVE`,`POINT`,`RECEIVE_CODE`,`BEGIN_TIME`,`END_TIME`,`AMOUNT`,`SEND_WAY`,`SEND_NUM`,`TOTAL`,`LIMIT_NUM`,`EXCEPT_TIME`,`STORE_IDS`,`DESCRIPTION`,`IMAGE`,`REMARKS`,`IN_RULE`,`OUT_RULE`,`CREATE_TIME`,`UPDATE_TIME`,`OPERATOR`,`STATUS`) values 
(2,4,'C','51活动优惠券',0,0,'','2021-04-22 15:35:00','2022-10-01 11:15:00',100.00,'offline',2,1000,1,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p><p>【先预约，再服务】春节期间（2021年1月21日—2021年2月27日），网点洗车人数剧增，排队等待时间较长，使用服务前请先查询可适用网点，致电网点预约服务时间，错峰到店（点开任意一家门店后可在“电话按钮”处点开服务电话），由此给您造成不便，敬请谅解！</p>','/static/defaultImage/coupon.png','对对对','','1000','2021-03-22 15:36:00','2021-03-22 15:36:00','fuint','A'),
(3,4,'P','2021年51限额500名活动预存券',0,0,'','2021-04-22 11:15:00','2021-09-30 11:15:00',0.00,'front',1,1000,1,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p><p>【先预约，再服务】春节期间（2021年1月21日—2021年2月27日），网点洗车人数剧增，排队等待时间较长，使用服务前请先查询可适用网点，致电网点预约服务时间，错峰到店（点开任意一家门店后可在“电话按钮”处点开服务电话），由此给您造成不便，敬请谅解！</p>','/static/defaultImage/card.png','s是','100_200,500_1000,1500_2000,5000_10000','','2021-04-22 11:15:54','2021-04-22 11:15:54','fuint','A'),
(4,4,'C','酬宾早餐优惠券',0,0,'','2021-04-22 11:26:00','2021-09-30 11:26:00',50.00,'front',1,1000,1,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p><p>【先预约，再服务】春节期间（2021年1月21日—2021年2月27日），网点洗车人数剧增，排队等待时间较长，使用服务前请先查询可适用网点，致电网点预约服务时间，错峰到店（点开任意一家门店后可在“电话按钮”处点开服务电话），由此给您造成不便，敬请谅解！</p>','/static/defaultImage/coupon.png','水水水水','','300','2021-04-22 11:26:42','2021-04-22 11:26:42','fuint','A'),
(5,4,'T','5月洗车集次卡',0,0,'','2021-04-22 14:49:00','2021-09-30 14:49:00',0.00,'front',1,1000,1,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p><p>【先预约，再服务】春节期间（2021年1月21日—2021年2月27日），网点洗车人数剧增，排队等待时间较长，使用服务前请先查询可适用网点，致电网点预约服务时间，错峰到店（点开任意一家门店后可在“电话按钮”处点开服务电话），由此给您造成不便，敬请谅解！</p>','/static/defaultImage/card.png','集次','','8','2021-04-22 14:49:26','2021-04-22 14:49:26','fuint','A'),
(6,4,'C','端午8折优惠券',0,0,'','2021-05-23 07:35:00','2022-02-28 07:35:00',50.00,'front',1,1000,1,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p>','/static/defaultImage/coupon.png','端午','','200','2021-05-23 07:35:58','2021-05-23 07:35:58','fuint','A'),
(7,4,'P','2021年端午预存大礼包',1,0,'','2021-04-08 21:30:00','2022-05-31 21:30:00',0.00,'front',1,1000,2,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p>','/static/defaultImage/coupon.png','端午','100_200,1000_2000,10000_20000','','2021-05-24 21:31:00','2021-05-24 21:31:00','fuint','A'),
(8,4,'C','2021年5月份大促销优惠券',1,0,'','2021-05-19 21:53:00','2022-02-24 21:53:00',80.00,'front',1,1000,2,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p>','/static/defaultImage/coupon.png','5月份','','488','2021-05-24 21:54:17','2021-05-24 21:54:17','fuint','A'),
(9,4,'C','5月美妆楼层优惠小促销',0,0,'','2021-05-24 21:59:00','2022-02-24 21:59:00',100.00,'front',1,1000,1,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p>','/static/defaultImage/coupon.png','美妆','','888','2021-05-24 22:00:53','2021-05-24 22:00:53','fuint','A'),
(10,4,'C','六一童装优惠券',0,0,'','2021-05-24 22:03:00','2022-03-31 22:03:00',80.00,'front',1,1000,1,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p>','/static/defaultImage/coupon.png','六一','','200','2021-05-24 22:03:53','2021-05-24 22:03:53','fuint','A'),
(11,4,'C','10元无门槛电子券',0,0,'','2021-05-24 22:40:00','2022-02-25 22:40:00',10.00,'front',1,888,1,'',NULL,'<p>1、凭本卡券到适用网点可享受车辆洗车服务一次，每日限用1张，若超标使用导致账户锁定我司不予处理。</p></br><p>2、使用本卡券前，需点击查看最新的“适用网点”，并致电网点预约服务时间，错峰到店。因服务网点实时变动，如您未查看【适用网点】，而前往不合作的网点使用服务，或未提前预约导致您行程耽误的，期间产生的损失与费用，我司概不负责。</p></br><p>3、 本卡券禁止外借或转赠他人，若发现我司有权回收权益，卡券使用最终解释权归平安产险海南分公司所有。</p></br><p></p>','/static/defaultImage/coupon.png','优惠007','','0','2021-05-24 22:40:37','2021-05-24 22:40:37','fuint','A'),
(12,4,'T','2021年5月份餐饮集次卡',0,0,'0','2021-05-28 10:08:00','2022-01-01 10:08:00',0.00,'front',1,0,1,'',NULL,'吃饭送精品菜','/static/defaultImage/coupon.png','','','6','2021-05-28 10:10:26','2021-05-28 10:10:26','fuint','A'),
(13,4,'P','2021中秋预存活动',0,0,'','2021-09-15 04:15:00','2021-09-30 04:15:00',0.00,'front',1,0,1,'','1','666','/static/defaultImage/coupon.png','','111_112,222_223','','2021-09-15 04:16:35','2021-09-15 04:16:35','fuint','A'),
(14,6,'C','优惠券10元',0,10,'','2021-09-24 04:17:00','2021-09-30 04:17:00',100.00,'front',1,100,1,'',NULL,'啦啦啦','/static/defaultImage/coupon.png','啊啊啊','','1000','2021-09-24 04:18:57','2021-09-24 04:18:57','fuint','A'),
(15,5,'C','100元优惠券',0,100,'','2021-09-24 04:19:00','2021-09-30 04:19:00',20.00,'front',1,50,1,'',NULL,'顶顶顶顶','/static/defaultImage/coupon.png','反反复复','','50','2021-09-24 04:19:54','2021-09-24 04:19:54','fuint','A'),
(17,5,'C','10月份优惠券',0,10,'','2021-09-27 01:50:00','2021-11-27 01:50:00',18.00,'front',1,100,1,'',NULL,'顶顶顶顶','/static/defaultImage/coupon.png','点点滴滴','','20','2021-09-27 01:51:22','2021-09-27 01:51:22','fuint','A'),
(18,6,'C','ewewe',0,0,'','2021-10-07 04:26:00','2021-10-30 04:26:00',10.00,'backend',1,1,1,'',NULL,'dsfsfsd','/static/uploadImages/20211007/7ccecfd8628040cfbe276648b8ce0764.png','ee','','1','2021-10-07 04:26:36','2021-10-07 04:26:36','fuint','A'),
(19,6,'C','好啊好啊',0,0,'','2021-10-07 07:43:00','2021-10-30 07:43:00',100.00,'backend',1,1,1,'',NULL,'d滴滴滴','/static/images/20211007/7a7fbb95956c4497ba9320335f2734f0.png','是的','','1000','2021-10-07 07:44:01','2021-10-07 07:44:01','fuint','A');

/*Table structure for table `mt_coupon_group` */

DROP TABLE IF EXISTS `mt_coupon_group`;

CREATE TABLE `mt_coupon_group` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(100) NOT NULL DEFAULT '' COMMENT '券组名称',
  `MONEY` decimal(18,2) DEFAULT '0.00' COMMENT '价值金额',
  `NUM` int DEFAULT '0' COMMENT '券种类数量',
  `TOTAL` int DEFAULT '0' COMMENT '发行数量',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '备注',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建日期',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新日期',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='优惠券组';

/*Data for the table `mt_coupon_group` */

insert  into `mt_coupon_group`(`ID`,`NAME`,`MONEY`,`NUM`,`TOTAL`,`DESCRIPTION`,`CREATE_TIME`,`UPDATE_TIME`,`OPERATOR`,`STATUS`) values 
(4,'2021年中秋',0.00,0,0,'测试','2021-03-16 17:16:03','2021-09-24 04:17:01','fuint','A'),
(5,'2021十一活动',0.00,2,0,'2021十一活动','2021-09-24 03:40:26','2021-09-24 04:16:52','fuint','A'),
(6,'十月份活动',0.00,3,0,'滴滴滴','2021-09-24 04:17:17','2021-09-24 04:17:26','fuint','A');

/*Table structure for table `mt_give` */

DROP TABLE IF EXISTS `mt_give`;

CREATE TABLE `mt_give` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '获赠者用户ID',
  `GIVE_USER_ID` int NOT NULL DEFAULT '0' COMMENT '赠送者用户ID',
  `MOBILE` varchar(20) NOT NULL DEFAULT '' COMMENT '赠予对象手机号',
  `HNA_ACCOUNT` varchar(50) DEFAULT NULL COMMENT '赠予对象海航账号',
  `USER_MOBILE` varchar(20) NOT NULL DEFAULT '' COMMENT '用户手机',
  `USER_HNA_ACCOUNT` varchar(50) DEFAULT NULL COMMENT '用户海航账号',
  `GROUP_IDS` varchar(200) NOT NULL DEFAULT '' COMMENT '券组ID，逗号隔开',
  `GROUP_NAMES` varchar(500) NOT NULL DEFAULT '' COMMENT '券组名称，逗号隔开',
  `COUPON_IDS` varchar(200) NOT NULL DEFAULT '' COMMENT '券ID，逗号隔开',
  `COUPON_NAMES` varchar(500) NOT NULL DEFAULT '' COMMENT '券名称，逗号隔开',
  `NUM` int NOT NULL DEFAULT '0' COMMENT '数量',
  `MONEY` decimal(10,2) NOT NULL COMMENT '总金额',
  `NOTE` varchar(200) DEFAULT '' COMMENT '备注',
  `MESSAGE` varchar(500) DEFAULT '' COMMENT '留言',
  `CREATE_TIME` datetime NOT NULL COMMENT '赠送时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '状态，A正常；C取消',
  PRIMARY KEY (`ID`),
  KEY `index_user_id` (`USER_ID`) USING BTREE,
  KEY `index_give_user_id` (`GIVE_USER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='转赠记录表';

/*Data for the table `mt_give` */

/*Table structure for table `mt_give_item` */

DROP TABLE IF EXISTS `mt_give_item`;

CREATE TABLE `mt_give_item` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `GIVE_ID` int NOT NULL COMMENT '转赠ID',
  `USER_COUPON_ID` int NOT NULL COMMENT '用户电子券ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIEM` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) NOT NULL COMMENT '状态，A正常；D删除',
  PRIMARY KEY (`ID`),
  KEY `index_give_id` (`GIVE_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='转赠明细表';

/*Data for the table `mt_give_item` */

/*Table structure for table `mt_goods` */

DROP TABLE IF EXISTS `mt_goods`;

CREATE TABLE `mt_goods` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(100) DEFAULT '' COMMENT '商品名称',
  `CATE_ID` int DEFAULT '0' COMMENT '分类ID',
  `GOODS_NO` varchar(100) DEFAULT '' COMMENT '商品编码',
  `LOGO` varchar(200) DEFAULT '' COMMENT '主图地址',
  `IMAGES` varchar(1000) DEFAULT '' COMMENT '图片地址',
  `PRICE` decimal(10,2) DEFAULT '0.00' COMMENT '价格',
  `LINE_PRICE` decimal(10,2) DEFAULT '0.00' COMMENT '划线价格',
  `STOCK` int DEFAULT '0' COMMENT '库存',
  `WEIGHT` decimal(10,2) DEFAULT '0.00' COMMENT '重量',
  `INIT_SALE` int DEFAULT '0' COMMENT '初始销量',
  `SALE_POINT` varchar(100) DEFAULT '' COMMENT '商品卖点',
  `CAN_USE_POINT` char(1) DEFAULT 'N' COMMENT '可否使用积分抵扣',
  `IS_MEMBER_DISCOUNT` char(1) DEFAULT 'Y' COMMENT '会员是否有折扣',
  `SORT` int DEFAULT '0' COMMENT '排序',
  `DESCRIPTION` text COMMENT '商品描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `mt_goods` */

insert  into `mt_goods`(`ID`,`NAME`,`CATE_ID`,`GOODS_NO`,`LOGO`,`IMAGES`,`PRICE`,`LINE_PRICE`,`STOCK`,`WEIGHT`,`INIT_SALE`,`SALE_POINT`,`CAN_USE_POINT`,`IS_MEMBER_DISCOUNT`,`SORT`,`DESCRIPTION`,`CREATE_TIME`,`UPDATE_TIME`,`OPERATOR`,`STATUS`) values 
(1,'芒果100kg',3,'998324234','/static/uploadImages/20211013/c255fd15adce41deae3017e769e87aac.png','[\"/static/uploadImages/20211013/c255fd15adce41deae3017e769e87aac.png\",\"/static/uploadImages/20211013/1981d0b9939647a7969668e643bef142.png\",\"/static/uploadImages/20211013/e72a2c7370b94b699d37a6af5a8108c2.png\"]',100.00,200.00,1989,1.00,99,'好吃看得见！','Y','Y',1,'<p>芒果 芒果！<br/></p><p>好芒果看得见。</p><p>好吃！</p><p>不贵！</p><p>超级实惠哦~~</p><p>速来购买。<br/></p>','2021-10-13 13:56:04','2021-10-14 01:13:09','fuint','A'),
(2,'南瓜2kg',4,'3423423','/static/uploadImages/20211013/4054d40ef15b486280ac3d17bd7a22e3.png','[\"/static/uploadImages/20211013/4054d40ef15b486280ac3d17bd7a22e3.png\",\"/static/uploadImages/20211013/4dcfc096ddce49b6bd9738941dcd3f05.png\"]',99.00,199.00,1008,1.00,89,'囊','Y','Y',1,'<p>南瓜，顶呱呱！<br/></p>','2021-10-13 14:19:45','2021-10-14 05:45:31','fuint','A'),
(3,'青瓜商品',4,'92342342342','/static/uploadImages/20211014/c3adefbc3cfc49b6aa54ba3ab06d1dd2.png','[\"/static/uploadImages/20211014/c3adefbc3cfc49b6aa54ba3ab06d1dd2.png\",\"/static/uploadImages/20211014/e7350e5e46ec4eaa9f867bf578dae550.png\",\"/static/uploadImages/20211014/338cd2965f6845dc832f01ed0cdd81fb.png\"]',100.00,200.00,9996,1.00,234,'好东西','Y','Y',1,'<p>呵呵呵</p><ul style=\"list-style-type: disc;\" class=\" list-paddingleft-2\"><li><p>123</p></li><li><p>345<br/></p></li></ul>','2021-10-14 01:08:45','2021-10-14 05:45:10','fuint','A'),
(4,'熏干腊肉',1,'52623524523','/static/uploadImages/20211014/8e135c43ee1b409180f7ea2e92b05a4b.png','[\"/static/uploadImages/20211014/8e135c43ee1b409180f7ea2e92b05a4b.png\"]',123.00,2121.00,6667,1.00,212,'测试测试吧','Y','Y',1,'<p>啦啦啦<br/></p>','2021-10-14 05:46:23','2021-10-14 05:46:50','fuint','A'),
(5,'竹笋1kg',2,'5436524635','/static/uploadImages/20211014/7efe27fe4c5544d1b5e4e6a3d8903b49.png','[\"/static/uploadImages/20211014/7efe27fe4c5544d1b5e4e6a3d8903b49.png\"]',100.00,234.00,8879,22.00,56,'来来来','Y','Y',1,'<p>滴滴滴<br/></p>','2021-10-14 05:47:31','2021-10-14 05:47:59','fuint','A'),
(6,'富硒红薯5kg',4,'3546346533','/static/uploadImages/20211014/6e10fc63c6de4eacb950d945f1594f12.png','[\"/static/uploadImages/20211014/6e10fc63c6de4eacb950d945f1594f12.png\",\"/static/uploadImages/20211014/691d40a3e50f40dcbd3c4a18ec7b6cc7.png\"]',555.00,33333.00,2229,1.00,543,'快快快','Y','Y',1,'<p>嗯嗯嗯<br/></p>','2021-10-14 05:48:43','2021-10-14 05:49:08','fuint','A');

/*Table structure for table `mt_goods_cate` */

DROP TABLE IF EXISTS `mt_goods_cate`;

CREATE TABLE `mt_goods_cate` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(100) DEFAULT '' COMMENT '分类名称',
  `LOGO` varchar(200) DEFAULT '' COMMENT 'LOGO地址',
  `DESCRIPTION` text COMMENT '分类描述',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '最后操作人',
  `SORT` int DEFAULT '0' COMMENT '排序',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `mt_goods_cate` */

insert  into `mt_goods_cate`(`ID`,`NAME`,`LOGO`,`DESCRIPTION`,`CREATE_TIME`,`UPDATE_TIME`,`OPERATOR`,`SORT`,`STATUS`) values 
(1,'当季水果','/static/uploadImages/20211009/074d2359a2ee4a9e8c34e0d0926b6e47.png','999','2021-10-09 06:27:11','2021-10-11 09:55:34','fuint',0,'A'),
(2,'精制干货','/static/uploadImages/20211009/e1265a59d15d402d86ae6a1bef9b7f32.png','ddd','2021-10-09 06:27:11','2021-10-11 09:55:50','fuint',1,'A'),
(3,'新鲜蔬菜','/static/uploadImages/20211009/9cbbb51dd27944aeb5e6303d89e05b98.png','等等','2021-10-09 06:27:11','2021-10-09 06:27:11','fuint',NULL,'A'),
(4,'当季&热卖','/static/uploadImages/20211014/9a4055aa19ff4724a6db521196da1780.png','111','2021-10-14 02:20:00','2021-10-14 02:20:00','fuint',NULL,'A');

/*Table structure for table `mt_message` */

DROP TABLE IF EXISTS `mt_message`;

CREATE TABLE `mt_message` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int NOT NULL COMMENT '用户ID',
  `TYPE` varchar(30) NOT NULL DEFAULT '' COMMENT '消息类型',
  `TITLE` varchar(200) DEFAULT '友情提示' COMMENT '消息标题',
  `CONTENT` varchar(500) NOT NULL DEFAULT '' COMMENT '消息内容',
  `IS_READ` char(1) DEFAULT 'N' COMMENT '是否已读',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `mt_message` */

/*Table structure for table `mt_open_gift` */

DROP TABLE IF EXISTS `mt_open_gift`;

CREATE TABLE `mt_open_gift` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `STORE_ID` int NOT NULL DEFAULT '0' COMMENT '门店ID',
  `GRADE_ID` int NOT NULL DEFAULT '0' COMMENT '会员等级ID',
  `POINT` int NOT NULL DEFAULT '0' COMMENT '赠送积分',
  `COUPON_ID` int NOT NULL DEFAULT '0' COMMENT '卡券ID',
  `COUPON_NUM` int NOT NULL DEFAULT '1' COMMENT '卡券数量',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='会员开卡赠礼';

/*Data for the table `mt_open_gift` */

insert  into `mt_open_gift`(`ID`,`STORE_ID`,`GRADE_ID`,`POINT`,`COUPON_ID`,`COUPON_NUM`,`CREATE_TIME`,`UPDATE_TIME`,`STATUS`,`OPERATOR`) values 
(1,0,1,1010,4,1,'2021-09-08 13:41:27','2021-09-09 09:29:37','A','fuint'),
(2,0,2,100,2,1,'2021-09-09 03:36:45','2021-09-09 06:42:58','D','fuint'),
(3,0,2,0,2,2,'2021-09-09 06:48:02','2021-09-09 06:48:21','A','fuint'),
(4,0,1,0,11,1,'2021-09-09 21:19:32','2021-09-09 21:19:32','A','fuint');

/*Table structure for table `mt_open_gift_item` */

DROP TABLE IF EXISTS `mt_open_gift_item`;

CREATE TABLE `mt_open_gift_item` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int NOT NULL COMMENT '会用ID',
  `OPEN_GIFT_ID` int NOT NULL COMMENT '赠礼ID',
  `CREATE_TIME` datetime NOT NULL COMMENT '创建时间',
  `STATUS` char(1) NOT NULL COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf32 COMMENT='开卡赠礼明细表';

/*Data for the table `mt_open_gift_item` */

/*Table structure for table `mt_order` */

DROP TABLE IF EXISTS `mt_order`;

CREATE TABLE `mt_order` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TYPE` varchar(30) DEFAULT NULL COMMENT '类型',
  `TYPE_NAME` varchar(30) DEFAULT '' COMMENT '类型名称',
  `ORDER_SN` varchar(32) NOT NULL DEFAULT '' COMMENT '订单号',
  `COUPON_ID` int DEFAULT '0' COMMENT '卡券ID',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '用户ID',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '订单金额',
  `PAY_AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '支付金额',
  `USE_POINT` int DEFAULT '0' COMMENT '使用积分数量',
  `POINT_AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '积分金额',
  `DISCOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '折扣金额',
  `PARAM` varchar(500) DEFAULT '' COMMENT '订单参数',
  `USER_INFO` varchar(500) DEFAULT '' COMMENT '用户信息',
  `REMARK` varchar(500) DEFAULT '' COMMENT '用户备注',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '订单状态',
  `PAY_TIME` datetime DEFAULT NULL COMMENT '支付时间',
  `PAY_STATUS` char(1) DEFAULT '' COMMENT '支付状态',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

/*Data for the table `mt_order` */

/*Table structure for table `mt_order_goods` */

DROP TABLE IF EXISTS `mt_order_goods`;

CREATE TABLE `mt_order_goods` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `ORDER_ID` int NOT NULL DEFAULT '0' COMMENT '订单ID',
  `GOODS_ID` int NOT NULL DEFAULT '0' COMMENT '商品ID',
  `NUM` int NOT NULL DEFAULT '0' COMMENT '商品数量',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT 'A：正常；D：删除',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `mt_order_goods` */

/*Table structure for table `mt_point` */

DROP TABLE IF EXISTS `mt_point`;

CREATE TABLE `mt_point` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `USER_ID` int NOT NULL DEFAULT '0' COMMENT '用户ID',
  `ORDER_SN` varchar(32) DEFAULT '' COMMENT '订单号',
  `AMOUNT` int NOT NULL DEFAULT '0' COMMENT '积分变化数量',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `DESCRIPTION` varchar(200) DEFAULT '' COMMENT '备注说明',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A正常；D作废',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='积分变化表';

/*Data for the table `mt_point` */

/*Table structure for table `mt_refund` */

DROP TABLE IF EXISTS `mt_refund`;

CREATE TABLE `mt_refund` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `ORDER_ID` int NOT NULL COMMENT '订单ID',
  `USER_ID` int NOT NULL COMMENT '会员ID',
  `REMARK` varchar(500) DEFAULT '' COMMENT '退款备注',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='售后表';

/*Data for the table `mt_refund` */

/*Table structure for table `mt_send_log` */

DROP TABLE IF EXISTS `mt_send_log`;

CREATE TABLE `mt_send_log` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TYPE` tinyint(1) NOT NULL COMMENT '1：单用户发券；2：批量发券',
  `USER_ID` int DEFAULT NULL COMMENT '用户ID',
  `FILE_NAME` varchar(100) DEFAULT '' COMMENT '导入excel文件名',
  `FILE_PATH` varchar(200) DEFAULT '' COMMENT '导入excel文件路径',
  `MOBILE` varchar(20) NOT NULL COMMENT '用户手机',
  `GROUP_ID` int NOT NULL COMMENT '券组ID',
  `GROUP_NAME` varchar(100) DEFAULT '' COMMENT '券组名称',
  `SEND_NUM` int DEFAULT NULL COMMENT '发放套数',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '操作时间',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '操作人',
  `UUID` varchar(50) DEFAULT '' COMMENT '导入UUID',
  `REMOVE_SUCCESS_NUM` int DEFAULT '0' COMMENT '作废成功张数',
  `REMOVE_FAIL_NUM` int DEFAULT '0' COMMENT '作废失败张数',
  `STATUS` char(1) DEFAULT NULL COMMENT '状态，A正常；B：部分作废；D全部作废',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='卡券发放记录表';

/*Data for the table `mt_send_log` */

/*Table structure for table `mt_setting` */

DROP TABLE IF EXISTS `mt_setting`;

CREATE TABLE `mt_setting` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `TYPE` varchar(10) NOT NULL DEFAULT '' COMMENT '类型',
  `NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '配置项',
  `VALUE` varchar(1000) NOT NULL DEFAULT '' COMMENT '配置值',
  `DESCRIPTION` varchar(200) DEFAULT '' COMMENT '配置说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态 A启用；D禁用',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 COMMENT='全局设置表';

/*Data for the table `mt_setting` */

insert  into `mt_setting`(`ID`,`TYPE`,`NAME`,`VALUE`,`DESCRIPTION`,`CREATE_TIME`,`UPDATE_TIME`,`OPERATOR`,`STATUS`) values 
(1,'point','pointNeedConsume','10','返1积分所需消费金额',NULL,'2021-09-19 14:27:03','fuint','D'),
(2,'point','canUsedAsMoney','true','是否可当作现金使用',NULL,'2021-09-19 14:27:03','fuint','D'),
(3,'point','exchangeNeedPoint','100','多少积分可抵扣1元现金',NULL,'2021-09-19 14:27:03','fuint','D'),
(4,'point','rechargePointSpeed','5','充值返积分倍数',NULL,'2021-09-19 14:27:03','fuint','D'),
(5,'point','pointNeedConsume','10','返1积分所需消费金额',NULL,'2021-09-28 08:10:47','fuint','A'),
(6,'point','canUsedAsMoney','true','是否可当作现金使用',NULL,'2021-09-28 08:10:47','fuint','A'),
(7,'point','exchangeNeedPoint','100','多少积分可抵扣1元现金',NULL,'2021-09-28 08:10:47','fuint','A'),
(8,'point','rechargePointSpeed','5','充值返积分倍数',NULL,'2021-09-28 08:10:47','fuint','A'),
(9,'user','getCouponNeedPhone','true','领券是否需要手机号码','2021-09-26 07:43:58','2021-09-26 07:43:58','fuint',NULL),
(10,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-09-26 07:43:58','2021-09-26 07:43:58','fuint',NULL),
(11,'user','loginNeedPhone','true','登录是否需要手机号','2021-09-26 07:43:58','2021-09-26 07:43:58','fuint',NULL),
(12,'user','getCouponNeedPhone','false','领券是否需要手机号码','2021-09-26 07:44:12','2021-09-26 07:44:12','fuint',NULL),
(13,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-09-26 07:44:12','2021-09-26 07:44:12','fuint',NULL),
(14,'user','loginNeedPhone','false','登录是否需要手机号','2021-09-26 07:44:12','2021-09-26 07:44:12','fuint',NULL),
(15,'user','getCouponNeedPhone','true','领券是否需要手机号码','2021-09-26 07:52:09','2021-09-26 07:52:09','fuint',NULL),
(16,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-09-26 07:52:09','2021-09-26 07:52:09','fuint',NULL),
(17,'user','loginNeedPhone','false','登录是否需要手机号','2021-09-26 07:52:09','2021-09-26 07:52:09','fuint',NULL),
(18,'user','getCouponNeedPhone','true','领券是否需要手机号码','2021-09-26 08:28:34','2021-09-26 08:28:34','fuint',NULL),
(19,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-09-26 08:28:34','2021-09-26 08:28:34','fuint',NULL),
(20,'user','loginNeedPhone','true','登录是否需要手机号','2021-09-26 08:28:34','2021-09-26 08:28:34','fuint',NULL),
(21,'user','getCouponNeedPhone','true','领券是否需要手机号码','2021-09-26 08:28:58','2021-09-26 08:28:58','fuint',NULL),
(22,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-09-26 08:28:58','2021-09-26 08:28:58','fuint',NULL),
(23,'user','loginNeedPhone','false','登录是否需要手机号','2021-09-26 08:28:58','2021-09-26 08:28:58','fuint',NULL),
(24,'user','getCouponNeedPhone','true','领券是否需要手机号码','2021-09-26 08:29:51','2021-09-26 08:29:51','fuint',NULL),
(25,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-09-26 08:29:51','2021-09-26 08:29:51','fuint',NULL),
(26,'user','loginNeedPhone','true','登录是否需要手机号','2021-09-26 08:29:52','2021-09-26 08:29:52','fuint',NULL),
(27,'user','getCouponNeedPhone','true','领券是否需要手机号码','2021-09-26 08:44:47','2021-09-26 08:44:47','fuint',NULL),
(28,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-09-26 08:44:47','2021-09-26 08:44:47','fuint',NULL),
(29,'user','loginNeedPhone','false','登录是否需要手机号','2021-09-26 08:44:47','2021-09-26 08:44:47','fuint',NULL),
(30,'user','getCouponNeedPhone','true','领券是否需要手机号码','2021-10-12 06:18:33','2021-10-12 06:18:33','fuint',NULL),
(31,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-10-12 06:18:33','2021-10-12 06:18:33','fuint',NULL),
(32,'user','loginNeedPhone','true','登录是否需要手机号','2021-10-12 06:18:33','2021-10-12 06:18:33','fuint',NULL),
(33,'user','getCouponNeedPhone','false','领券是否需要手机号码','2021-10-12 21:31:58','2021-10-12 21:31:58','fuint',NULL),
(34,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-10-12 21:31:58','2021-10-12 21:31:58','fuint',NULL),
(35,'user','loginNeedPhone','true','登录是否需要手机号','2021-10-12 21:31:58','2021-10-12 21:31:58','fuint',NULL),
(36,'user','getCouponNeedPhone','false','领券是否需要手机号码','2021-10-12 21:32:05','2021-10-12 21:32:05','fuint',NULL),
(37,'user','submitOrderNeedPhone','false','提交订单是否需要手机号码','2021-10-12 21:32:05','2021-10-12 21:32:05','fuint',NULL),
(38,'user','loginNeedPhone','false','登录是否需要手机号','2021-10-12 21:32:05','2021-10-12 21:32:05','fuint',NULL);

/*Table structure for table `mt_sms_sended_log` */

DROP TABLE IF EXISTS `mt_sms_sended_log`;

CREATE TABLE `mt_sms_sended_log` (
  `LOG_ID` int NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `MOBILE_PHONE` varchar(32) DEFAULT NULL COMMENT '手机号',
  `CONTENT` varchar(1024) DEFAULT NULL COMMENT '短信内容',
  `SEND_TIME` datetime DEFAULT NULL COMMENT '发送时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`LOG_ID`),
  KEY `FK_REFERENCE_1` (`MOBILE_PHONE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='短信发送记录表';

/*Data for the table `mt_sms_sended_log` */

insert  into `mt_sms_sended_log`(`LOG_ID`,`MOBILE_PHONE`,`CONTENT`,`SEND_TIME`,`CREATE_TIME`,`UPDATE_TIME`) values 
(1,'18976679980','尊敬的用户，您的[2021年5月份大促销优惠券]已在[解放路店]完成核销，该券消费流水号为[2108132253500002469053]，谢谢您的光临！','2021-08-13 09:53:52','2021-08-13 09:53:52','2021-08-13 09:53:52'),
(2,'18999998889','您的Fuint优惠券账户已注册完成。请您关注Fuint卡券公众号（Fuint卡券系统），在我的优惠券中通过本手机号登录查看。','2021-08-15 23:17:05','2021-08-15 23:17:05','2021-08-15 23:17:05'),
(3,'18999998889','您的Fuint优惠券账户内已收到优惠券1张，总额80.00元。请您关注Fuint公众号（Fuint卡券系统），在我的优惠券中通过本手机号登录查看。','2021-08-15 23:17:05','2021-08-15 23:17:05','2021-08-15 23:17:05'),
(4,'13888889980','您的Fuint优惠券账户已注册完成。请您关注Fuint卡券公众号（Fuint卡券系统），在我的优惠券中通过本手机号登录查看。','2021-08-15 23:17:35','2021-08-15 23:17:35','2021-08-15 23:17:35'),
(5,'13888889980','您的Fuint优惠券账户内已收到优惠券1张，总额20000.00元。请您关注Fuint公众号（Fuint卡券系统），在我的优惠券中通过本手机号登录查看。','2021-08-15 23:17:35','2021-08-15 23:17:35','2021-08-15 23:17:35'),
(6,'18999998880','您的Fuint优惠券账户已注册完成。请您关注Fuint卡券公众号（Fuint卡券系统），在我的优惠券中通过本手机号登录查看。','2021-08-15 23:17:58','2021-08-15 23:17:58','2021-08-15 23:17:58'),
(7,'18999998880','您的Fuint优惠券账户内已收到优惠券1张，总额2000.00元。请您关注Fuint公众号（Fuint卡券系统），在我的优惠券中通过本手机号登录查看。','2021-08-15 23:17:58','2021-08-15 23:17:58','2021-08-15 23:17:58');

/*Table structure for table `mt_sms_template` */

DROP TABLE IF EXISTS `mt_sms_template`;

CREATE TABLE `mt_sms_template` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `UNAME` varchar(50) NOT NULL DEFAULT '' COMMENT '英文名称',
  `CODE` varchar(30) NOT NULL DEFAULT '' COMMENT '编码',
  `CONTENT` varchar(255) NOT NULL DEFAULT '' COMMENT '内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `STATUS` char(1) NOT NULL DEFAULT 'A' COMMENT '状态：A激活；N禁用',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='短信模板';

/*Data for the table `mt_sms_template` */

insert  into `mt_sms_template`(`ID`,`NAME`,`UNAME`,`CODE`,`CONTENT`,`CREATE_TIME`,`UPDATE_TIME`,`OPERATOR`,`STATUS`) values 
(1,'会员登录验证码','login-code','SMS_129758678','您的验证码是：{code}，该验证码仅用于登录验证，请勿泄露给他人使用。','2020-04-18 17:07:18','2020-04-18 17:07:18','sysadmin','A'),
(2,'会员收到优惠券','received-coupon','SMS_187944564','您的Fuint优惠券账户内已收到优惠券{totalNum}张，总额{totalMoney}元。请您关注Fuint公众号（Fuint卡券系统），在我的优惠券中通过本手机号登录查看。','2020-04-19 16:20:27','2020-04-19 16:20:27','sysadmin','A'),
(3,'优惠券被核销','confirm-coupon','SMS_129758679','尊敬的用户，您的[{couponName}]已在[{storeName}]完成核销，该券消费流水号为[{sn}]，谢谢您的光临！','2020-04-18 17:06:25','2020-04-18 17:06:25','sysadmin','A'),
(4,'会员注册完成','register-sms','SMS_129768678','您的Fuint优惠券账户已注册完成。请您关注Fuint卡券公众号（Fuint卡券系统），在我的优惠券中通过本手机号登录查看。','2020-04-18 17:15:11','2020-04-18 17:15:11','sysadmin','A'),
(5,'核销人员审核通过','confirmer-authed','SMS_129756978','{name}，您的店铺核销人员登记已完成审核，可以在{storeName}进行优惠券核销，谢谢！','2020-04-18 17:07:03','2020-04-18 17:07:03','sysadmin','A');

/*Table structure for table `mt_store` */

DROP TABLE IF EXISTS `mt_store`;

CREATE TABLE `mt_store` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '酒店名称',
  `CONTACT` varchar(30) DEFAULT '' COMMENT '联系人姓名',
  `PHONE` varchar(20) DEFAULT '' COMMENT '联系电话',
  `DESCRIPTION` varchar(2000) DEFAULT '' COMMENT '备注信息',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：有效/启用；D：无效',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='酒店表';

/*Data for the table `mt_store` */

insert  into `mt_store`(`ID`,`NAME`,`CONTACT`,`PHONE`,`DESCRIPTION`,`CREATE_TIME`,`UPDATE_TIME`,`STATUS`,`OPERATOR`) values 
(1,'默认店铺','王牟','18976679980','默认店铺8','2021-09-16 22:08:37','2021-09-16 22:08:37','A',''),
(2,'解放路店','李思','18966655525','解放路店','2020-04-26 09:27:22','2021-09-13 06:43:49','A',''),
(3,'永万路店','989','13661634777','777','2021-09-24 03:16:39','2021-10-15 03:33:20','A','');

/*Table structure for table `mt_user` */

DROP TABLE IF EXISTS `mt_user`;

CREATE TABLE `mt_user` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `AVATAR` varchar(255) DEFAULT '' COMMENT '头像',
  `NAME` varchar(30) DEFAULT '' COMMENT '称呼',
  `OPEN_ID` varchar(50) DEFAULT '' COMMENT '微信open_id',
  `MOBILE` varchar(20) DEFAULT '' COMMENT '手机号码',
  `IDCARD` varchar(20) DEFAULT '' COMMENT '证件号码',
  `GRADE_ID` varchar(10) DEFAULT '1' COMMENT '等级ID',
  `START_TIME` datetime DEFAULT NULL COMMENT '会员开始时间',
  `END_TIME` datetime DEFAULT NULL COMMENT '会员结束时间',
  `BALANCE` float(10,2) DEFAULT '0.00' COMMENT '余额',
  `POINT` int DEFAULT '0' COMMENT '积分',
  `SEX` int DEFAULT '0' COMMENT '性别 0男；1女',
  `BIRTHDAY` varchar(20) DEFAULT '' COMMENT '出生日期',
  `CAR_NO` varchar(10) DEFAULT '' COMMENT '车牌号',
  `PASSWORD` varchar(32) DEFAULT '' COMMENT '密码',
  `SALT` varchar(4) DEFAULT '' COMMENT 'salt',
  `ADDRESS` varchar(100) DEFAULT '' COMMENT '地址',
  `CREATE_TIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态，A：激活；N：禁用；D：删除',
  `DESCRIPTION` varchar(255) DEFAULT '' COMMENT '备注信息',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  PRIMARY KEY (`ID`),
  KEY `index_phone` (`MOBILE`)
) ENGINE=InnoDB AUTO_INCREMENT=208 DEFAULT CHARSET=utf8 COMMENT='会员个人信息';

/*Data for the table `mt_user` */

insert  into `mt_user`(`ID`,`AVATAR`,`NAME`,`OPEN_ID`,`MOBILE`,`IDCARD`,`GRADE_ID`,`START_TIME`,`END_TIME`,`BALANCE`,`POINT`,`SEX`,`BIRTHDAY`,`CAR_NO`,`PASSWORD`,`SALT`,`ADDRESS`,`CREATE_TIME`,`UPDATE_TIME`,`STATUS`,`DESCRIPTION`,`OPERATOR`) values 
(0,'','系统','','-',NULL,'2',NULL,NULL,0.00,555,NULL,'2020-11-10',NULL,NULL,NULL,'海口市','2021-03-16 10:24:17','2021-03-16 10:24:17','A','等等',NULL);

/*Table structure for table `mt_user_coupon` */

DROP TABLE IF EXISTS `mt_user_coupon`;

CREATE TABLE `mt_user_coupon` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `CODE` varchar(32) NOT NULL DEFAULT '' COMMENT '编码',
  `TYPE` char(1) NOT NULL DEFAULT 'C' COMMENT '券类型，C优惠券；P预存卡；T集次卡',
  `IMAGE` varchar(100) DEFAULT '' COMMENT '效果图',
  `GROUP_ID` int NOT NULL DEFAULT '0' COMMENT '券组ID',
  `COUPON_ID` int NOT NULL DEFAULT '0' COMMENT '券ID',
  `MOBILE` varchar(20) DEFAULT '' COMMENT '用户手机号码',
  `USER_ID` int DEFAULT '0' COMMENT '用户ID',
  `STORE_ID` int DEFAULT NULL COMMENT '使用店铺ID',
  `AMOUNT` decimal(10,2) DEFAULT '0.00' COMMENT '面额',
  `BALANCE` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `STATUS` char(1) NOT NULL DEFAULT '1' COMMENT '状态：A：未使用；B：已使用；C：已过期; D：已删除；E：未领取',
  `USED_TIME` datetime DEFAULT NULL COMMENT '使用时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `OPERATOR` varchar(30) DEFAULT '' COMMENT '最后操作人',
  `UUID` varchar(50) DEFAULT '' COMMENT '导入UUID',
  `ORDER_ID` int DEFAULT '0' COMMENT '订单ID',
  PRIMARY KEY (`ID`),
  KEY `index_user_id` (`USER_ID`),
  KEY `index_coupon_id` (`COUPON_ID`),
  KEY `index_group_id` (`GROUP_ID`) USING BTREE,
  KEY `index_code` (`CODE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='会员优惠券表';

/*Data for the table `mt_user_coupon` */

/*Table structure for table `mt_user_grade` */

DROP TABLE IF EXISTS `mt_user_grade`;

CREATE TABLE `mt_user_grade` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `GRADE` tinyint DEFAULT '1' COMMENT '等级',
  `NAME` varchar(30) DEFAULT '' COMMENT '等级名称',
  `CATCH_CONDITION` varchar(255) DEFAULT '' COMMENT '升级会员等级条件描述',
  `CATCH_TYPE` varchar(30) DEFAULT 'pay' COMMENT '升级会员等级条件，init:默认获取;pay:付费升级；frequency:消费次数；amount:累积消费金额升级',
  `CATCH_VALUE` int DEFAULT '0' COMMENT '达到升级条件的值',
  `USER_PRIVILEGE` varchar(1000) DEFAULT '' COMMENT '会员权益描述',
  `VALID_DAY` int DEFAULT '0' COMMENT '有效期',
  `DISCOUNT` float(5,2) DEFAULT '0.00' COMMENT '享受折扣',
  `SPEED_POINT` float(5,2) DEFAULT '1.00' COMMENT '积分加速',
  `STATUS` char(1) DEFAULT 'A' COMMENT '状态',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Data for the table `mt_user_grade` */

insert  into `mt_user_grade`(`ID`,`GRADE`,`NAME`,`CATCH_CONDITION`,`CATCH_TYPE`,`CATCH_VALUE`,`USER_PRIVILEGE`,`VALID_DAY`,`DISCOUNT`,`SPEED_POINT`,`STATUS`) values 
(1,1,'普通会员','','init',11,'',0,0.00,1.00,'A'),
(2,2,'铜牌会员','','amount',100,'',0,0.00,1.00,'A'),
(3,3,'银牌会员','22','amount',500,'22',0,0.00,1.00,'A'),
(4,4,'金牌会员','','amount',1000,'',0,0.00,1.00,'A'),
(5,5,'金卡会员','付费升级','pay',199,'1、9折，2、双倍积分',90,9.50,2.00,'A'),
(6,6,'钻卡会员','付费升级','pay',499,'1、8折，2、5倍积分',180,8.00,5.00,'A'),
(7,7,'黑卡会员','付费购买','pay',899,'1、5折\r\n2、10倍积分',365,5.00,10.00,'A'),
(8,8,'至尊会员','付费升级','pay',10000,'买单1折，20倍积分',0,1.00,20.00,'A');

/*Table structure for table `mt_verify_code` */

DROP TABLE IF EXISTS `mt_verify_code`;

CREATE TABLE `mt_verify_code` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `mobile` varchar(16) DEFAULT NULL COMMENT '手机号',
  `verifyCode` char(6) DEFAULT NULL COMMENT '验证码',
  `addTime` datetime DEFAULT NULL COMMENT '创建时间',
  `expireTime` datetime DEFAULT NULL COMMENT '过期时间',
  `usedTime` datetime DEFAULT NULL COMMENT '使用时间',
  `validFlag` char(1) DEFAULT NULL COMMENT '可用状态 0未用 1已用 2置为失效',
  PRIMARY KEY (`id`),
  KEY `ix_mobile_verifyCode` (`mobile`,`verifyCode`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='短信验证码表';

/*Data for the table `mt_verify_code` */

insert  into `mt_verify_code`(`id`,`mobile`,`verifyCode`,`addTime`,`expireTime`,`usedTime`,`validFlag`) values 
(1,'18976679980','218966','2021-09-26 05:49:48','2021-09-26 05:49:48','2021-09-26 05:47:51','1');

/*Table structure for table `t_account` */

DROP TABLE IF EXISTS `t_account`;

CREATE TABLE `t_account` (
  `acct_id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `account_key` varchar(23) NOT NULL DEFAULT '' COMMENT '账户编码',
  `account_name` varchar(20) NOT NULL DEFAULT '' COMMENT '账户名称',
  `password` varchar(100) NOT NULL DEFAULT '' COMMENT '密码',
  `account_status` int NOT NULL DEFAULT '1' COMMENT '0 无效 1 有效',
  `is_active` int NOT NULL DEFAULT '0' COMMENT '0 未激活 1已激活',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `modify_date` datetime NOT NULL COMMENT '修改时间',
  `salt` varchar(64) NOT NULL DEFAULT '' COMMENT '随机码',
  `role_ids` varchar(100) DEFAULT NULL,
  `locked` int NOT NULL DEFAULT '0',
  `owner_id` int DEFAULT NULL COMMENT '所属平台',
  `real_name` varchar(255) DEFAULT NULL,
  `store_id` int DEFAULT NULL COMMENT '管辖店铺id  : -1 代表全部',
  `store_name` varchar(255) DEFAULT NULL COMMENT '管辖店铺名称',
  PRIMARY KEY (`acct_id`),
  KEY `FKmlsqc08c6khxhoed7abkl2s9l` (`owner_id`),
  CONSTRAINT `FKmlsqc08c6khxhoed7abkl2s9l` FOREIGN KEY (`owner_id`) REFERENCES `t_platform` (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Data for the table `t_account` */

insert  into `t_account`(`acct_id`,`account_key`,`account_name`,`password`,`account_status`,`is_active`,`create_date`,`modify_date`,`salt`,`role_ids`,`locked`,`owner_id`,`real_name`,`store_id`,`store_name`) values 
(1,'20160714677851484251776','fuint','ccc975f27323c340234ddfedc056a58cc339a30d',1,1,'2019-10-25 15:54:17','2021-09-19 13:37:48','583c50401a805373','2',0,NULL,'管理员',-1,'全部店铺'),
(2,'20211016340951724856742','admin','24f2e7cef3f97b401c5b56d0020ca6801bae098a',1,1,'2021-10-12 22:19:32','2021-10-17 02:13:50','c534f423472b1838',NULL,0,NULL,'忽而哈茶',-1,'全部店铺'),
(3,'20211016344347831674930','test','40a1078809a86ec17cf728a4f703f3952acfa162',1,1,'2021-10-16 20:39:43','2021-10-17 02:13:30','006bf21394912aee',NULL,0,NULL,'test',-1,'全部店铺');

/*Table structure for table `t_account_duty` */

DROP TABLE IF EXISTS `t_account_duty`;

CREATE TABLE `t_account_duty` (
  `acc_duty_id` int NOT NULL AUTO_INCREMENT COMMENT '账户角色ID',
  `acct_id` int NOT NULL COMMENT '账户ID',
  `duty_id` int NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`acc_duty_id`),
  KEY `FKcym10gcigo2c175iqqjj7xu5h` (`acct_id`),
  KEY `FKpfts0wq2y4xhq9vv2g7uo1kr0` (`duty_id`),
  CONSTRAINT `FKcym10gcigo2c175iqqjj7xu5h` FOREIGN KEY (`acct_id`) REFERENCES `t_account` (`acct_id`),
  CONSTRAINT `FKpfts0wq2y4xhq9vv2g7uo1kr0` FOREIGN KEY (`duty_id`) REFERENCES `t_duty` (`duty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=249 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Data for the table `t_account_duty` */

insert  into `t_account_duty`(`acc_duty_id`,`acct_id`,`duty_id`) values 
(243,1,2),
(247,3,2),
(248,2,2);

/*Table structure for table `t_action_log` */

DROP TABLE IF EXISTS `t_action_log`;

CREATE TABLE `t_action_log` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `action_time` datetime NOT NULL COMMENT '操作时间',
  `time_consuming` decimal(11,0) DEFAULT NULL COMMENT '耗时',
  `client_ip` varchar(50) DEFAULT NULL COMMENT '客户端IP',
  `module` varchar(255) DEFAULT NULL COMMENT '操作模块',
  `url` varchar(255) DEFAULT NULL COMMENT '请求URL',
  `acct_name` varchar(255) NOT NULL COMMENT '操作用户账户',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '用户系统以及浏览器信息',
  `client_port` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Data for the table `t_action_log` */

/*Table structure for table `t_duty` */

DROP TABLE IF EXISTS `t_duty`;

CREATE TABLE `t_duty` (
  `duty_id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `duty_name` varchar(240) DEFAULT NULL COMMENT '角色名称',
  `status` varchar(6) NOT NULL COMMENT '状态(A: 可用  D: 禁用)',
  `description` varchar(400) DEFAULT NULL COMMENT '描述',
  `duty_type` varchar(50) NOT NULL COMMENT '角色类型',
  PRIMARY KEY (`duty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='角色表';

/*Data for the table `t_duty` */

insert  into `t_duty`(`duty_id`,`duty_name`,`status`,`description`,`duty_type`) values 
(2,'系统管理员','A','系统管理员','1');

/*Table structure for table `t_duty_source` */

DROP TABLE IF EXISTS `t_duty_source`;

CREATE TABLE `t_duty_source` (
  `duty_source_id` int NOT NULL AUTO_INCREMENT,
  `duty_id` int DEFAULT NULL,
  `source_id` int DEFAULT NULL,
  PRIMARY KEY (`duty_source_id`),
  KEY `FKlciudb88j4tptc36d43ghl5dg` (`duty_id`),
  KEY `FKp1c59mwxgjue4qdl86sd6dogf` (`source_id`),
  CONSTRAINT `FKlciudb88j4tptc36d43ghl5dg` FOREIGN KEY (`duty_id`) REFERENCES `t_duty` (`duty_id`),
  CONSTRAINT `FKp1c59mwxgjue4qdl86sd6dogf` FOREIGN KEY (`source_id`) REFERENCES `t_source` (`source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5983 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Data for the table `t_duty_source` */

insert  into `t_duty_source`(`duty_source_id`,`duty_id`,`source_id`) values 
(5934,2,54),
(5935,2,108),
(5936,2,18),
(5937,2,83),
(5938,2,103),
(5939,2,111),
(5940,2,90),
(5941,2,84),
(5942,2,100),
(5943,2,6),
(5944,2,19),
(5945,2,5),
(5946,2,78),
(5947,2,9),
(5948,2,107),
(5949,2,71),
(5950,2,104),
(5951,2,112),
(5952,2,85),
(5953,2,1),
(5954,2,16),
(5955,2,97),
(5956,2,80),
(5957,2,17),
(5958,2,91),
(5959,2,50),
(5960,2,102),
(5961,2,110),
(5962,2,109),
(5963,2,61),
(5964,2,96),
(5965,2,11),
(5966,2,3),
(5967,2,7),
(5968,2,79),
(5969,2,101),
(5970,2,12),
(5971,2,2),
(5972,2,4),
(5973,2,106),
(5974,2,49),
(5975,2,69),
(5976,2,113),
(5977,2,10),
(5978,2,15),
(5979,2,86),
(5980,2,105),
(5981,2,81),
(5982,2,93);

/*Table structure for table `t_platform` */

DROP TABLE IF EXISTS `t_platform`;

CREATE TABLE `t_platform` (
  `owner_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(20) NOT NULL COMMENT '平台名称',
  `status` int NOT NULL COMMENT '状态 0 无效 1 有效',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `platform_type` int NOT NULL COMMENT '平台类型 1：免税易购 2：其他体验店',
  PRIMARY KEY (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Data for the table `t_platform` */

insert  into `t_platform`(`owner_id`,`name`,`status`,`description`,`platform_type`) values 
(1,'会员营销管理系统',1,'会员营销管理系统说明',1);

/*Table structure for table `t_source` */

DROP TABLE IF EXISTS `t_source`;

CREATE TABLE `t_source` (
  `source_id` int NOT NULL AUTO_INCREMENT COMMENT '菜单Id',
  `source_name` varchar(240) NOT NULL COMMENT '菜单名称',
  `source_code` varchar(200) NOT NULL COMMENT '菜单对应url',
  `status` varchar(6) NOT NULL COMMENT '状态(A:可用 D:禁用)',
  `source_level` int NOT NULL COMMENT '菜单级别',
  `source_style` varchar(40) NOT NULL COMMENT '样式',
  `is_menu` int NOT NULL COMMENT '是否显示',
  `description` varchar(400) DEFAULT NULL COMMENT '描述',
  `parent_id` int DEFAULT NULL COMMENT '上级菜单ID',
  `is_log` int DEFAULT NULL,
  `icon` varchar(20) DEFAULT NULL COMMENT '菜单图标',
  PRIMARY KEY (`source_id`),
  UNIQUE KEY `SOURCE_NAME` (`source_name`,`parent_id`),
  KEY `FKfcvh926f0p0tey75b7spk8sd3` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='菜单表';

/*Data for the table `t_source` */

insert  into `t_source`(`source_id`,`source_name`,`source_code`,`status`,`source_level`,`source_style`,`is_menu`,`description`,`parent_id`,`is_log`,`icon`) values 
(1,'后台管理员','/user/query','A',2,'3',1,'管理员管理',15,0,NULL),
(2,'新增用户','/user/add','A',3,'99',0,'',1,1,NULL),
(3,'修改用户','/user/edit','A',3,'99',0,'',1,1,NULL),
(4,'删除用户','/user/delete','A',3,'99',0,'',1,1,NULL),
(5,'后台菜单','/source/query','A',2,'1',1,NULL,15,0,NULL),
(6,'新增菜单','/source/add','A',3,'99',0,'',5,1,NULL),
(7,'修改菜单','/source/edit','A',3,'99',0,'修改菜单',5,1,NULL),
(9,'删除菜单','/source/delete','A',3,'99',0,'删除菜单',5,1,NULL),
(10,'平台管理','/platform/query','A',2,'5',0,NULL,15,0,NULL),
(11,'新增平台','/platform/add','A',3,'99',0,'',10,1,NULL),
(12,'删除平台','/platform/delete','A',3,'99',0,'',10,1,NULL),
(15,'系统管理','######','A',1,'99',1,NULL,NULL,0,'cog'),
(16,'后台角色','/duty/query','A',2,'2',1,NULL,15,0,NULL),
(17,'新增角色','/duty/add','A',3,'99',0,'',16,1,NULL),
(18,'修改角色','/source/edit','A',3,'99',0,'',16,1,NULL),
(19,'删除角色','/source/delete','A',3,'99',0,'',16,1,NULL),
(49,'后台日志','/log/query','A',2,'1',1,'后台操作日志',71,0,NULL),
(50,'卡券管理','######','A',1,'3',1,'卡券管理',NULL,0,'money'),
(54,'卡券明细','/backend/member/CouponinfoList','A',2,'3',1,'卡券明细列表',50,1,NULL),
(61,'新增会员','/backend/member/add','A',2,'2',1,'新增会员',79,0,NULL),
(69,'员工管理','/backend/confirmer/queryList','A',2,'3',1,'店铺员工管理',84,0,NULL),
(71,'风控中心','######','A',1,'7',1,'风控中心',NULL,0,'video-camera'),
(78,'发券记录','/backend/sendLog/index','A',2,'2',1,'发券记录',71,0,NULL),
(79,'会员管理','######','A',1,'2',1,'会员管理',NULL,0,'user'),
(80,'会员列表','/backend/member/queryList','A',2,'1',1,'会员列表',79,0,NULL),
(81,'核销流水','/backend/confirmLog/confirmLogList','A',2,'4',1,'核销记录列表',50,0,NULL),
(83,'分组管理','/backend/couponGroup/index','A',2,'1',1,'分组管理',50,0,NULL),
(84,'店铺管理','######','A',1,'1',1,'店铺管理',NULL,0,'columns'),
(85,'新增店铺','/backend/store/add','A',2,'2',0,'新增店铺信息',84,1,NULL),
(86,'店铺列表','/backend/store/queryList','A',2,'1',1,'店铺列表',84,0,NULL),
(90,'短信管理','######','A',1,'6',1,'短信管理',NULL,0,'tablet'),
(91,'已发短信','/backend/smsManager/index','A',2,'1',1,'已发短信列表',90,0,NULL),
(93,'短信模板','/backend/smsTemplate/index','A',2,'0',1,'短信模板',90,0,NULL),
(96,'订单管理','######','A',1,'5',1,'订单管理',NULL,0,'list'),
(97,'转赠记录','/backend/give/index','A',2,'5',1,'转赠记录',50,0,NULL),
(100,'卡券列表','/backend/coupon/index','A',2,'2',1,'卡券列表',50,0,NULL),
(101,'内容管理','######','A',1,'1',1,'内容管理',NULL,1,'book'),
(102,'首页Banner','/backend/banner/queryList','A',2,'0',1,'首页广告',101,1,NULL),
(103,'会员等级','/backend/userGrade/queryList','A',2,'3',1,'会员等级',79,1,NULL),
(104,'积分管理','######','A',1,'6',1,'积分管理',NULL,1,'file'),
(105,'积分明细','/backend/point/index','A',2,'2',1,'积分明细',104,1,NULL),
(106,'积分设置','/backend/point/setting','A',2,'1',1,'积分设置',104,0,NULL),
(107,'订单列表','/backend/order/list','A',2,'1',1,'订单列表',96,0,NULL),
(108,'开卡赠礼','/backend/openGift/list','A',2,'4',1,'开卡礼设置',79,0,NULL),
(109,'售后订单','/backend/refund/index','A',2,'2',1,'售后订单',96,0,NULL),
(110,'会员设置','/backend/member/setting','A',2,'5',1,'会员设置',79,0,NULL),
(111,'商品管理','/backend/goods/goods/list','A',1,'5',1,'商品管理',NULL,0,'shopping-cart'),
(112,'商品分类','/backend/goods/cate/list','A',2,'0',1,'商品分类',111,0,NULL),
(113,'商品列表','/backend/goods/goods/list','A',2,'1',1,'商品列表',111,0,NULL),
(114,'新增','add.do','A',3,'1',0,'banner新增',102,1,'#');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
