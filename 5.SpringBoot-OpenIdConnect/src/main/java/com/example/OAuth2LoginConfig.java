package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

import com.example.util.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.util.IdSvrAuthenticationSuccessHandler;
import com.example.util.IdSvrAuthorizationRequestResolver;

@Configuration
public class OAuth2LoginConfig {
	private Logger logger = LoggerFactory.getLogger(OAuth2LoginConfig.class);
	
    @EnableWebSecurity
    public static class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {
    	@Autowired
        private ClientRegistrationRepository clientRegistrationRepository;
    	@Autowired
    	private IdSvrAuthenticationSuccessHandler successHandler;
    	
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                
                .oauth2Login()
                
                .successHandler(successHandler)
                .authorizationEndpoint()
                	
                	.authorizationRequestRepository(this.cookieAuthorizationRequestRepository())
	                //1. 配置自定义OAuth2AuthorizationRequestResolver
	                .authorizationRequestResolver(new IdSvrAuthorizationRequestResolver(this.clientRegistrationRepository))
	                .and()
	                //.exceptionHandling().authenticationEntryPoint(new AuthenticationProcessingFilterEntryPoint("/signIn"))
	                   
	            ; 
        }
        
        private AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
            //return new HttpSessionOAuth2AuthorizationRequestRepository();
        	final int shortLivedMillis = 15 * 60 * 1000; // 15 minutes
        	return new HttpCookieOAuth2AuthorizationRequestRepository(shortLivedMillis);
        }
    }
    
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.idsvrClientRegistration());
    }

    private ClientRegistration idsvrClientRegistration() {
    	System.out.println("----OAuth2LoginConfig idsvrClientRegistration: " + IdSvrAuthorizationRequestResolver.RegistrationId_IdentityServer4);

        return ClientRegistration.withRegistrationId(IdSvrAuthorizationRequestResolver.RegistrationId_IdentityServer4)
            .clientId("Y0RiYQ==")
            .clientName("cDba")
            .clientSecret("MmJmNWIyM2Q5ZjY4OWU5YzFmYWVkZTUwNzY2ZWJkNTg=")
            .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .scope("openid", "profile", "adminapi")
            //.userNameAttributeName("SubjectId")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            //.redirectUriTemplate("{baseUrl}/oidc/signin-callback")
            .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
            .authorizationUri("http://localhost:1001/connect/authorize")
            .tokenUri("http://localhost:1001/connect/token")
            .userInfoUri("http://localhost:1001/connect/userinfo")
            .jwkSetUri("http://localhost:1001/.well-known/openid-configuration/jwks")
            .build();
    }
    
}
