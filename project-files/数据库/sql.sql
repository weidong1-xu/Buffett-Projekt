/*删除当前库*/
DROP DATABASE stock_selector;

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
	stock_code VARCHAR(10) UNIQUE,
	stock_name VARCHAR(8)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看股票基本信息表表结构*/
DESC tab_stock_info;

/*查看股票基本信息表数据量*/
SELECT COUNT(stock_code) FROM tab_stock_info;

/*查看全部股票基本信息数据*/
SELECT * FROM tab_stock_info;

SELECT * FROM tab_stock_info WHERE stock_id = '84';


/*删除表*/
DROP TABLE tab_stock_info;



/* ------------------------------------------------------------------ */
/*日历翻牌表*/
CREATE TABLE tab_daily
(
	current_data_date VARCHAR(10)
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
/*算法信息表*/
CREATE TABLE tab_algorithm_info
(
	algorithm_id BIGINT PRIMARY KEY AUTO_INCREMENT,
	algorithm_name VARCHAR(50),
	algorithm_class_name VARCHAR(255),
	algorithm_day_count VARCHAR(4),
	algorithm_comment VARCHAR(255),
	update_date VARCHAR(10)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看算法信息表表结构*/
DESC tab_algorithm_info;

/*查看算法信息表数据量*/
SELECT COUNT(algorithm_id) FROM tab_algorithm_info;

/*查看算法信息表全部数据*/
SELECT * FROM tab_algorithm_info;

/*删除算法信息表全部数据*/
DELETE FROM tab_algorithm_info;

/*删除表*/
DROP TABLE tab_algorithm_info;


/* ------------------------------------------------------------------ */
/*算法结果表001*/
CREATE TABLE tab_algorithm_result_001
(
	algorithm_id BIGINT,
	run_date VARCHAR(10),
	stock_id BIGINT,
	is_success VARCHAR(10) DEFAULT 'UNKNOWN',
	success_rate VARCHAR(10),
	PRIMARY KEY (algorithm_id , run_date , stock_id),
	CONSTRAINT fk_algorithm_result_info_001 FOREIGN KEY (algorithm_id) REFERENCES tab_algorithm_info (algorithm_id),
	CONSTRAINT fk_algorithm_result_stockInfo_001 FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看算法信息表表结构*/
DESC tab_algorithm_result_001;

/*查看算法信息表数据量*/
SELECT COUNT(*) FROM tab_algorithm_result_001;

/*查看算法信息表全部数据*/
SELECT * FROM tab_algorithm_result_001;

/*删除算法信息表全部数据*/
DELETE FROM tab_algorithm_result_001;

/*删除当前表*/
DROP TABLE tab_algorithm_result_001;
/* ------------------------------------------------------------------ */
/*算法结果表002*/
CREATE TABLE tab_algorithm_result_002
(
	algorithm_id BIGINT,
	run_date VARCHAR(10),
	stock_id BIGINT,
	is_success VARCHAR(10) DEFAULT 'UNKNOWN',
	success_rate VARCHAR(10),
	PRIMARY KEY (algorithm_id , run_date , stock_id),
	CONSTRAINT fk_algorithm_result_info_002 FOREIGN KEY (algorithm_id) REFERENCES tab_algorithm_info (algorithm_id),
	CONSTRAINT fk_algorithm_result_stockInfo_002 FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看算法信息表表结构*/
DESC tab_algorithm_result_002;

/*查看算法信息表数据量*/
SELECT COUNT(*) FROM tab_algorithm_result_002;

/*查看算法信息表全部数据*/
SELECT * FROM tab_algorithm_result_002;

/*删除算法信息表全部数据*/
DELETE FROM tab_algorithm_result_002;

/*删除当前表*/
DROP TABLE tab_algorithm_result_002;
/* ------------------------------------------------------------------ */
/*算法结果表003*/
CREATE TABLE tab_algorithm_result_003
(
	algorithm_id BIGINT,
	run_date VARCHAR(10),
	stock_id BIGINT,
	is_success VARCHAR(10) DEFAULT 'UNKNOWN',
	success_rate VARCHAR(10),
	PRIMARY KEY (algorithm_id , run_date , stock_id),
	CONSTRAINT fk_algorithm_result_info_003 FOREIGN KEY (algorithm_id) REFERENCES tab_algorithm_info (algorithm_id),
	CONSTRAINT fk_algorithm_result_stockInfo_003 FOREIGN KEY (stock_id) REFERENCES tab_stock_info (stock_id)
)
ENGINE=INNODB DEFAULT CHARSET=UTF8;

/*查看算法信息表表结构*/
DESC tab_algorithm_result_003;

/*查看算法信息表数据量*/
SELECT COUNT(*) FROM tab_algorithm_result_003;

/*查看算法信息表全部数据*/
SELECT * FROM tab_algorithm_result_003;

/*删除算法信息表全部数据*/
DELETE FROM tab_algorithm_result_003;

/*删除当前表*/
DROP TABLE tab_algorithm_result_003;
/* ------------------------------------------------------------------ */
/*初始化信息*/
INSERT INTO tab_algorithm_info (algorithm_name , algorithm_class_name , algorithm_day_count , algorithm_comment)
VALUES
('圆弧底算法' , 'com.hanslv.stock.selector.algorithm.Algorithm0001' , '5' , '通过抛物线函数方程计算符合要求的股票信息');
UPDATE tab_algorithm_info SET update_date = null WHERE algorithm_id = 1;
SELECT * FROM tab_algorithm_info;









/* ------------------------------------------------------------------ */
/*测试数据*/
INSERT INTO tab_algorithm_info
(algorithm_name , algorithm_comment , update_date)
VALUES
('test-algorithm' , '测试算法' , '2019-10-05');


SELECT * FROM tab_algorithm_info;


INSERT INTO tab_algorithm_result_001
(algorithm_id , run_date , stock_id , success_rate)
VALUES
(1 , '2018-04-04' , '1434' , '0');

INSERT INTO tab_algorithm_result_001
(algorithm_id , run_date , stock_id , success_rate)
VALUES
(1 , '2018-05-04' , '1434' , '0');

INSERT INTO tab_algorithm_result_003
(algorithm_id , run_date , stock_id , success_rate)
VALUES
(1 , '2018-05-11' , '1434' , '0');

SELECT * FROM tab_algorithm_result_001;

SELECT stock_id , stock_price_date , /*stock_price_start_price , stock_price_end_price , *//*stock_price_highest_price , stock_price_lowest_price , */stock_price_volume /*, stock_price_turnover , *//*stock_price_amplitude*//* , stock_price_turnover_rate*/ 
FROM tab_stock_price_shangzheng_0001 WHERE stock_id = '1'
UNION
SELECT stock_id , stock_price_date , /*stock_price_start_price , stock_price_end_price , *//*stock_price_highest_price , stock_price_lowest_price , */stock_price_volume /*, stock_price_turnover , *//*stock_price_amplitude*//* , stock_price_turnover_rate*/ 
FROM tab_stock_price_shangzheng_0002 WHERE stock_id = '1'
UNION
SELECT stock_id , stock_price_date , /*stock_price_start_price , stock_price_end_price , *//*stock_price_highest_price , stock_price_lowest_price , */stock_price_volume /*, stock_price_turnover , *//*stock_price_amplitude*//* , stock_price_turnover_rate*/ 
FROM tab_stock_price_shangzheng_0003 WHERE stock_id = '1'
UNION
SELECT stock_id , stock_price_date , /*stock_price_start_price , stock_price_end_price , *//*stock_price_highest_price , stock_price_lowest_price , */stock_price_volume /*, stock_price_turnover , *//*stock_price_amplitude*//* , stock_price_turnover_rate*/ 
FROM tab_stock_price_shenzheng_0001 WHERE stock_id = '1'
UNION
SELECT stock_id , stock_price_date , /*stock_price_start_price , stock_price_end_price , *//*stock_price_highest_price , stock_price_lowest_price , */stock_price_volume /*, stock_price_turnover , *//*stock_price_amplitude*//* , stock_price_turnover_rate*/ 
FROM tab_stock_price_shenzheng_0002 WHERE stock_id = '1'
UNION
SELECT stock_id , stock_price_date , /*stock_price_start_price , stock_price_end_price , *//*stock_price_highest_price , stock_price_lowest_price , */stock_price_volume /*, stock_price_turnover , *//*stock_price_amplitude*//* , stock_price_turnover_rate*/ 
FROM tab_stock_price_shenzheng_0003 WHERE stock_id = '1'
ORDER BY stock_price_date ASC;
















