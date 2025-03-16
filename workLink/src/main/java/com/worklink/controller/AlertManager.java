package com.worklink.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.worklink.utills.AlertMessage;
import com.worklink.utills.Configuration;
import com.worklink.utills.Queries;


public class AlertManager {
	private static Logger logger = Logger.getLogger(AlertManager.class);

	@Autowired
	private Configuration properties;
	
	public void sendSms(String Message, String toAddress,String eventname,int vendorid, Connection con) {
		String toMessage = Message;
		AlertMessage sms = new AlertMessage();
		String body = null;
		if(Message !=null)
		{
		 body += sms.getMessage(toMessage, properties.getSid());
		}	
		try(PreparedStatement ps = con.prepareStatement(Queries.INSERT_SMS_IN_MESSAGEINTABLE, Statement.RETURN_GENERATED_KEYS)){
			ps.setString(1, toAddress);//address
			ps.setString(2, body);//message
			ps.setInt(3, 1);//msgType
			ps.setString(4, "0,0");//customData
			ps.setString(5, eventname);//eventname
			//date and time should be in query
			ps.setInt(6, vendorid);//vendorid
			logger.info("query "+ps.toString());
			int affectedRows = ps.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Could not insert into messagesintable");
			}
			try(ResultSet rs = ps.getGeneratedKeys()){
				if (rs.next()) {
					logger.info("inserted Id "+rs.getInt(1));
					logger.info("Message :- " + Message + " has been sent successfully to " + toAddress);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
