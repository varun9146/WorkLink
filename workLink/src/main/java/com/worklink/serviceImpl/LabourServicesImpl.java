package com.worklink.serviceImpl;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.worklink.dao.GeneralDao;
import com.worklink.dao.Registration;
import com.worklink.exception.DataRetrievalFailedExceptiom;
import com.worklink.exception.InvalidRequestException;
import com.worklink.model.AlertMessageData;
import com.worklink.model.LabourRegistration;
import com.worklink.model.ValidateOTP;
import com.worklink.model.VerifyOTP;
import com.worklink.services.LabourServices;
import com.worklink.services.LocationService;
import com.worklink.utills.AlertMessage;
import com.worklink.utills.Configuration;
import com.worklink.utills.ErrorKeys;
import com.worklink.utills.ErrorResponseMessages;
import com.worklink.utills.Keys;
import com.worklink.utills.OtpGenerator;
import com.worklink.utills.Restutills;
import com.worklink.utills.SaveProfileResponse;
import com.worklink.utills.SessionEntryption;

import net.sf.json.JSONObject;

@Service
@Validated
public class LabourServicesImpl implements LabourServices {

	private final ErrorResponseMessages message;
	private final Registration registration;
	private final Configuration config;
	private final Restutills restutills;
	private final OtpGenerator otpGenerator;
	private final AlertMessage alertMessage;
	private final GeneralDao gDao;
	private final SessionEntryption session;
	private final LocationService locservice;

	public LabourServicesImpl(ErrorResponseMessages message, Registration registration, Configuration config,
			Restutills restutills,OtpGenerator otpGenerator, AlertMessage alertMessage,
			GeneralDao gDao, SessionEntryption session, LocationService locservice) {
		super();
		this.message = message;
		this.registration = registration;
		this.config = config;
		this.restutills = restutills;
		this.otpGenerator = otpGenerator;
		this.alertMessage = alertMessage;
		this.gDao = gDao;
		this.session = session;
		this.locservice = locservice;
	}

