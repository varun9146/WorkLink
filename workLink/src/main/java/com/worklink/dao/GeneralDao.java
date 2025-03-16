package com.worklink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.DateTimeException;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.worklink.controller.AlertManager;
import com.worklink.model.Education;
import com.worklink.utills.ErrorResponseMessages;
import com.worklink.utills.Keys;
import com.worklink.utills.Queries;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@Component
public class GeneralDao {
	private static Logger logger = Logger.getLogger(GeneralDao.class);

	@Autowired
	private ErrorResponseMessages message;

	public void executeUpdate(String query, Connection con) {
		logger.info("Inside GeneralDao:ExecuteUpdate:");
		try (Statement stmt = con.createStatement()) {
			if (stmt.executeUpdate(query) > 0) {
				logger.info("Inside GeneralDao:ExecuteUpdate: Updated");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void executeUpdate(String query, Connection con, Object... args) {
		logger.info("Inside GeneralDao:ExecuteUpdate:");
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg instanceof String) {
					stmt.setString(i + 1, (String) arg);
				} else if (arg instanceof Integer) {
					stmt.setInt(i + 1, (Integer) arg);
				} else if (arg instanceof Double) {
					stmt.setDouble(i + 1, (Double) arg);
				} else if (arg instanceof Boolean) {
					stmt.setBoolean(i + 1, (Boolean) arg);
				}
			}
			if (stmt.executeUpdate() > 0) {
				logger.info("Inside GeneralDao:ExecuteUpdate: Updated");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isEmpty(JSONObject test) {
		if (test == null || test.toString().equalsIgnoreCase("") || test.toString() == ""
				|| test.toString().equalsIgnoreCase("null") || test.toString().equalsIgnoreCase("{}")) {
			return true;
		}
		return false;
	}

	public boolean isEmpty(String test) {
		if (test == null || test == "") {
			return true;
		}
		return false;
	}

	public static boolean isJSONValid(String jsonString) {
		logger.info("request string is  " + jsonString);
		boolean valid = true;
		JSONObject obj = new JSONObject();
		try {
			obj = (JSONObject) JSONSerializer.toJSON(jsonString);
			return valid;
		} catch (JSONException e) {
			valid = false;
			return valid;
		} catch (ClassCastException e) {
			valid = false;
			return valid;
		} catch (Exception e) {
			valid = false;
			logger.error("Exception", e);
			return valid;
		}
	}

	public boolean isValidWorkRange(YearMonth workFrom, YearMonth workUntil) {
		// Check if workFrom is less than or equal to workUntil
		return !workFrom.isAfter(workUntil);
	}

	public YearMonth parseToYearMonth(String input) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
		return YearMonth.parse(input, formatter);
	}

	/*
	 * public boolean isGivenDateBetweenTwoDates(YearMonth startTarget,YearMonth
	 * endTarget, YearMonth start, YearMonth end) {
	 * logger.info("target "+target+" start "+start+" end "+end); return
	 * target.isBefore(start) && !target.isAfter(end); }
	 */
	public boolean isGivenDateBetweenTwoDates(YearMonth startTarget, YearMonth endTarget, YearMonth start,
			YearMonth end) {
		// Log the values for debugging
		logger.info("startTarget " + startTarget + " endTarget " + endTarget + " start " + start + " end " + end);

		// Check if startTarget is before or equal to endTarget
		boolean condition1 = startTarget.isBefore(endTarget) || startTarget.equals(endTarget);

		// Check if target is after start and before end
		boolean condition2 = start.isBefore(end) && startTarget.isBefore(end) && endTarget.isAfter(start)
				&& endTarget.isBefore(end);

		// Return true if both conditions are met
		return condition1 && condition2;
	}

	public String validateCgpaString(String cgpaString) {
		// Split the CGPA string into numerator and denominator
		String[] parts = cgpaString.split("/");

		if (parts.length != 2) {
			return message.getMessage("INCORRECT_CGPA_FORMAT");
		}
		Float numerator = Float.parseFloat(parts[0].trim());
		int denominator = Integer.parseInt(parts[1].trim());

		// Check if numerator is less than denominator
		if (numerator > denominator) {
			return message.getMessage("INCORRECT_CGPA_MARKS");
		}

		return message.getMessage("SUCCESS");
	}

	public boolean validateYearFormat(String year) {
		try {
			// Try parsing as Year to check for valid year format
			Year.parse(year, DateTimeFormatter.ofPattern("yyyy"));
			return true;
		} catch (DateTimeException e) {
			return false; // Return false if parsing fails
		}
	}

	public boolean validateMonthYearFormat(String monthYear) {
		try {
			// Try parsing as Year to check for valid year format
			YearMonth.parse(monthYear, DateTimeFormatter.ofPattern("MMM yyyy"));
			return true;
		} catch (DateTimeException e) {
			return false; // Return false if parsing fails
		}
	}

	public String encode(String s) {
		if (s == null)
			return null;
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(s);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\'') {
				result.append("&#039;");
			} else if (character == '&') {
				result.append("&amp;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public void sendSms(String message, String mobile, String eventname, int vendorid, Connection con) {
		try {
			AlertManager manager = new AlertManager();
			manager.sendSms(message, mobile, eventname, vendorid, con);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Integer createSession(int id, String type, Connection conn) {
		logger.info("Inside LogInDao:createSession:");
		try (PreparedStatement pst = conn.prepareStatement(Queries.CREATE_SESSION, Statement.RETURN_GENERATED_KEYS)) {
			pst.setInt(1, id);
			pst.setString(2, type);
			pst.executeUpdate();
			try (ResultSet rs = pst.getGeneratedKeys()) {
				return rs.next() ? rs.getInt(1) : 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	public Integer updateSessionTimeInMillis(int sessionId,int TimeInMillis,Connection conn) {
		logger.info("Inside updateSessionExpiry:");
		try(PreparedStatement pst=conn.prepareStatement(Queries.UPDATE_SESSION_TIME_IN_MILLIS)) {
			pst.setInt(1, TimeInMillis);
			pst.setInt(2, sessionId);
			int id=pst.executeUpdate();
			return id;
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public Integer updateSessionExpiry(int userId,Connection conn) {
		logger.info("Inside updateSessionExpiry:");
		try(PreparedStatement pst=conn.prepareStatement(Queries.UPDATE_SESSION_EXPIRY)) {
			pst.setInt(1, userId);
			int id=pst.executeUpdate();
			return id;
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public List<Education> getEducation(String qquery,Connection conn){
		try(PreparedStatement ps =conn.prepareStatement(qquery)){
			try(ResultSet rs=ps.executeQuery()){
				List<Education> edu=new ArrayList<>();
				while(rs.next()) {
					Education e=new Education();
					e.setId(rs.getInt(Keys.id));
					e.setEducation(rs.getString(Keys.QUALIFICATION) +"( "+rs.getString(Keys.SPECIALIZATION)+" )");
					edu.add(e);
				}
				return edu;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
