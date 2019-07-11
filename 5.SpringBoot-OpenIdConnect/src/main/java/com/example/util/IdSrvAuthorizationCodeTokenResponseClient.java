package com.example.util;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.TenantInfo;
import com.example.Interceptor.TenantContext;

@Component
public class IdSrvAuthorizationCodeTokenResponseClient  implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {
	private Logger logger = LoggerFactory.getLogger(IdSrvAuthorizationCodeTokenResponseClient.class);
	private static final String INVALID_TOKEN_RESPONSE_ERROR_CODE = "invalid_token_response";

	private RestOperations restOperations;

	public IdSrvAuthorizationCodeTokenResponseClient() {
		RestTemplate restTemplate = new RestTemplate(Arrays.asList(
				new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		this.restOperations = restTemplate;
	}

	@Override
	public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
		Assert.notNull(authorizationCodeGrantRequest, "authorizationCodeGrantRequest cannot be null");

		ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
		if (clientRegistration == null) {
			throw new IllegalArgumentException("IdSrvAuthorizationCodeTokenResponseClient Invalid Token Client Registration with getGrantType: " + authorizationCodeGrantRequest.getGrantType());
		}

		TenantInfo tenant = TenantContext.getCurrentTenant();
		if (tenant == null)
			throw new IllegalArgumentException("IdSrvAuthorizationCodeTokenResponseClient Invalid Token Client Tenant.");

		RequestEntity<?> request = convert(authorizationCodeGrantRequest, tenant);

		ResponseEntity<OAuth2AccessTokenResponse> response;

		System.out.println("----IdSrvAuthorizationCodeTokenResponseClient getTokenResponse tenant: " + tenant.getTenantId()
				+ ", requestUrl=" + request.getUrl());
		
		try {
			response = this.restOperations.exchange(request, OAuth2AccessTokenResponse.class);
		} catch (RestClientException ex) {
			OAuth2Error oauth2Error = new OAuth2Error(INVALID_TOKEN_RESPONSE_ERROR_CODE,
					"An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: " + ex.getMessage(), null);
			throw new OAuth2AuthorizationException(oauth2Error, ex);
		}

		OAuth2AccessTokenResponse tokenResponse = response.getBody();

		if (CollectionUtils.isEmpty(tokenResponse.getAccessToken().getScopes())) {
			// As per spec, in Section 5.1 Successful Access Token Response
			// https://tools.ietf.org/html/rfc6749#section-5.1
			// If AccessTokenResponse.scope is empty, then default to the scope
			// originally requested by the client in the Token Request
			tokenResponse = OAuth2AccessTokenResponse.withResponse(tokenResponse)
					.scopes(authorizationCodeGrantRequest.getClientRegistration().getScopes())
					.build();
		}

		return tokenResponse;
	}
	
	private RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest, TenantInfo tenant) {
		ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();

		HttpHeaders headers = getTokenRequestHeaders(clientRegistration, tenant);
		MultiValueMap<String, String> formParameters = this.buildFormParameters(authorizationCodeGrantRequest, tenant);
		String tokenUrl = replaceHostInUrl(clientRegistration.getProviderDetails().getTokenUri(), tenant.getTenantId());
		URI uri = UriComponentsBuilder.fromUriString(tokenUrl)
				.build()
				.toUri();

		return new RequestEntity<>(formParameters, headers, HttpMethod.POST, uri);
	}
	
	private MultiValueMap<String, String> buildFormParameters(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest, TenantInfo tenant) {
		ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
		OAuth2AuthorizationExchange authorizationExchange = authorizationCodeGrantRequest.getAuthorizationExchange();

		MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
		formParameters.add(OAuth2ParameterNames.GRANT_TYPE, authorizationCodeGrantRequest.getGrantType().getValue());
		formParameters.add(OAuth2ParameterNames.CODE, authorizationExchange.getAuthorizationResponse().getCode());
		formParameters.add(OAuth2ParameterNames.REDIRECT_URI, authorizationExchange.getAuthorizationRequest().getRedirectUri());
		if (ClientAuthenticationMethod.POST.equals(clientRegistration.getClientAuthenticationMethod())) {
			formParameters.add(OAuth2ParameterNames.CLIENT_ID, tenant.getClientId());
			formParameters.add(OAuth2ParameterNames.CLIENT_SECRET, tenant.getClientSecret());
		}

		return formParameters;
	}

	private HttpHeaders getTokenRequestHeaders(ClientRegistration clientRegistration, TenantInfo tenant) {
		HttpHeaders headers = new HttpHeaders();
		HttpHeaders defaultHeader = getDefaultTokenRequestHeaders();
		headers.addAll(defaultHeader);
		if (ClientAuthenticationMethod.BASIC.equals(clientRegistration.getClientAuthenticationMethod())) {
			headers.setBasicAuth(tenant.getClientId(), tenant.getClientSecret());
		}
		return headers;
	}
	private HttpHeaders getDefaultTokenRequestHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
		final MediaType contentType = MediaType.valueOf(APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
		headers.setContentType(contentType);
		return headers;
	}
	
	public static String replaceHostInUrl(String originalURL, String tenantName) {
		try {
			if(originalURL.contains("?")) {
				originalURL += "&acr_values=idp%3A" + OpenIdConnectConstants.ClientAuthScheme + "&acr_values=tenant%3A" + tenantName + "&tenantName=" + tenantName;
			}
			else {
				originalURL += "?acr_values=idp%3A" + OpenIdConnectConstants.ClientAuthScheme + "&acr_values=tenant%3A" + tenantName + "&tenantName=" + tenantName;
			}

			URI uri = new URI(originalURL);
			uri = new URI(uri.getScheme().toLowerCase(), tenantName + "." + uri.getAuthority(), uri.getPath(), uri.getQuery(), uri.getFragment());
			
			return uri.toString();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * Sets the {@link RestOperations} used when requesting the OAuth 2.0 Access Token Response.
	 *
	 * <p>
	 * <b>NOTE:</b> At a minimum, the supplied {@code restOperations} must be configured with the following:
	 * <ol>
	 *  <li>{@link HttpMessageConverter}'s - {@link FormHttpMessageConverter} and {@link OAuth2AccessTokenResponseHttpMessageConverter}</li>
	 *  <li>{@link ResponseErrorHandler} - {@link OAuth2ErrorResponseErrorHandler}</li>
	 * </ol>
	 *
	 * @param restOperations the {@link RestOperations} used when requesting the Access Token Response
	 */
	public void setRestOperations(RestOperations restOperations) {
		Assert.notNull(restOperations, "restOperations cannot be null");
		this.restOperations = restOperations;
	}
}
