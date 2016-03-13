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
	
	// Declaramos como transaccionales los dos métodos de acceso a Base de Datos para evitar que
	// el accesso concurrente pudiera duplicar registros en las tablas de tokens
	
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