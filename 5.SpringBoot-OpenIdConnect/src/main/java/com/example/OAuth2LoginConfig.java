package com.example;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

@Configuration
public class OAuth2LoginConfig {

    @EnableWebSecurity
    public static class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {
    	@Autowired
        private ClientRegistrationRepository clientRegistrationRepository;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                .oauth2Login()
                .authorizationEndpoint()
                	.authorizationRequestRepository(this.cookieAuthorizationRequestRepository())
	                //1. 配置自定义OAuth2AuthorizationRequestResolver
	                .authorizationRequestResolver(new IdSvrAuthorizationRequestResolver(this.clientRegistrationRepository))
	                .and()
	            ; 
        }
        
        private AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
            return new HttpSessionOAuth2AuthorizationRequestRepository();
        }
    }
    
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.idsvrClientRegistration());
    }

    private ClientRegistration idsvrClientRegistration() {
        return ClientRegistration.withRegistrationId(IdSvrAuthorizationRequestResolver.RegistrationId_IdentityServer4)
            .clientId("Y0RiYQ==")
            .clientName("cDba")
            .clientSecret("M2Y5NzAwMDkzYTIyYzRiNzE0YzBmYWU1N2Q2MmRjNTM=")
            .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .scope("openid", "profile", "adminapi")
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .redirectUriTemplate("{baseUrl}/oidc/signin-callback")
            //.redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
            .authorizationUri("http://localhost:1001/connect/authorize")
            .tokenUri("http://localhost:1001/connect/token")
            .userInfoUri("http://localhost:1001/connect/userinfo")
            .jwkSetUri("http://localhost:1001/.well-known/openid-configuration/jwks")
            .build();
    }
    
}
