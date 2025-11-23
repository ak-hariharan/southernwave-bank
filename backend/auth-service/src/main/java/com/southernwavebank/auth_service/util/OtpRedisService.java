package com.southernwavebank.auth_service.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpRedisService {

	private static final long OTP_TTL = 5 * 60; // 5 minutes in seconds
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	public OtpRedisService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void saveOtp(String email, int otp) {
		redisTemplate.opsForValue().set(email, otp, OTP_TTL, TimeUnit.SECONDS);
	}

	public Integer getOtp(String email) {
		Object value = redisTemplate.opsForValue().get(email);
		return value != null ? (Integer) value : null;
	}

	public void deleteOtp(String email) {
		redisTemplate.delete(email);
	}
}
