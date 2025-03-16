package com.worklink.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.worklink.dao.LabourLoginDao;
import com.worklink.utills.Keys;
import com.worklink.utills.Queries;

@Service
public class SessionValidationDao {
	private static Logger logger = Logger.getLogger(LabourLoginDao.class);

	public Integer sessionValidation(int sessionId, int userId, int timeInMillis,Connection conn) {
		logger.info("Inside SessionValidationDao:sessionValidation:");
		try (PreparedStatement pst = conn.prepareStatement(Queries.CHECK_SESSION_VALID);) {
			pst.setInt(1, sessionId);
			pst.setInt(2, userId);
			pst.setInt(3, timeInMillis);
			try (ResultSet rs = pst.executeQuery();) {
				if (rs.next()) {
					logger.info("Inside SessionValidationDao:sessionValidation:Authenticated Session");
					return updateSessionActivity(rs.getInt(Keys.id), conn) > 0 ? rs.getInt(Keys.id) : 0;
				}
				logger.info("Inside SessionValidationDao:sessionValidation:UnAuthenticated Session");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	
	public Integer updateSessionActivity(int id,Connection conn) {
		logger.info("Inside updateSessionExpiry:");
		try(PreparedStatement pst=conn.prepareStatement(Queries.UPDATE_SESSION_ACTIVITY)) {
			pst.setInt(1, id);
			int updated=pst.executeUpdate();
			return updated;
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
