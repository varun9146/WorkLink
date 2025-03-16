package com.worklink.utills;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class Configuration {

	@Value("${location_api_URL}")
	private String Location_API_URL;

	@Value("${location_api_key}")
	private String Location_API_Key;
	
	@Value("${OTP_QA}")
	private String OTP_QA;
	
	@Value("${serverMail}")
	private String serverMail;
	
	@Value("${sid}")
	private String sid;

	@Value("${labour_type}")
	private String labourType;
	
	@Value("${job_poster_type}")
	private String jobPosterType;
	
	public String getLocation_API_URL() {
		return Location_API_URL;
	}

	public void setLocation_API_URL(String location_API_URL) {
		Location_API_URL = location_API_URL;
	}

	public String getLocation_API_Key() {
		return Location_API_Key;
	}

	public void setLocation_API_Key(String location_API_Key) {
		Location_API_Key = location_API_Key;
	}

	public String getOTP_QA() {
		return OTP_QA;
	}

	public void setOTP_QA(String oTP_QA) {
		OTP_QA = oTP_QA;
	}

	public String getServerMail() {
		return serverMail;
	}

	public void setServerMail(String serverMail) {
		this.serverMail = serverMail;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getLabourType() {
		return labourType;
	}

	public void setLabourType(String labourType) {
		this.labourType = labourType;
	}

	public String getJobPosterType() {
		return jobPosterType;
	}

	public void setJobPosterType(String jobPosterType) {
		this.jobPosterType = jobPosterType;
	}
	
}
