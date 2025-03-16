package com.worklink.security;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.worklink.dbconfig.ConnectionPool;
import com.worklink.utills.Keys;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SessionValidationFilter  implements Filter {
	private static final Logger logger = Logger.getLogger(SessionValidationFilter.class);

	@Autowired
	com.worklink.utills.SessionEntryption session;

	@Autowired
	SessionValidationDao sessionDao;

	@Autowired
	ConnectionPool connectionPool;
	// List of URLs to be excluded from the filter
	// Add your excluded URL patterns here
	private static final List<String> EXCLUDED_URLS = Arrays.asList("/workLink/wl/slp", "/wl/slp");
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        //res.setHeader("Access-Control-Allow-Headers", "x-session, Content-Type"); // Add your custom headers
        res.setHeader("Access-Control-Allow-Headers", "*");
        res.setHeader("Access-Control-Max-Age", "3600"); // One hour
        
        String sessionId =null;
//     // Get the value of the x-session header
 //       String headersString = req.getHeader("access-control-request-headers");
//        logger.info("Headers String:--#######" + headersString);
//        String[] headersArray = headersString.split(",");
//        for (String header : headersArray) {
//            if (header.trim().equalsIgnoreCase("x-session")) {
//                String xSessionValue = req.getHeader(header.trim());
//                // Now you have the value of the x-session header
//                logger.info("x-session value-----$$$$: " + xSessionValue);
//                sessionId=xSessionValue;
//                break;
//            }
//        }

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            // Handle preflight request as needed, e.g., set CORS headers
        	logger.info("Inside preflight request:");
        	res.setHeader("Access-Control-Allow-Origin", "*");
    		res.setHeader("Access-Control-Allow-Credentials", "true");
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "*"); // Add your custom headers
            String error="Invalid sessionid";
            res.setHeader("Access-Control-Max-Age", "3600"); // One hour
            res.setStatus(HttpServletResponse.SC_OK);
    		res.setContentLength(error.length());
    		res.getWriter().write(error);
    		logger.info("Response:########:"+ res.toString());
            return;
        }
        
        
		sessionId =  req.getHeader("x-session");
		logger.info("Session ID:" + sessionId);
        
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = req.getHeader(headerName);
            logger.info("Header: " + headerName + " - Value: " + headerValue);
        }
        
		// Get the request URI
		String requestUri = req.getRequestURI();
		logger.info("Inside SessionValidationFilter:doFilter:Requested Url:" + requestUri);
		// Check if the request URI matches any excluded pattern
		boolean isExcluded = EXCLUDED_URLS.stream().anyMatch(requestUri::matches);
		logger.info("Inside SessionValidationFilter:doFilter:isExcluded:" + isExcluded);

		if (isExcluded) {
			// If the request URI is excluded, pass through without validation
			logger.info("Inside SessionValidationFilter:doFilter:isExcluded:" + "pass through without validation");
			chain.doFilter(req, res);
		} else {
			// Continue with your existing validation logic
			logger.info("Inside SessionValidationFilter:doFilter:URL Validation:");
//			String sessionId = req.getHeader(Keys.Sessionid);
			logger.info("header------------ : " + req.getHeader("X-Session") + "session id:-----" + sessionId);
			if (sessionId != null && !sessionId.isEmpty()) {
				logger.info("Inside SessionValidationFilter:doFilter:URL Validation:Session Id is Exist in the request");
				checkSessionIsValid(req, res, chain, sessionId);
			} else {
				logger.info("Inside SessionValidationFilter:doFilter:URL Validation:Session Id is Not Exist in the request");
				sendHttpErrorMessage(req, res);
			}

		}
	}

	private void checkSessionIsValid(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			String id) throws ServletException, IOException {
		logger.info("Inside sessionValidationFilter:checkSessionIsValid:Validate session ");
		try (Connection conn = connectionPool.getConnection()) {
			JSONObject sessionObj = session.decryptSessionId(id);
			logger.info("Session Object:" + sessionObj);
			if (sessionObj == null) {
				sendHttpErrorMessage(request, response);
			} else {
				int sessionId = sessionObj.getInt(Keys.sessionId);
				int userId = sessionObj.getInt(Keys.userId);
				int time = sessionObj.getInt(Keys.TIMEINMILLISECONDS);
				if (sessionDao.sessionValidation(sessionId, userId, time, conn) > 0) {
					passOver(request, response, chain, id, String.valueOf(userId));
				} else {
					sendHttpErrorMessage(request, response);
				}
			}

		} catch (Exception e) {
		   e.printStackTrace();
		}

	}

	private void passOver(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String session,
			String clientId) throws ServletException, IOException {
		logger.info("Inside sessionValidationFilter:passOver:session:" + session);
		request.setAttribute("sessionId", session);
		request.setAttribute("clientId", clientId);
		chain.doFilter(request, response);
	}

	private void sendHttpErrorMessage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		logger.info("Inside sessionValidationFilter:sendHttpErrorMessage:");
		String error = "Invalid session id";
		response.reset();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentLength(error.length());
		response.getWriter().write(error);
	}

	
	
}
