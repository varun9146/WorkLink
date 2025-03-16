package com.worklink.utills;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class OtpGenerator {

	@Autowired
	private Configuration properties;

	private static Logger logger = Logger.getLogger(OtpGenerator.class);
	private LoadingCache<String, Integer> otpCache;

	/**
	 * Constructor configuration.
	 */
	public OtpGenerator() {
		super();

		otpCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(String s) throws Exception {
						return 0;
					}
				});
	}

	/**
	 * Method for generating OTP and put it in cache.
	 *
	 * @param key - cache key
	 * @return cache value (generated OTP number)
	 */
	public Integer generateOTP(String key) {
		Random random = new Random();
		int OTP;
		if (properties.getOTP_QA() != null && properties.getOTP_QA().equalsIgnoreCase("true")) {
			OTP = 123456;
		} else {
			OTP = 100000 + random.nextInt(900000);
		}
		otpCache.put(key, OTP);
		logger.info("Inside OtpGenerator:generateOTP:Generated Otp is:  " + otpCache.toString());
		return OTP;
	}

	/**
	 * Method for getting OTP value by key.
	 *
	 * @param key - target key
	 * @return OTP value
	 */
	public Integer getOPTByKey(String key) {
		logger.info("Inside OtpGenerator:getOPTByKey:Passed Key is:  " + key);
		return otpCache.getIfPresent(key);
	}

	/**
	 * Method for removing key from cache.
	 *
	 * @param key - target key
	 */
	public void clearOTPFromCache(String key) {
		logger.info("Inside OtpGenerator:clearOTPFromCache:Cleared the Cache For the Key:  " + key);
		otpCache.invalidate(key);
	}

}
