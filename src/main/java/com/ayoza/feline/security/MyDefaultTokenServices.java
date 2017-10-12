package com.ayoza.feline.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@EnableTransactionManagement
public class MyDefaultTokenServices extends DefaultTokenServices {
	
	// Declare these two methods as transactional in order to avoid duplicated registers in 
	//  token tables
	
	@Transactional
	@Override
	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
	    return super.createAccessToken(authentication);
	}

	@Transactional
	@Override
	public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {
	    return super.refreshAccessToken(refreshTokenValue, tokenRequest);
	}

}