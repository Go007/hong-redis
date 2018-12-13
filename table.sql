CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT(20) NOT NULL, -- 订单ID
  `name` VARCHAR(128), -- 订单名称 其他业务熟悉忽略
  `password` BIGINT(20) , -- 消息唯一ID
  `count` BIGINT(100) DEFAULT 0, -- 订单状态 1 支付中   2 支付成功  3 取消订单
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;