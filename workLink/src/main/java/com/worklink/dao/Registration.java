package com.worklink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Component;

import com.worklink.exception.DataInsertionFailedException;
import com.worklink.exception.DataRetrievalFailedExceptiom;
import com.worklink.model.JobPosterRegistration;
import com.worklink.model.LabourRegistration;
import com.worklink.utills.ErrorKeys;
import com.worklink.utills.ErrorResponseMessages;
import com.worklink.utills.Keys;
import com.worklink.utills.Queries;

@Component
public class Registration {
	private final ErrorResponseMessages message;

	public Registration(ErrorResponseMessages message) {
		super();
		this.message = message;
	}

	public boolean checknumberexist(String number, Connection conn) {
		try (PreparedStatement ps = conn.prepareStatement(Queries.CHECK_NUMBER_EXIST)) {
			ps.setString(1, number);
			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return rs.getInt(Keys.id) > 0 ? true : false;
				}
			}
		} catch (Exception e) {
			throw new DataRetrievalFailedExceptiom(message.getMessage(ErrorKeys.Error_getting_number));
		}
		return false;
	}	
	
	public boolean checkJobPosterNumberexist(String number, Connection conn) {
		try (PreparedStatement ps = conn.prepareStatement(Queries.CHECK_JOBPOSTER_NUMBER_EXIST)) {
			ps.setString(1, number);
			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return rs.getInt(Keys.id) > 0 ? true : false;
				}
			}
		} catch (Exception e) {
			throw new DataRetrievalFailedExceptiom(message.getMessage(ErrorKeys.Error_getting_number));
		}
		return false;
	}
	public boolean registerLabour(LabourRegistration labour,String loc,Connection conn) {
		try(PreparedStatement ps =conn.prepareStatement(Queries.INSERT_LABOUR)){
			ps.setString(1, labour.firstName());
			if(labour.lastName() != null && !labour.lastName().isEmpty()) {
				ps.setString(2,labour.lastName());
			}else {
				ps.setString(2,"");
			}
			ps.setString(3, labour.number());
			ps.setString(4, labour.countryCode());
			ps.setString(5, labour.gender());
			ps.setString(6, labour.dob().toString());
			ps.setInt(7, labour.education());
			ps.setDouble(8, labour.experience());
			ps.setDouble(9, labour.perDayCharges());
			ps.setString(10, loc);
			ps.setInt(11, labour.age());
			int executed = ps.executeUpdate();
			return executed >0 ? true : false;
		}catch (Exception e) {
			throw new DataInsertionFailedException(message.getMessage(ErrorKeys.UNABLETOSAVE));
		}
	}
	
	public boolean registerobPoster(JobPosterRegistration jobPoster,String loc,Connection conn) {
		try(PreparedStatement ps =conn.prepareStatement(Queries.INSERT_LABOUR)){
			ps.setString(1, jobPoster.firstName());
			if(jobPoster.lastName() != null && !jobPoster.lastName().isEmpty()) {
				ps.setString(2,jobPoster.lastName());
			}else {
				ps.setString(2,"");
			}
			ps.setString(3, jobPoster.number());
			ps.setString(4, jobPoster.countryCode());
			ps.setString(5, jobPoster.gender());
			ps.setString(6, jobPoster.dob().toString());
			ps.setInt(7, jobPoster.education());
			ps.setDouble(8, jobPoster.experience());
			ps.setDouble(9, jobPoster.perDayCharges());
			ps.setString(10, loc);
			ps.setInt(11, jobPoster.age());
			int executed = ps.executeUpdate();
			return executed >0 ? true : false;
		}catch (Exception e) {
			throw new DataInsertionFailedException(message.getMessage(ErrorKeys.UNABLETOSAVE));
		}
	}
	
	
	public int getLabourId(String number, Connection conn) {
		try (PreparedStatement ps = conn.prepareStatement(Queries.CHECK_NUMBER_EXIST)) {
			ps.setString(1, number);
			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return rs.getInt(Keys.id);
				}
			}
		} catch (Exception e) {
			throw new DataRetrievalFailedExceptiom(message.getMessage(ErrorKeys.Error_getting_number));
		}
		return 0;
	}
	
	public int getjobPosterId(String number, Connection conn) {
		try (PreparedStatement ps = conn.prepareStatement(Queries.CHECK_JOBPOSTER_NUMBER_EXIST)) {
			ps.setString(1, number);
			try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
					return rs.getInt(Keys.id);
				}
			}
		} catch (Exception e) {
			throw new DataRetrievalFailedExceptiom(message.getMessage(ErrorKeys.Error_getting_number));
		}
		return 0;
	}
}
