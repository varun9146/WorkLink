package com.worklink.dbconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:dbconfig.properties")
public class DatabaseProperties {

	@Value("${DATABASE_NAME}")
	private String DATABASE_NAME;

	@Value("${DATABASE_HOST}")
	private String DATABASE_HOST;

	@Value("${DATABASE_PORT}")
	private String DATABASE_PORT;

	@Value("${DATABASE_USERNAME}")
	private String DATABASE_USERNAME;

	@Value("${DATABASE_PASSWORD}")
	private String DATABASE_PASSWORD;

	@Value("${MYSQL_DRIVER}")
	private String DATABASE_DRIVER;

	@Value("${MINIMUM_IDEL}")
	private Integer MINIMUM_IDEL;

	@Value("${MAXIMUM_CONN_SIZE}")
	private Integer MAXIMUM_CONN_SIZE;

	public String getDATABASE_NAME() {
		return DATABASE_NAME;
	}

	public String getDATABASE_HOST() {
		return DATABASE_HOST;
	}

	public String getDATABASE_PORT() {
		return DATABASE_PORT;
	}

	public String getDATABASE_USERNAME() {
		return DATABASE_USERNAME;
	}

	public String getDATABASE_PASSWORD() {
		return DATABASE_PASSWORD;
	}

	public String getDATABASE_DRIVER() {
		return DATABASE_DRIVER;
	}

	public Integer getMINIMUM_IDEL() {
		return MINIMUM_IDEL;
	}

	public Integer getMAXIMUM_CONN_SIZE() {
		return MAXIMUM_CONN_SIZE;
	}

}
