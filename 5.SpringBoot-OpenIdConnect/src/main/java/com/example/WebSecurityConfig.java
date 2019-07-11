package com.example;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import com.example.util.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.util.IdSrvAccessDecisionManager;
import com.example.util.IdSrvAccessDecisionVoter;
import com.example.util.IdSrvAuthenticationFailureHandler;
import com.example.util.IdSrvAuthenticationProvider;
import com.example.util.IdSrvAuthenticationSuccessHandler;
import com.example.util.IdSrvAuthorizationCodeTokenResponseClient;
import com.example.util.IdSrvAuthorizationRequestResolver;
import com.example.util.IdSrvGrantedAuthority;
import com.example.util.IdSrvOidcUser;
import com.example.util.IdSrvSecurityInterceptor;
import com.example.util.OpenIdConnectConstants;

@Configuration
public class WebSecurityConfig {
	private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

	@EnableWebSecurity
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	public static class OAuth2LoginSecurityConfig extends WebSecurityConfigurerAdapter {
		@Autowired
		private ClientRegistrationRepository clientRegistrationRepository;
		@Autowired
		private IdSrvAuthenticationSuccessHandler successHandler;
		@Autowired
		private IdSrvAuthenticationFailureHandler failureHandler;
		@Autowired
		private IdSrvSecurityInterceptor securityFilter;
		@Autowired
		private IdSrvAuthorizationCodeTokenResponseClient idTokenResponseClient;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.addFilterBefore(securityFilter, FilterSecurityInterceptor.class)
				.authenticationProvider(this.authenticationProvider())
				.authorizeRequests().anyRequest().authenticated()
					// 修改授权相关逻辑
					.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
						public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {

							// 覆盖SecurityMetadataSource
							// fsi.setSecurityMetadataSource(fsi.getSecurityMetadataSource());

							// 覆盖AccessDecisionManager
							//fsi.setAccessDecisionManager(new IdSvrAccessDecisionManager());

							// 增加投票项
							//AccessDecisionManager accessDecisionManager = fsi.getAccessDecisionManager();
							//if (accessDecisionManager instanceof AbstractAccessDecisionManager) {
							//	((AbstractAccessDecisionManager) accessDecisionManager).getDecisionVoters()
							//			.add(new IdSvrAccessDecisionVoter());
							//}

							return fsi;
						}
					}).and()
					.oauth2Login()
					.successHandler(successHandler)
					.failureHandler(failureHandler)
					.authorizationEndpoint()
						//.authorizationRequestRepository(this.cookieAuthorizationRequestRepository())
						// 1. 配置自定义OAuth2AuthorizationRequestResolver
						.authorizationRequestResolver(new IdSrvAuthorizationRequestResolver(this.clientRegistrationRepository))
						.and()
					.tokenEndpoint()
						.accessTokenResponseClient(this.idTokenResponseClient)
						.and()
					.userInfoEndpoint()
						.customUserType(IdSrvOidcUser.class, OpenIdConnectConstants.ClientAuthScheme)
						.userAuthoritiesMapper(this.userAuthoritiesMapper())
						.oidcUserService(this.oidcUserService())

			;
		}

		@Autowired
	    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	        auth
	            .authenticationEventPublisher(authenticationEventPublisher());
	    }

	    @Bean
	    public DefaultAuthenticationEventPublisher authenticationEventPublisher() {
	        return new DefaultAuthenticationEventPublisher();
	    }
	    
	    private AuthenticationProvider authenticationProvider() {
	    	return new IdSrvAuthenticationProvider(this.idTokenResponseClient, oidcUserService());
	    }
		
		private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {

			// 方法一：方法内部实现
			/*
			 * final OidcUserService delegate = new OidcUserService();
			 * 
			 * return (userRequest) -> { 
			 * // Delegate to the default implementation for loading a user 
			 * OidcUser oidcUser = delegate.loadUser(userRequest);
			 * 
			 * OAuth2AccessToken accessToken = userRequest.getAccessToken();
			 * Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			 * 
			 * Map<String, Object> claims = oidcUser.getClaims();
			 * 
			 * // TODO 
			 * // 1) Fetch the authority information from the protected resource using accessToken 
			 * // 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities
			 * 
			 * // 3) Create a copy of oidcUser but use the mappedAuthorities instead
			 * oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(),
			 * oidcUser.getUserInfo());
			 * 
			 * return oidcUser; };
			 */

			// 方法二：自定义UserService
			final com.example.util.IdSrvOidcUserService delegate = new com.example.util.IdSrvOidcUserService();
			return (userRequest) -> {
				return delegate.loadUser(userRequest);
			};
		}

		private GrantedAuthoritiesMapper userAuthoritiesMapper() {
			return (authorities) -> {
				Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

				authorities.forEach(authority -> {
					if (IdSrvGrantedAuthority.class.isInstance(authority)) {
						IdSrvGrantedAuthority oidcUserAuthority = (IdSrvGrantedAuthority) authority;
						OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

						Map<String, Object> claims = userInfo.getClaims();
						if (oidcUserAuthority.getIdToken() != null
								&& oidcUserAuthority.getIdToken().getClaims().size() > 0) {
							claims = oidcUserAuthority.getIdToken().getClaims();
						}
						for (String key : claims.keySet()) {
							Object claim = claims.get(key);
							if (!key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_Id)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_Name)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_Email)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_Phone)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_DisplayName)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_TenantName)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_RoleId)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_RoleName)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_preferred_username)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_given_name)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_sid)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_aud)
									&& !key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_iss)) {
								if (claim instanceof String) {
									GrantedAuthority idAuthority = new IdSrvGrantedAuthority(key, claim.toString(),
											oidcUserAuthority.getIdToken(), userInfo);
									mappedAuthorities.add(idAuthority);
								} else if (claim instanceof List<?>) {
									List<String> claimList = (List<String>) claim;
									for (String ca : claimList) {
										GrantedAuthority idAuthority = new IdSrvGrantedAuthority(key, ca,
												oidcUserAuthority.getIdToken(), userInfo);
										mappedAuthorities.add(idAuthority);
									}
								}
							}
						}
					} else if (OidcUserAuthority.class.isInstance(authority)) {
						OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
						OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

						if (userInfo.containsClaim("role")) {
							String roleName = "ROLE_" + (String) userInfo.getClaimAsString("role");
							mappedAuthorities.add(new SimpleGrantedAuthority(roleName));
						}
					} else if (OAuth2UserAuthority.class.isInstance(authority)) {
						OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
						Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

						if (userAttributes.containsKey("role")) {
							String roleName = "ROLE_" + (String) userAttributes.get("role");
							mappedAuthorities.add(new SimpleGrantedAuthority(roleName));
						}
					}
				});

				return mappedAuthorities;
			};
		}

		// 将认证信息保存到cookie
		private AuthorizationRequestRepository<OAuth2AuthorizationRequest> cookieAuthorizationRequestRepository() {
			// return new HttpSessionOAuth2AuthorizationRequestRepository();
			final int shortLivedMillis = 15 * 60 * 1000; // 15 minutes
			return new HttpCookieOAuth2AuthorizationRequestRepository(shortLivedMillis);
		}
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(this.idsvrClientRegistration());
	}

	private ClientRegistration idsvrClientRegistration() {
		System.out.println("----WebSecurityConfig idsvrClientRegistration: "
				+ OpenIdConnectConstants.ClientAuthScheme);

		return ClientRegistration.withRegistrationId(OpenIdConnectConstants.ClientAuthScheme)
				.clientId("Y0RiYQ==").clientName("cDba").clientSecret("MmJmNWIyM2Q5ZjY4OWU5YzFmYWVkZTUwNzY2ZWJkNTg=")
				.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.scope("openid", "profile", "adminapi")
				// .userNameAttributeName("SubjectId")
				.userNameAttributeName(IdTokenClaimNames.SUB)
				// .redirectUriTemplate("{baseUrl}/oidc/signin-callback/{registrationId}")
				.redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
				.authorizationUri("http://localhost:1001/connect/authorize")
				.tokenUri("http://localhost:1001/connect/token").userInfoUri("http://localhost:1001/connect/userinfo")
				.jwkSetUri("http://localhost:1001/.well-known/openid-configuration/jwks").build();
	}

}
