package com.ayoza.feline.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private MyUserDetailsManager myUserDetailsManager;

    /**
     * Configures how users will be authenticated.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	
    	DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    	
    	Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
    	daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    	
    	daoAuthenticationProvider.setUserDetailsService(myUserDetailsManager);
    	
        // @formatter:off
    	auth.authenticationProvider(daoAuthenticationProvider);
       /* auth
            .inMemoryAuthentication()
                .withUser("user1")
                    .password("user1")
                    .roles("USER")
                    .and()
                .withUser("user2")
                    .password("user2")
                    .roles("USER")
        ;*/
        // @formatter:on
    }

    /**
     * Configures static resources that you don't want to be secured.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
        .antMatchers("/styles/**")
        .antMatchers("/images/**");
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
	public MyDefaultTokenServices defaultTokenServices() {
		MyDefaultTokenServices defaultTokenServices = new MyDefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		//defaultTokenServices.setTokenEnhancer(tokenEnhancer);
	    return defaultTokenServices;
	}
    
	@Bean
	public TokenStore tokenStore() {
		//tokenStore = new JdbcTokenStore(wsiaDataSource); 
	    //return tokenStore;
		return new InMemoryTokenStore();
	}

    /**
     * Configures the application's security.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
        http
        	.sessionManagement()
    			.sessionCreationPolicy(SessionCreationPolicy.NEVER)
    			.and()
            .authorizeRequests()
                .antMatchers("/").permitAll()
                .and()
//            .authorizeRequests()
//                //.anyRequest().hasRole("USER")
//                .anyRequest().hasRole("PPV_USER")
//                .and()
            //.authorizeRequests()

            	// De momento comentamos esta línea que implicaría logarse al cliente
            	//.anyRequest().hasRole("CLIENT")
            	//.antMatchers("/v1/generics/**").permitAll()
            	//.and()          	
//            .authorizeRequests()
//                .antMatchers(HttpMethod.POST, "/v1/users").permitAll()
//            	.and()
//            .authorizeRequests()
//                .antMatchers(HttpMethod.PUT, "/v1/users/confirm").permitAll()
//            	.and()            	
            .exceptionHandling()
                .accessDeniedPage("/login?error=authorization")
                .and()
            .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable()
            .logout()
                .logoutSuccessUrl("/")
                .and()
            .formLogin()
                .failureUrl("/login?error=authentication")
                .loginPage("/login") // If don't set, default page is generated and used.
                .permitAll()
            ;
    }
    
    
}
