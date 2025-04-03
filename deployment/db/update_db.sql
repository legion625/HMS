-- 0.5.3 -> 0.6.0
CREATE TABLE `sys_attr` (
  `uid` varchar(45) NOT NULL,
  `type_idx` tinyint DEFAULT NULL,
  `attr_key` varchar(45) DEFAULT NULL,
  `attr_value` varchar(45) DEFAULT NULL,
  `object_create_time` bigint DEFAULT NULL,
  `object_update_time` bigint DEFAULT NULL,
  PRIMARY KEY (`uid`)
) ;

CREATE TABLE `system_seq` (
  `item_id` varchar(45) NOT NULL,
  `current_num` bigint DEFAULT NULL,
  `last_num` bigint DEFAULT NULL,
  `max_num` bigint DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ;

-- hms


-- 0.6.1 -> unstaging
ALTER TABLE `consumption` 
CHANGE COLUMN `object_create_time` `object_create_time_ld_str` VARCHAR(45) NOT NULL ,
CHANGE COLUMN `object_update_time` `object_update_time_ld_str` VARCHAR(45) NOT NULL ;

ALTER TABLE `consumption` 
ADD COLUMN `object_create_time` BIGINT UNSIGNED NULL AFTER `object_update_time_ld_str`,
ADD COLUMN `object_update_time` BIGINT UNSIGNED NULL AFTER `object_create_time`;

update consumption set object_create_time = UNIX_TIMESTAMP(STR_TO_DATE(object_create_time_ld_str, '%Y-%m-%dT%H:%i:%s.%f'))*1000, object_update_time = UNIX_TIMESTAMP(STR_TO_DATE(object_update_time_ld_str, '%Y-%m-%dT%H:%i:%s.%f'))*1000;

ALTER TABLE `payment` 
CHANGE COLUMN `object_create_time` `object_create_time_ld_str` VARCHAR(45) NOT NULL ,
CHANGE COLUMN `object_update_time` `object_update_time_ld_str` VARCHAR(45) NOT NULL ;

ALTER TABLE `payment` 
ADD COLUMN `object_create_time` BIGINT UNSIGNED NULL AFTER `object_update_time_ld_str`,
ADD COLUMN `object_update_time` BIGINT UNSIGNED NULL AFTER `object_create_time`;

update payment set object_create_time = UNIX_TIMESTAMP(STR_TO_DATE(object_create_time_ld_str, '%Y-%m-%dT%H:%i:%s.%f'))*1000, object_update_time = UNIX_TIMESTAMP(STR_TO_DATE(object_update_time_ld_str, '%Y-%m-%dT%H:%i:%s.%f'))*1000;


-- hms_dev