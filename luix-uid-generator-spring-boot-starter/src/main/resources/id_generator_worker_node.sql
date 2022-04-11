CREATE TABLE %s
(
id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
app_id VARCHAR(64) NOT NULL COMMENT 'application ID',
host_name VARCHAR(64) NOT NULL COMMENT 'host name',
port VARCHAR(64) NOT NULL COMMENT 'port',
type INT NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
uptime DATE NOT NULL COMMENT 'uptime',
created_time datetime NOT NULL COMMENT 'created time',
PRIMARY KEY(id)
)
COMMENT='Worker assigner for ID generator',ENGINE = INNODB;
