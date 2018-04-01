package com.ayoza.feline.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import ayoza.com.feline.api.managers.UserServicesMgr;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final UserServicesMgr userServicesMgr;

    /**
     * Configures how users will be authenticated.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    	
    	daoAuthenticationProvider.setUserDetailsService(userServicesMgr);

    	auth.authenticationProvider(daoAuthenticationProvider);
    }
    
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
    	DelegatingPasswordEncoder passwordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
    	passwordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
    	return passwordEncoder;
    }

    /**
     * Exposes AuthenticationManager as a bean.
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
	@Bean
	@Primary
	public AuthorizationServerTokenServices defaultTokenServices() {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(tokenStore());
		tokenServices.setSupportRefreshToken(true);
	    return tokenServices;
	}
    
	@Bean
	public TokenStore tokenStore() {
		// TODO Set distributed token store, redis?
		TokenStore tokenStore = new InMemoryTokenStore();//new JdbcTokenStore(dataSource); 
	    return tokenStore;
	}

    /**
     * Configures the application's security.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
        	.and()
        	.httpBasic().disable()
        	.csrf().disable()
        	.anonymous().disable()     	
        	.authorizeRequests().anyRequest().authenticated()
        ;
    }
    
}
