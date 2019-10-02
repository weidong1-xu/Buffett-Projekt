/*创建数据库*/
CREATE DATABASE stock_selector;

/*进入数据库*/
USE stock_selector;

/*查看全部表*/
SHOW TABLES;




/* ------------------------------------------------------------------ */
/*股票基本信息表*/
CREATE TABLE tab_stock_info
(
	stock_id BIGINT PRIMARY KEY AUTO_INCREMENT,
	stock_code VARCHAR(6) UNIQUE,
	stock_name VARCHAR(8)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看股票基本信息表表结构*/
DESC tab_stock_info;

/*查看股票基本信息表数据量*/
SELECT COUNT(stock_code) FROM tab_stock_info;

/*查看全部股票基本信息数据*/
SELECT * FROM tab_stock_info;




/* ------------------------------------------------------------------ */
/*日历翻牌表*/
CREATE TABLE tab_daily
(
	current_data_date DATE
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看日历翻牌表表结构*/
DESC tab_daily;

/*查看日历翻牌表数据*/
SELECT current_date FROM tab_daily;




/* ------------------------------------------------------------------ */
/*股票价格信息表上证0001表*/
CREATE TABLE tab_stock_price_shangzheng_0001
(
	stock_id BIGINT,
	stock_price_date VARCHAR(10),
	stock_price_start_price DECIMAL(18,2),
	stock_price_end_price DECIMAL(18,2),
	stock_price_highest_price DECIMAL(18,2),
	stock_price_lowest_price DECIMAL(18,2),
	stock_price_volume INT(10),
	stock_price_turnover INT(10),
	stock_price_amplitude VARCHAR(10),
	stock_price_turnover_rate DECIMAL(4,1),
	PRIMARY KEY (stock_id , stock_price_date),
	CONSTRAINT fk_stock_shangzheng_001_price_info FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看股票价格信息表表结构*/
DESC tab_stock_price_shangzheng_0001;

/*查看股票价格信息表数据量*/
SELECT COUNT(*) FROM tab_stock_price_shangzheng_0001;

/*查看部分股票价格信息表部分数据*/
SELECT * FROM tab_stock_price_shangzheng_0001 LIMIT 10;

/*删除全部数据*/
DELETE FROM tab_stock_price_shangzheng_0001;

/*删除表格*/
DROP TABLE tab_stock_price_shangzheng_0001;


/*---------------------------------------*/
/*股票价格信息表上证0002表*/
CREATE TABLE tab_stock_price_shangzheng_0002
(
	stock_id BIGINT,
	stock_price_date VARCHAR(10),
	stock_price_start_price DECIMAL(18,2),
	stock_price_end_price DECIMAL(18,2),
	stock_price_highest_price DECIMAL(18,2),
	stock_price_lowest_price DECIMAL(18,2),
	stock_price_volume INT(10),
	stock_price_turnover INT(10),
	stock_price_amplitude VARCHAR(10),
	stock_price_turnover_rate DECIMAL(4,1),
	PRIMARY KEY (stock_id , stock_price_date),
	CONSTRAINT fk_stock_shangzheng_002_price_info FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看股票价格信息表表结构*/
DESC tab_stock_price_shangzheng_0002;

/*查看股票价格信息表数据量*/
SELECT COUNT(*) FROM tab_stock_price_shangzheng_0002;

/*查看部分股票价格信息表部分数据*/
SELECT * FROM tab_stock_price_shangzheng_0002 LIMIT 10;


/*删除全部数据*/
DELETE FROM tab_stock_price_shangzheng_0002;

/*删除表格*/
DROP TABLE tab_stock_price_shangzheng_0002;



/*---------------------------------------*/
/*股票价格信息表上证0003表*/
CREATE TABLE tab_stock_price_shangzheng_0003
(
	stock_id BIGINT,
	stock_price_date VARCHAR(10),
	stock_price_start_price DECIMAL(18,2),
	stock_price_end_price DECIMAL(18,2),
	stock_price_highest_price DECIMAL(18,2),
	stock_price_lowest_price DECIMAL(18,2),
	stock_price_volume INT(10),
	stock_price_turnover INT(10),
	stock_price_amplitude VARCHAR(10),
	stock_price_turnover_rate DECIMAL(4,1),
	PRIMARY KEY (stock_id , stock_price_date),
	CONSTRAINT fk_stock_shangzheng_003_price_info FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看股票价格信息表表结构*/
DESC tab_stock_price_shangzheng_0003;

/*查看股票价格信息表数据量*/
SELECT COUNT(*) FROM tab_stock_price_shangzheng_0003;

/*查看部分股票价格信息表部分数据*/
SELECT * FROM tab_stock_price_shangzheng_0003 LIMIT 10;

/*删除全部数据*/
DELETE FROM tab_stock_price_shangzheng_0003;

/*删除表格*/
DROP TABLE tab_stock_price_shangzheng_0003;



/*---------------------------------------*/
/*股票价格信息表深证0001表*/
CREATE TABLE tab_stock_price_shenzheng_0001
(
	stock_id BIGINT,
	stock_price_date  VARCHAR(10),
	stock_price_start_price DECIMAL(18,2),
	stock_price_end_price DECIMAL(18,2),
	stock_price_highest_price DECIMAL(18,2),
	stock_price_lowest_price DECIMAL(18,2),
	stock_price_volume INT(10),
	stock_price_turnover INT(10),
	stock_price_amplitude VARCHAR(10),
	stock_price_turnover_rate DECIMAL(4,1),
	PRIMARY KEY (stock_id , stock_price_date),
	CONSTRAINT fk_stock_shenzheng_001_price_info FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看股票价格信息表表结构*/
DESC tab_stock_price_shenzheng_0001;

/*查看股票价格信息表数据量*/
SELECT COUNT(*) FROM tab_stock_price_shenzheng_0001;

/*查看部分股票价格信息表部分数据*/
SELECT * FROM tab_stock_price_shenzheng_0001 LIMIT 10;


/*删除全部数据*/
DELETE FROM tab_stock_price_shenzheng_0001;

/*删除表格*/
DROP TABLE tab_stock_price_shenzheng_0001;



/*---------------------------------------*/
/*股票价格信息表深证0002表*/
CREATE TABLE tab_stock_price_shenzheng_0002
(
	stock_id BIGINT,
	stock_price_date VARCHAR(10),
	stock_price_start_price DECIMAL(18,2),
	stock_price_end_price DECIMAL(18,2),
	stock_price_highest_price DECIMAL(18,2),
	stock_price_lowest_price DECIMAL(18,2),
	stock_price_volume INT(10),
	stock_price_turnover INT(10),
	stock_price_amplitude VARCHAR(10),
	stock_price_turnover_rate DECIMAL(4,1),
	PRIMARY KEY (stock_id , stock_price_date),
	CONSTRAINT fk_stock_shenzheng_002_price_info FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看股票价格信息表表结构*/
DESC tab_stock_price_shenzheng_0002;

/*查看股票价格信息表数据量*/
SELECT COUNT(*) FROM tab_stock_price_shenzheng_0002;

/*查看部分股票价格信息表部分数据*/
SELECT * FROM tab_stock_price_shenzheng_0002 LIMIT 10;


/*删除全部数据*/
DELETE FROM tab_stock_price_shenzheng_0002;

/*删除表格*/
DROP TABLE tab_stock_price_shenzheng_0002;


/*---------------------------------------*/
/*股票价格信息表深证0003表*/
CREATE TABLE tab_stock_price_shenzheng_0003
(
	stock_id BIGINT,
	stock_price_date VARCHAR(10),
	stock_price_start_price DECIMAL(18,2),
	stock_price_end_price DECIMAL(18,2),
	stock_price_highest_price DECIMAL(18,2),
	stock_price_lowest_price DECIMAL(18,2),
	stock_price_volume INT(10),
	stock_price_turnover INT(10),
	stock_price_amplitude VARCHAR(10),
	stock_price_turnover_rate DECIMAL(4,1),
	PRIMARY KEY (stock_id , stock_price_date),
	CONSTRAINT fk_stock_shenzheng_003_price_info FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看股票价格信息表表结构*/
DESC tab_stock_price_shenzheng_0003;

/*查看股票价格信息表数据量*/
SELECT COUNT(*) FROM tab_stock_price_shenzheng_0003;

/*查看部分股票价格信息表部分数据*/
SELECT * FROM tab_stock_price_shenzheng_0003 LIMIT 10;

/*删除全部数据*/
DELETE FROM tab_stock_price_shenzheng_0003;

/*删除表格*/
DROP TABLE tab_stock_price_shenzheng_0003;

/* ------------------------------------------------------------------ */