	@Override
	public Optional<SaveProfileResponse> saveProfile(LabourRegistration labour, Connection conn) {
		System.out.println(" length is " + labour.number().length());
		if (labour.number().length() != 10) {
			throw new InvalidRequestException(message.getMessage(ErrorKeys.Invalid_Number_Key));
		}
		if (!labour.countryCode().equals("+91")) {
			throw new InvalidRequestException(message.getMessage(ErrorKeys.Invalid_Country_code));
		}
		if (registration.checknumberexist(labour.number(), conn)) {
			throw new InvalidRequestException(message.getMessage(ErrorKeys.Number_exist));
		} else {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date date = formatter.parse(labour.dob());
				System.out.println("Converted Date: " + date);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new InvalidRequestException(message.getMessage(ErrorKeys.Error_getting_number));
			}
			List<Integer> loc = locservice.getlocationIds(labour.location(), conn);
			String array = restutills.convertlistToString(loc);
			boolean updated = registration.registerLabour(labour, array, conn);
			if (updated) {
				SaveProfileResponse s = new SaveProfileResponse();
				s.setMessage("Profile Saved Sucessfully");
				return Optional.of(s);
			} else {
				return Optional.empty();
			}
		}
	}

	@Override
	public Optional<SaveProfileResponse> generateOTP(String mobileNumber, String countryCode, Boolean labour,
			Connection conn) {
		try {
			boolean ispresent = false;
			if (labour) {
				ispresent = registration.checknumberexist(mobileNumber, conn);
			} else {
				ispresent =  registration.checkJobPosterNumberexist(mobileNumber, conn);
			}

			if (ispresent) {
				Optional<Integer> otpValue = generateOtp(mobileNumber, countryCode, conn);
				if (otpValue.isPresent() && otpValue.get().intValue() == -1) {
					return Optional.empty();
				}
			} else {
				return Optional.empty();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String msg = message.getMessage(Keys.OTP_SUCCESS) + " " + mobileNumber;
		SaveProfileResponse svp = new SaveProfileResponse();
		svp.setMessage(msg);
		return Optional.of(svp);
	}

	/**
	 * Method for generate OTP number
	 *
	 * @param key - provided key (username in this case)
	 * @return boolean value (true|false)
	 */
	public Optional<Integer> generateOtp(String key, String countryCode, Connection con) {
		// generate otp
		Integer otpValue = otpGenerator.generateOTP(countryCode + key);
		AlertMessageData messageData = new AlertMessageData();
		messageData.setToNumber(key);
		messageData.setCc(countryCode);
		messageData.setPassword(otpValue.toString());
		String toadd = alertMessage.addAddress(messageData);
		gDao.sendSms(null, toadd, message.getMessage("OTP_SMS"), 0, con);
		return Optional.of(otpValue);
	}

	@Override
	public Optional<ValidateOTP> verifyOTP(VerifyOTP verifyOTP, Connection conn) {

		Integer cacheOTP = otpGenerator.getOPTByKey(verifyOTP.countryCode() + verifyOTP.mobileNumber());

		if (cacheOTP != null && cacheOTP.equals(verifyOTP.otp())) {
			if (verifyOTP.labour()) {
				int labourid = registration.getLabourId(verifyOTP.mobileNumber(), conn);
				String sessionId = createsession(labourid, config.getLabourType(), conn);
				ValidateOTP validate = new ValidateOTP(message.getMessage(Keys.LOGIN_SUCESS), sessionId);
				return Optional.of(validate);
			} else {
				int jobPosterid = registration.getjobPosterId(verifyOTP.mobileNumber(), conn);
				String sessionId = createsession(jobPosterid, config.getJobPosterType(), conn);
				ValidateOTP validate = new ValidateOTP(message.getMessage(Keys.LOGIN_SUCESS), sessionId);
				return Optional.of(validate);
			}
		}
		return Optional.empty();
	}

	public JSONObject getotp(AlertMessageData msg) {
		try {
			JSONObject obj1 = new JSONObject();
			obj1.put("OTP", msg.getPassword());
			obj1.put("number", msg.getCc() + msg.getToNumber());
			return obj1;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Optional<SaveProfileResponse> logout(int id, Connection conn) {
		Integer i = gDao.updateSessionExpiry(id, conn);
		if (i > 0) {
			SaveProfileResponse sp = new SaveProfileResponse();
			sp.setMessage(message.getMessage(Keys.LOGOUT_SUCESS));
			return Optional.of(sp);
		}
		return Optional.empty();
	}

	public String createsession(int id, String type, Connection conn) {
		gDao.updateSessionExpiry(id, conn);
		int incrementalSessionId = gDao.createSession(id, type, conn);
		if (incrementalSessionId > 0) {
			JSONObject objSession = session.encryptSessionId(incrementalSessionId, id);
			gDao.updateSessionTimeInMillis(incrementalSessionId, objSession.getInt(Keys.TIMEINMILLISECONDS), conn);
			return objSession.getString(Keys.sessionId);
		} else {
			throw new DataRetrievalFailedExceptiom(message.getMessage(ErrorKeys.Invalid_Number_Key));
		}
	}

}

//	List<Integer> locationIds = new ArrayList<Integer>();
////	JsonObject locationobject=new JsonObject();
//		JSONObject locationobject = new JSONObject();
//		JSONArray locationArray1 = new JSONArray();
//		try {
//		for (int i = 0; i < labour.loc().size(); i++) {
//			int locationId = -1;
//			Location location = labour.loc().get(i);
//			String country = location.country();
//			String state = location.state();
//			String city = location.city();
//
//			String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
//			String encodeState = URLEncoder.encode(state, StandardCharsets.UTF_8);
//			String encodeCountry = URLEncoder.encode(country, StandardCharsets.UTF_8);
//			String url = config.getLocation_API_URL() + encodedCity+","+encodeState+","+ encodeCountry + "&key="
//					+config.getLocation_API_Key();
//			String res = restutills.connectandGet(url);
//			if (res == null) {
//				throw new DataRetrievalFailedExceptiom(message.getMessage(ErrorKeys.UNABLETOSAVE));
//			}
//
//			JSONObject latlong = restutills.formjson(res);
//			int id = loc.checkLocExist(latlong.getDouble("latitude"), latlong.getDouble("longitude"),conn);
//			if (id <=0) {
//				if (latlong != null && latlong.size() >= 2) {
//					ObjectMapper objectMapper = new ObjectMapper();
//				
//						String jsonString = objectMapper.writeValueAsString(latlong);
//					
//					JSONArray citys = new JSONArray();
//					citys.add(city.toUpperCase());
//				
//					String jsonString1;
//
//						jsonString1 = objectMapper.writeValueAsString(citys);
//						locationId=loc.insertNewLocation(city.toUpperCase(), state.toUpperCase(), country.toUpperCase(), latlong.toString(), jsonString1, true, conn);							
//				}
//			} else {
//				String query = "SELECT id FROM location WHERE JSON_SEARCH(ArrAlias,  'one','"
//						+ city.toUpperCase() + "' ) IS NOT NULL";
//				int aliasid =loc.checkAlliasExist(query, conn);
//				if (aliasid <= 0 ) {
//					List<String> alliasarray =loc.getarrayalias(id, conn);
//					if (alliasarray != null && alliasarray.size() >0) {
//						JSONArray jsonArray = new JSONArray();
//						ObjectMapper objectMapper = new ObjectMapper();
//						for (String j : alliasarray) {
//							jsonArray.add(j);
//						}
//						jsonArray.add(city.toUpperCase());
//						String jsonString;
//							jsonString = objectMapper.writeValueAsString(jsonArray);
//							loc.updatearrayalias(jsonString, id, conn);
//					} else {
//						JSONArray citys = new JSONArray();
//						citys.add(city.toUpperCase());
//						ObjectMapper objectMapper = new ObjectMapper();
//						String jsonString1;
//							jsonString1 = objectMapper.writeValueAsString(citys);
//							loc.updatearrayalias(jsonString1, id, conn);
//					}
//
//					locationId = id;
//					locationobject.put("id", id);
//					locationobject.put("alias", city.toUpperCase());
//				} else {
//					locationId = id;
//					locationobject.put("id", id);
//					locationobject.put("alias", city.toUpperCase());
//				}
//			}
//			locationIds.add(locationId);
//		}} catch (JsonProcessingException e) {
//			throw new DataRetrievalFailedExceptiom(message.getMessage(ErrorKeys.UNABLETOSAVE));
//		}
