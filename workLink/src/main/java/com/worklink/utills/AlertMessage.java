package com.worklink.utills;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.worklink.dao.GeneralDao;
import com.worklink.model.AlertMessageData;


@Component
public class AlertMessage {

	@Autowired
	private ErrorResponseMessages message;

	@Autowired
	private Configuration properties;
	
	@Autowired
	private GeneralDao gDao;
	
	private String fromAddress = "";
	private String title = "";
	private String body = "";

	public String addAddress(AlertMessageData alertMessage) {
		String toAddress = "";

		String toString = constructToXmlTagsForPerson(alertMessage);
		toAddress += message.getMessage("to_open_tag");
		toAddress += toString;
		toAddress += message.getMessage("to_close_tag");
		String toStr = getAddressAsXml(toAddress);

		this.fromAddress = message.getMessage("from_open_tag") + message.getMessage("name_open_tag")
				+ "admin" + message.getMessage("name_close_tag") + message.getMessage("number_open_tag")
				+ "0000" + message.getMessage("number_close_tag")
				+ message.getMessage("from_close_tag");

		String Address = toStr + fromAddress;
		return Address;

	}

	private String constructToXmlTagsForPerson(AlertMessageData alertMessage) {

		String ret = "";

		ret += alertMessage.getToNumber() != null ? message.getMessage("number_open_tag") + "+" + alertMessage.getCc() + "." + gDao.encode(alertMessage.getToNumber()) + message.getMessage("number_close_tag") : message.getMessage("number_open_tag") + message.getMessage("number_close_tag");
		ret += alertMessage.getPassword() != null ? message.getMessage("password_open_tag") + alertMessage.getPassword() + message.getMessage("password_close_tag") : message.getMessage("password_open_tag") + message.getMessage("password_close_tag");
		ret += message.getMessage("email_open_tag") + properties.getServerMail() + message.getMessage("email_close_tag");
		ret += message.getMessage("prefix_open_tag") + "mr." + message.getMessage("prefix_close_tag");
		ret += message.getMessage("contact_person_prefix_open_tag") + "mr." + message.getMessage("conatct_person_prefix_close_tag");

		return ret;
	}

	public String getAddressAsXml(String toAddress) {
		String address = message.getMessage("to_list_open_tag") + toAddress
				+ message.getMessage("to_list_close_tag");

		return address;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage(String body, String sid) {
		String msg = message.getMessage("sid_open_tag") + sid + message.getMessage("sid_close_tag");

		msg += message.getMessage("short_text_open_tag") + gDao.encode(body)
				+ message.getMessage("short_text_close_tag");

		return msg;
	}

}
