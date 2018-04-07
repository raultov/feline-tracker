 package com.ayoza.feline.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import lombok.RequiredArgsConstructor;

@Configuration
public class OAuth2ServerConfig {

    private static final String RESOURCE_1_ID = "resource-1";

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(RESOURCE_1_ID);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
			http
				.requestMatchers().antMatchers(
											   "/oauth/token/revoke",
											   "/v1/tracks/**",
											   "/v1/users/**",
											   "/v1/trackers/**",
											   "/cache/**"
											   )
				.and()
			.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/oauth/token/revoke").access("#oauth2.hasScope('general') and hasRole('ROLE_USER')")
                
                .antMatchers(HttpMethod.POST, "/v1/tracks").access("#oauth2.hasScope('general') and hasRole('ROLE_TRA_USER')")
                .antMatchers(HttpMethod.GET, "/v1/tracks").access("#oauth2.hasScope('general') and hasRole('ROLE_APP_USER')")
                .antMatchers(HttpMethod.GET, "/v1/tracks/*/center").access("#oauth2.hasScope('general') and hasRole('ROLE_APP_USER')")
                .antMatchers(HttpMethod.GET, "/v1/tracks/*/points").access("#oauth2.hasScope('general') and hasRole('ROLE_APP_USER')")
              
                .antMatchers(HttpMethod.GET, "/v1/users").access("#oauth2.hasScope('general') and hasRole('ROLE_APP_USER')")
                
                .antMatchers(HttpMethod.GET, "/v1/trackers").access("#oauth2.hasScope('general') and hasRole('ROLE_APP_USER')")
                
                .antMatchers(HttpMethod.DELETE, "/cache/**").access("#oauth2.hasScope('general') and hasRole('ROLE_APP_ADMIN')")
            .and()
            	.csrf().disable()
            	.cors().disable()
                ;                

            // @formatter:off
        }
    }

    @Configuration
    @EnableAuthorizationServer
    @RequiredArgsConstructor
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        private final AuthenticationManager authenticationManager;
        
        private final TokenStore tokenStore;
        
        private final AuthorizationServerTokenServices defaultTokenServices;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        	
            // @formatter:off
			clients.inMemory()
                    .withClient("client-with-refresh-token")
			 			.resourceIds(RESOURCE_1_ID)
			 			.authorizedGrantTypes("password", "client_credentials", "refresh_token")
			 			.authorities("ROLE_CLIENT")
			 			.scopes("general")
                        .secret("$2a$04$SGPmvIrMa50tRNXr2z5OO.mdOpgzrQurPdrzy2D.VimA5vUxDJHEK")
                        .and()
            ;
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                    // Set an AuthenticationManager to support password grant type!
                    .authenticationManager(authenticationManager)

                    // For testing purpose we don't want to store the approvals, so each time the approval will be checked.
                    .userApprovalHandler(new DefaultUserApprovalHandler())
                    
                    .tokenStore(tokenStore)
                    
                    .tokenServices(defaultTokenServices)
            ;
        }
    }

}
