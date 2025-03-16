package com.worklink.dbconfig;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariPoolMXBean;

@Component
public class ConnectionPool {

	public static final Logger logger = Logger.getLogger(ConnectionPool.class);

	@Autowired
	DataSource dataSource;

	@Autowired
	HikariPoolMXBean hikariPoolMXBean;

	public Connection getConnection() throws SQLException {
		logger.info("Max idle connections is " + hikariPoolMXBean.getIdleConnections() + " Total Connections are "
				+ hikariPoolMXBean.getTotalConnections() + " And Thread Awaiting Connection are "
				+ hikariPoolMXBean.getThreadsAwaitingConnection());
		Connection conn = dataSource.getConnection();
		if (conn != null) {
			return conn;
		}
		throw new SQLException("MYSQL-CONNECTION-FAILED");
	}
}
