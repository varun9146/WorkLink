package com.worklink.dbconfig;

import javax.sql.DataSource;

//import org.apache.log4j.Logger;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

@Configuration
public class ConnectionConfig {

	private static final Logger logger = Logger.getLogger(ConnectionConfig.class);

	@Bean
	public HikariConfig config(DatabaseProperties dbproperties) {
		HikariConfig config = new HikariConfig();
		String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", dbproperties.getDATABASE_HOST(),
				dbproperties.getDATABASE_PORT(), dbproperties.getDATABASE_NAME());
		logger.info("JDBC URL is " + jdbcUrl);
		config.setDriverClassName(dbproperties.getDATABASE_DRIVER());
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(dbproperties.getDATABASE_USERNAME());
		config.setPassword(dbproperties.getDATABASE_PASSWORD());
		config.setMinimumIdle(dbproperties.getMINIMUM_IDEL());
		config.setMaximumPoolSize(dbproperties.getMAXIMUM_CONN_SIZE());
		return config;
	}

	@Bean
	public DataSource getDataSource(HikariConfig config) {
		logger.info("Datasource bean created");
		return new HikariDataSource(config);
	}

	@Bean
	public HikariPoolMXBean hikariPoolMXBean(HikariDataSource hikariDataSource) {
		return hikariDataSource.getHikariPoolMXBean();
	}
}
