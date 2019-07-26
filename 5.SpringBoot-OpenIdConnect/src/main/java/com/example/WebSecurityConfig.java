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
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import com.example.util.IdSrvAccessDecisionManager;
import com.example.util.IdSrvAccessDecisionVoter;
import com.example.util.IdSrvAuthenticationFailureHandler;
import com.example.util.IdSrvAuthenticationProvider;
import com.example.util.IdSrvAuthenticationSuccessHandler;
import com.example.util.IdSrvAuthorizationCodeTokenResponseClient;
import com.example.util.IdSrvAuthorizationRequestResolver;
import com.example.util.IdSrvGrantedAuthority;
import com.example.util.IdSrvOidcUser;
import com.example.util.IdSrvOidcUserService;
import com.example.util.IdSrvSecurityInterceptor;
import com.example.util.IdSrvMethodSecurityInterceptor;
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
		//@Autowired
		//private IdSrvMethodSecurityInterceptor securityMethodFilter;
		@Autowired
		private IdSrvAuthorizationCodeTokenResponseClient idTokenResponseClient;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.addFilterBefore(securityFilter, FilterSecurityInterceptor.class)
				//.addFilterBefore(securityMethodFilter, FilterSecurityInterceptor.class)
				.authenticationProvider(new IdSrvAuthenticationProvider(this.idTokenResponseClient, oidcUserService()))
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
					//.successHandler(successHandler)
					//.failureHandler(failureHandler)
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

		private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
			final IdSrvOidcUserService delegate = new IdSrvOidcUserService();
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
							if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_AuthorityIds)) {
								if (claim instanceof String) {
									GrantedAuthority idAuthority = new IdSrvGrantedAuthority(claim.toString(),
											oidcUserAuthority.getIdToken(), userInfo);
									mappedAuthorities.add(idAuthority);
								} else if (claim instanceof List<?>) {
									@SuppressWarnings("unchecked")
									List<String> claimList = (List<String>) claim;
									for (String ca : claimList) {
										GrantedAuthority idAuthority = new IdSrvGrantedAuthority(ca,
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
				
				StringBuilder sb = new StringBuilder();
	            for(GrantedAuthority authority : mappedAuthorities)
	            {
	            	sb.append(authority.getAuthority() + ", ");
	            }
	            System.out.println("----userAuthoritiesMapper mappedAuthorities: " + sb.toString());
				return mappedAuthorities;
			};
		}

	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(this.idsvrClientRegistration());
	}

	private ClientRegistration idsvrClientRegistration() {
		logger.debug("----WebSecurityConfig idsvrClientRegistration: "
				+ OpenIdConnectConstants.ClientAuthScheme);

		return ClientRegistration.withRegistrationId(OpenIdConnectConstants.ClientAuthScheme)
				.clientId("Y0RiYQ==").clientName("cDba").clientSecret("MmJmNWIyM2Q5ZjY4OWU5YzFmYWVkZTUwNzY2ZWJkNTg=")
				.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.scope(OidcScopes.OPENID, OidcScopes.PROFILE, "adminapi")
				// .userNameAttributeName("SubjectId")
				.userNameAttributeName(IdTokenClaimNames.SUB)
				// .redirectUriTemplate("{baseUrl}/oidc/signin-callback/{registrationId}")
				.redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
				.authorizationUri("http://localhost:1001/connect/authorize")
				.tokenUri("http://localhost:1001/connect/token").userInfoUri("http://localhost:1001/connect/userinfo")
				.jwkSetUri("http://localhost:1001/.well-known/openid-configuration/jwks").build();
	}

}
