package com.worklink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.worklink.exception.DataRetrievalFailedExceptiom;
import com.worklink.utills.ErrorResponseMessages;
import com.worklink.utills.Keys;
import com.worklink.utills.Queries;
import com.worklink.utills.Restutills;

@Component
public class LocationDao {

	private final ErrorResponseMessages message;
	private final Restutills restutils;

	public LocationDao(ErrorResponseMessages message, Restutills restutils) {
		super();
		this.message = message;
		this.restutils = restutils;
	}

	public int checkLocExist(double latitude, double longitude, Connection conn){
		String query = "select id from location where JSON_UNQUOTE(JSON_EXTRACT(latlong, '$.latitude')) =" + latitude
				+ " AND JSON_UNQUOTE(JSON_EXTRACT(latlong, '$.longitude')) =" + longitude + " and Validated =1";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(Keys.id);
				}
			}
		} catch (Exception e) {
			throw new DataRetrievalFailedExceptiom(message.getMessage(Keys.ErrorSavingProfile));
		}
		return 0;
	}

	public int checkAlliasExist(String query, Connection conn){
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(Keys.id);
				}
			}
		} catch (Exception e) {
			throw new DataRetrievalFailedExceptiom(message.getMessage(Keys.ErrorSavingProfile));
		}
		return 0;
	}

	public List<String> getarrayalias(int id, Connection conn) {
		try (PreparedStatement ps = conn.prepareStatement(Queries.SELECT_ARRIALIAS)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String ali = rs.getString(Keys.ArrAlias);
					List<String> alias = new ArrayList<>();
					if (ali != null) {
						alias = restutils.getlocallias(ali);
					}
					return alias;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean updatearrayalias(String arayalias, int id, Connection conn) {
		try (PreparedStatement ps = conn.prepareStatement(Queries.UPDATE_ALLIAS)) {
			ps.setString(1, arayalias);
			ps.setInt(2, id);
			int updated = ps.executeUpdate();
			return updated > 0 ? true : false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public int insertNewLocation(String city,String state,String country,String latlong,String alias,boolean validated,Connection conn) {
		try (PreparedStatement stmt = conn.prepareStatement(Queries.INSERT_NEW_LOCATION, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, city);
			stmt.setString(2, state);
			stmt.setString(3, country);
			stmt.setString(4, latlong);
			stmt.setString(5, alias);
			stmt.setBoolean(6, validated);

			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				throw new DataRetrievalFailedExceptiom(message.getMessage(Keys.ErrorSavingProfile));
			}
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1); 
				} else {
					throw new DataRetrievalFailedExceptiom(message.getMessage(Keys.ErrorSavingProfile));
				}
			}
		}catch(SQLException e) {
			throw new DataRetrievalFailedExceptiom(message.getMessage(Keys.ErrorSavingProfile));
		}
	}

}
