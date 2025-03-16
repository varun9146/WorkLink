package com.worklink.utills;

public interface Queries {

	String CHECK_SESSION_VALID = "Select id from session where id=? and expired=0 and userid=? and timeInMilliSeconds=?";
	String UPDATE_SESSION_ACTIVITY = "Update session set LastActiveTime=now() where id=?";
	String CHECK_NUMBER_EXIST =" Select id from labour where Number =?";
	String CHECK_JOBPOSTER_NUMBER_EXIST =" Select id from jobposter where Number =?";
	String SELECT_ARRIALIAS = "select ArrAlias from location where id =?";
	String UPDATE_ALLIAS = "update location set ArrAlias =? where id=?";
	String INSERT_NEW_LOCATION = "insert into location (city,state,country,LatLong,ArrAlias,Validated) values (?,?,?,?,?,?)";
	String INSERT_LABOUR = " insert into labour (FirstName,SecondName,Number,CountryCode,Gender,Dob,Education,Experience,PerDayCharge,Location,Age,CreatedDate,UpdateDate) values (?,?,?,?,?,?,?,?,?,?,?,now(),now())";
	String INSERT_SMS_IN_MESSAGEINTABLE="insert into messagesintable (address, message, msgtype, customdata, event_name, date, time, vendor_id) values (?,?, ?, ?, ?, curdate(), curtime(), ?)";
	String CREATE_SESSION = "Insert into session(UserId,usertype,LoginTime,LastActiveTime)" + "Values(?,?,now(),now())";
	String UPDATE_SESSION_TIME_IN_MILLIS = "Update session set timeInMilliSeconds=? where id=?";
	String UPDATE_SESSION_EXPIRY = "Update session set expired=1 where userid=?";
}
