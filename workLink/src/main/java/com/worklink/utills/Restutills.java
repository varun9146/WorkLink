package com.worklink.utills;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worklink.exception.InvalidRequestException;

import net.sf.json.JSONObject;

@Component
public class Restutills {

	@Autowired
	private ObjectMapper json;

	public String connectandGet(String URL) {
		try {
			URL url = new URL(URL);
			System.out.println(" url is " + URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			int statuscode = connection.getResponseCode();
			if (statuscode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder response = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();
				return response.toString();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject formjson(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(json);
			// Extract location's latitude and longitude
			JsonNode locationNode = rootNode.path("results").path(0).path("geometry").path("location");
			double latitude = locationNode.path("lat").asDouble();
			double longitude = locationNode.path("lng").asDouble();
			JSONObject re = new JSONObject();
			re.put("latitude", latitude);
			re.put("longitude", longitude);
			return re;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public List<String> getlocallias(String alias) {
		try {
			return json.readValue(alias, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new InvalidRequestException("Invalid Json");
		}
	}

	public String convertlistToString(List<Integer> list) {
		String result = list.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
		return result;
	}

	public String formQuery(String searchkeyword, String tablename, List<String> columns) {
		String query = "select * from " + tablename + " ";
		if (checkIsEmpty(searchkeyword) || checkIsEmpty(tablename) || columns ==null || columns.size() <=0 ) {
			return query;
		} else {
			String operator = "%" + searchkeyword + "%";

			String whereClause = columns.stream().map(column -> column + " LIKE '" + operator + "'")
					.collect(Collectors.joining(" OR "));
			query += " WHERE " + whereClause;
			return query;
		}
	}

	public boolean checkIsEmpty(String keyword) {
		if (keyword == null) {
			return true;
		} else if (keyword.isEmpty()) {
			return true;
		}
		return false;
	}
}
