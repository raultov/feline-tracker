package com.ayoza.feline.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import ayoza.com.feline.api.managers.UserServicesMgr;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableWebSecurity
@AllArgsConstructor(onConstructor=@__({@Autowired}))
@FieldDefaults(level=AccessLevel.PRIVATE)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	UserServicesMgr userServicesMgr;
	
	DataSource dataSource;

    /**
     * Configures how users will be authenticated.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    	
    	// TODO replace this with PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    	// and prefix every DB password with prefix {MD5}
    	//PasswordEncoder passwordEncoder = new MessageDigestPasswordEncoder("MD5");
    	PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    	daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    	
    	daoAuthenticationProvider.setUserDetailsService(userServicesMgr);
    	
    	auth.authenticationProvider(daoAuthenticationProvider);
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
		MyDefaultTokenServices defaultTokenServices = new MyDefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
	    return defaultTokenServices;
	}
    
	@Bean
	public TokenStore tokenStore() {
		TokenStore tokenStore = new JdbcTokenStore(dataSource); 
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
