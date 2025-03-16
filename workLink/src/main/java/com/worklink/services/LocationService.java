package com.worklink.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worklink.dao.LocationDao;
import com.worklink.exception.DataRetrievalFailedExceptiom;
import com.worklink.model.Location;
import com.worklink.utills.Configuration;
import com.worklink.utills.ErrorKeys;
import com.worklink.utills.ErrorResponseMessages;
import com.worklink.utills.Restutills;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class LocationService {

	private final LocationDao loc;
	private final Configuration config;
	private final Restutills restutills;
	private final ErrorResponseMessages message;

	public LocationService(LocationDao loc, Configuration config, Restutills restutills,
			ErrorResponseMessages message) {
		super();
		this.loc = loc;
		this.config = config;
		this.restutills = restutills;
		this.message = message;
	}

	public List<Integer> getlocationIds(List<Location> loca, Connection conn) {
		List<Integer> locationIds = loca.stream().map(location -> {
			int locationId = -1;
			try {
				String country = location.country();
				String state = location.state();
				String city = location.city();

				String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
				String encodeState = URLEncoder.encode(state, StandardCharsets.UTF_8);
				String encodeCountry = URLEncoder.encode(country, StandardCharsets.UTF_8);
				String url = config.getLocation_API_URL() + encodedCity + "," + encodeState + "," + encodeCountry
						+ "&key=" + config.getLocation_API_Key();

				String res = restutills.connectandGet(url);
				if (res == null) {
					throw new DataRetrievalFailedExceptiom(message.getMessage(ErrorKeys.UNABLETOSAVE));
				}

				JSONObject latlong = restutills.formjson(res);
				int id = loc.checkLocExist(latlong.getDouble("latitude"), latlong.getDouble("longitude"), conn);

				if (id <= 0) {
					if (latlong != null && latlong.size() >= 2) {
						ObjectMapper objectMapper = new ObjectMapper();
						JSONArray citys = new JSONArray();
						citys.add(city.toUpperCase());

						String jsonString1 = objectMapper.writeValueAsString(citys);
						locationId = loc.insertNewLocation(city.toUpperCase(), state.toUpperCase(),
								country.toUpperCase(), latlong.toString(), jsonString1, true, conn);
					}
				} else {
					String query = "SELECT id FROM location WHERE JSON_SEARCH(ArrAlias, 'one', '" + city.toUpperCase()
							+ "' ) IS NOT NULL";
					int aliasid = loc.checkAlliasExist(query, conn);

					if (aliasid <= 0) {
						List<String> alliasarray = loc.getarrayalias(id, conn);
						JSONArray jsonArray = new JSONArray();
						ObjectMapper objectMapper = new ObjectMapper();

						if (alliasarray != null && !alliasarray.isEmpty()) {
							alliasarray.forEach(jsonArray::add);
						}
						jsonArray.add(city.toUpperCase());

						String jsonString = objectMapper.writeValueAsString(jsonArray);
						loc.updatearrayalias(jsonString, id, conn);
					} else {
						locationId = id;
					}
				}
			} catch (JsonProcessingException e) {
				throw new DataRetrievalFailedExceptiom(message.getMessage(ErrorKeys.UNABLETOSAVE));
			}
			return locationId;
		}).collect(Collectors.toList());
		return locationIds;
	}

}
