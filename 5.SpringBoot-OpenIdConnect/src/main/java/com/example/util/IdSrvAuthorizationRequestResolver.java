package com.example.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.Tenant;
import com.example.Interceptor.TenantContext;

public class IdSrvAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
	private Logger logger = LoggerFactory.getLogger(IdSrvAuthorizationRequestResolver.class);
	
	private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";
	private static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";
	private final ClientRegistrationRepository clientRegistrationRepository;
	private final AntPathRequestMatcher authorizationRequestMatcher;
	private final StringKeyGenerator stateGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder());

	public IdSrvAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {

		this.clientRegistrationRepository = clientRegistrationRepository;
    	this.authorizationRequestMatcher = new AntPathRequestMatcher(
    			DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
    	String registrationId = this.resolveRegistrationId(request);
    	if (registrationId == null) {
			return null;
		}
		String redirectUriAction = getAction(request, "login");
		return resolve(request, registrationId, redirectUriAction);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(
            HttpServletRequest request, String registrationId) {
    	if (registrationId == null) {
			return null;
		}
		String redirectUriAction = getAction(request, "authorize");
		return resolve(request, registrationId, redirectUriAction);
    }

    private OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId, String redirectUriAction) {
    	ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
		if (clientRegistration == null) {
			throw new IllegalArgumentException("Invalid Client Registration with Id: " + registrationId);
		}

		String serverName = request.getServerName();
		Tenant tenant = TenantContext.GetTenantByDomain(serverName);
		if (tenant == null)
			throw new IllegalArgumentException("Invalid Client Tenant with serverName: " + serverName);

        OAuth2AuthorizationRequest.Builder builder;
		if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(clientRegistration.getAuthorizationGrantType())) {
			builder = OAuth2AuthorizationRequest.authorizationCode();
		} else if (AuthorizationGrantType.IMPLICIT.equals(clientRegistration.getAuthorizationGrantType())) {
			builder = OAuth2AuthorizationRequest.implicit();
		} else {
			throw new IllegalArgumentException("Invalid Authorization Grant Type ("  +
					clientRegistration.getAuthorizationGrantType().getValue() +
					") for Client Registration with Id: " + clientRegistration.getRegistrationId());
		}

		String tenantName = tenant.getTenantName();
		String redirectUriStr = this.expandRedirectUri(request, clientRegistration, redirectUriAction);
		String authUriStr = replaceHostInUrl(clientRegistration.getProviderDetails().getAuthorizationUri(), tenantName);
		System.out.println("----IdSvrAuthorizationRequestResolver resolve tenant: " + tenantName
				+ ", redirectUriStr=" + redirectUriStr
				+ ", authUriStr=" + authUriStr);
        
		//将自定义参数添加到现有OAuth2AuthorizationRequest.additionalParameters
		Map<String, Object> additionalParameters = new HashMap<>();
		additionalParameters.put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId());
		if (registrationId.equalsIgnoreCase(OpenIdConnectConstants.ClientAuthScheme)) {
        	additionalParameters.put("acr_values", "idp%3A" + registrationId);
        	additionalParameters.put("acr_values", "tenant%3A" + tenantName);
        	additionalParameters.put(OpenIdConnectConstants.ClaimTypes_TenantName, tenantName);
        }
        
        OAuth2AuthorizationRequest authorizationRequest = builder
				.clientId(tenant.getClientId())
				.authorizationUri(authUriStr)
				.redirectUri(redirectUriStr)
				.scopes(clientRegistration.getScopes())
				.state(this.stateGenerator.generateKey())
				.additionalParameters(additionalParameters)
				.build();

		return authorizationRequest;
    }
    
    private String getAction(HttpServletRequest request, String defaultAction) {
		String action = request.getParameter("action");
		if (action == null) {
			return defaultAction;
		}
		return action;
	}
    
    
    private String resolveRegistrationId(HttpServletRequest request) {
		if (this.authorizationRequestMatcher.matches(request)) {
			return this.authorizationRequestMatcher
					.extractUriTemplateVariables(request).get(REGISTRATION_ID_URI_VARIABLE_NAME);
		}
		return null;
	}

	private String expandRedirectUri(HttpServletRequest request, ClientRegistration clientRegistration, String action) {
		// Supported URI variables -> baseUrl, action, registrationId
		// Used in -> CommonOAuth2Provider.DEFAULT_REDIRECT_URL = "{baseUrl}/{action}/oauth2/code/{registrationId}"
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("registrationId", clientRegistration.getRegistrationId());
		String baseUrl = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
				.replaceQuery(null)
				.replacePath(request.getContextPath())
				.build()
				.toUriString();
		uriVariables.put("baseUrl", baseUrl);
		if (action != null) {
			uriVariables.put("action", action);
		}
		return UriComponentsBuilder.fromUriString(clientRegistration.getRedirectUriTemplate())
				.buildAndExpand(uriVariables)
				.toUriString();
	}
	
	public static String getUrlHostWithoutPort(String originalURL)
	{
		try {
			URI uri = new URI(originalURL);
			int port = uri.getPort();
			if(port == -1)
				return uri.getHost();
			
			return uri.getHost();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public static String getUrlHostWithPort(String originalURL)
	{
		try {
			URI uri = new URI(originalURL);
			int port = uri.getPort();
			if(port == -1)
				return uri.getHost();
			
			return uri.getHost() + ":" + port;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public static String replaceHostInUrl(String originalURL, String tenantName) {
		try {
			URI uri = new URI(originalURL);
			uri = new URI(uri.getScheme().toLowerCase(), tenantName + "." + uri.getAuthority(), uri.getPath(), uri.getQuery(), uri.getFragment());
			
			return uri.toString();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}

