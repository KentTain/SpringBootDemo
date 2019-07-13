package com.example.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoderJwkSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.Tenant;
import com.example.Interceptor.TenantContext;

public class IdSrvAuthenticationProvider extends OidcAuthorizationCodeAuthenticationProvider{
	private static final String INVALID_STATE_PARAMETER_ERROR_CODE = "invalid_state_parameter";
	private static final String INVALID_REDIRECT_URI_PARAMETER_ERROR_CODE = "invalid_redirect_uri_parameter";
	private static final String INVALID_ID_TOKEN_ERROR_CODE = "invalid_id_token";
	private static final String MISSING_SIGNATURE_VERIFIER_ERROR_CODE = "missing_signature_verifier";
	private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;
	private final OAuth2UserService<OidcUserRequest, OidcUser> userService;
	private final Map<String, JwtDecoder> jwtDecoders = new ConcurrentHashMap<>();
	private GrantedAuthoritiesMapper authoritiesMapper = (authorities -> authorities);
	
	public IdSrvAuthenticationProvider(
			IdSrvAuthorizationCodeTokenResponseClient accessTokenResponseClient,
			OAuth2UserService<OidcUserRequest, OidcUser> userService) {
		super(accessTokenResponseClient, userService);
		this.accessTokenResponseClient = accessTokenResponseClient;
		this.userService = userService;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		OAuth2LoginAuthenticationToken authorizationCodeAuthentication = (OAuth2LoginAuthenticationToken) authentication;

		// Section 3.1.2.1 Authentication Request - https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest
		// scope
		// 		REQUIRED. OpenID Connect requests MUST contain the "openid" scope value.
		if (!authorizationCodeAuthentication.getAuthorizationExchange()
			.getAuthorizationRequest().getScopes().contains(OidcScopes.OPENID)) {
			// This is NOT an OpenID Connect Authentication Request so return null
			// and let OAuth2LoginAuthenticationProvider handle it instead
			return null;
		}

		OAuth2AuthorizationRequest authorizationRequest = authorizationCodeAuthentication
			.getAuthorizationExchange().getAuthorizationRequest();
		OAuth2AuthorizationResponse authorizationResponse = authorizationCodeAuthentication
			.getAuthorizationExchange().getAuthorizationResponse();

		if (authorizationResponse.statusError()) {
			throw new OAuth2AuthenticationException(
				authorizationResponse.getError(), authorizationResponse.getError().toString());
		}

		if (!authorizationResponse.getState().equals(authorizationRequest.getState())) {
			OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
		}

		if (!authorizationResponse.getRedirectUri().equals(authorizationRequest.getRedirectUri())) {
			OAuth2Error oauth2Error = new OAuth2Error(INVALID_REDIRECT_URI_PARAMETER_ERROR_CODE);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
		}

		OAuth2AccessTokenResponse accessTokenResponse;
		try {
			accessTokenResponse = this.accessTokenResponseClient.getTokenResponse(
					new OAuth2AuthorizationCodeGrantRequest(
							authorizationCodeAuthentication.getClientRegistration(),
							authorizationCodeAuthentication.getAuthorizationExchange()));
		} catch (OAuth2AuthorizationException ex) {
			OAuth2Error oauth2Error = ex.getError();
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
		}
		
		Tenant tenant = TenantContext.getCurrentTenant();
		if (tenant == null)
			throw new IllegalArgumentException("IdSrvAuthenticationProvider throw excepiton: Tenant is null." + authorizationRequest.getRedirectUri());

		ClientRegistration clientRegistration = getClientRegistrationByTenant(authorizationCodeAuthentication.getClientRegistration(), tenant);
		
		Map<String, Object> additionalParameters = accessTokenResponse.getAdditionalParameters();
		if (!additionalParameters.containsKey(OidcParameterNames.ID_TOKEN)) {
			OAuth2Error invalidIdTokenError = new OAuth2Error(
				INVALID_ID_TOKEN_ERROR_CODE,
				"Missing (required) ID Token in Token Response for Client Registration: " + clientRegistration.getRegistrationId(),
				null);
			throw new OAuth2AuthenticationException(invalidIdTokenError, invalidIdTokenError.toString());
		}
		OidcIdToken idToken = createOidcToken(clientRegistration, accessTokenResponse);

		OidcUser oidcUser = this.userService.loadUser(new OidcUserRequest(
				clientRegistration, accessTokenResponse.getAccessToken(), idToken, additionalParameters));
		Collection<? extends GrantedAuthority> mappedAuthorities =
			this.authoritiesMapper.mapAuthorities(oidcUser.getAuthorities());

		OAuth2LoginAuthenticationToken authenticationResult = new OAuth2LoginAuthenticationToken(
			authorizationCodeAuthentication.getClientRegistration(),
			authorizationCodeAuthentication.getAuthorizationExchange(),
			oidcUser,
			mappedAuthorities,
			accessTokenResponse.getAccessToken(),
			accessTokenResponse.getRefreshToken());
		authenticationResult.setDetails(authorizationCodeAuthentication.getDetails());

		return authenticationResult;
	}
	
	private ClientRegistration getClientRegistrationByTenant(ClientRegistration clientReg, Tenant tenant)
	{
		System.out.println(
				clientReg.getClientName() + "----updateClientRegistrationByTenant with tenant: " 
						+ tenant == null
						? clientReg.getClientName()
						: tenant.getTenantName());

		if (tenant == null)
			return clientReg;

		ProviderDetails provider = clientReg.getProviderDetails();
		if (provider == null)
			return clientReg;

		String tenantName = Optional.of(tenant.getTenantName()).orElse(clientReg.getClientName());
		String clientId = Optional.of(tenant.getClientId()).orElse(clientReg.getClientId());
		String clientSecret = Optional.of(tenant.getClientSecret()).orElse(clientReg.getClientSecret());

		return ClientRegistration.withRegistrationId(OpenIdConnectConstants.ClientAuthScheme).clientId(clientId)
				.clientName(tenantName).clientSecret(clientSecret)
				.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE).scope(clientReg.getScopes())
				// .userNameAttributeName("SubjectId")
				.userNameAttributeName(IdTokenClaimNames.SUB)
				// .redirectUriTemplate("{baseUrl}/oidc/signin-callback/{registrationId}")
				.redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
				.authorizationUri(replaceHostInUrl(provider.getAuthorizationUri(), tenantName))
				.tokenUri(replaceHostInUrl(provider.getTokenUri(), tenantName))
				.userInfoUri(replaceHostInUrl(provider.getUserInfoEndpoint().getUri(), tenantName))
				.jwkSetUri(replaceHostInUrl(provider.getJwkSetUri(), tenantName)).build();

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
	
	private OidcIdToken createOidcToken(ClientRegistration clientRegistration, OAuth2AccessTokenResponse accessTokenResponse) {
		JwtDecoder jwtDecoder = getJwtDecoder(clientRegistration);
		Jwt jwt = jwtDecoder.decode((String) accessTokenResponse.getAdditionalParameters().get(
				OidcParameterNames.ID_TOKEN));
		OidcIdToken idToken = new OidcIdToken(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getClaims());
		validateIdToken(idToken, clientRegistration);
		return idToken;
	}

	private JwtDecoder getJwtDecoder(ClientRegistration clientRegistration) {
		JwtDecoder jwtDecoder = this.jwtDecoders.get(clientRegistration.getRegistrationId());
		if (jwtDecoder == null) {
			if (!StringUtils.hasText(clientRegistration.getProviderDetails().getJwkSetUri())) {
				OAuth2Error oauth2Error = new OAuth2Error(
						MISSING_SIGNATURE_VERIFIER_ERROR_CODE,
						"Failed to find a Signature Verifier for Client Registration: '" +
								clientRegistration.getRegistrationId() + "'. Check to ensure you have configured the JwkSet URI.",
						null
				);
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
			jwtDecoder = new NimbusJwtDecoderJwkSupport(clientRegistration.getProviderDetails().getJwkSetUri());
			this.jwtDecoders.put(clientRegistration.getRegistrationId(), jwtDecoder);
		}
		return jwtDecoder;
	}

	private void validateIdToken(OidcIdToken idToken, ClientRegistration clientRegistration) {
		// 3.1.3.7  ID Token Validation
		// https://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation

		// Validate REQUIRED Claims
		URL issuer = idToken.getIssuer();
		if (issuer == null) {
			throwInvalidIdTokenException();
		}
		String subject = idToken.getSubject();
		if (subject == null) {
			throwInvalidIdTokenException();
		}
		List<String> audience = idToken.getAudience();
		if (CollectionUtils.isEmpty(audience)) {
			throwInvalidIdTokenException();
		}
		Instant expiresAt = idToken.getExpiresAt();
		if (expiresAt == null) {
			throwInvalidIdTokenException();
		}
		Instant issuedAt = idToken.getIssuedAt();
		if (issuedAt == null) {
			throwInvalidIdTokenException();
		}

		// 2. The Issuer Identifier for the OpenID Provider (which is typically obtained during Discovery)
		// MUST exactly match the value of the iss (issuer) Claim.
		// TODO Depends on gh-4413

		// 3. The Client MUST validate that the aud (audience) Claim contains its client_id value
		// registered at the Issuer identified by the iss (issuer) Claim as an audience.
		// The aud (audience) Claim MAY contain an array with more than one element.
		// The ID Token MUST be rejected if the ID Token does not list the Client as a valid audience,
		// or if it contains additional audiences not trusted by the Client.
		if (!audience.contains(clientRegistration.getClientId())) {
			throwInvalidIdTokenException();
		}

		// 4. If the ID Token contains multiple audiences,
		// the Client SHOULD verify that an azp Claim is present.
		String authorizedParty = idToken.getAuthorizedParty();
		if (audience.size() > 1 && authorizedParty == null) {
			throwInvalidIdTokenException();
		}

		// 5. If an azp (authorized party) Claim is present,
		// the Client SHOULD verify that its client_id is the Claim Value.
		if (authorizedParty != null && !authorizedParty.equals(clientRegistration.getClientId())) {
			throwInvalidIdTokenException();
		}

		// 7. The alg value SHOULD be the default of RS256 or the algorithm sent by the Client
		// in the id_token_signed_response_alg parameter during Registration.
		// TODO Depends on gh-4413

		// 9. The current time MUST be before the time represented by the exp Claim.
		Instant now = Instant.now();
		if (!now.isBefore(expiresAt)) {
			throwInvalidIdTokenException();
		}

		// 10. The iat Claim can be used to reject tokens that were issued too far away from the current time,
		// limiting the amount of time that nonces need to be stored to prevent attacks.
		// The acceptable range is Client specific.
		Instant maxIssuedAt = now.plusSeconds(30);
		if (issuedAt.isAfter(maxIssuedAt)) {
			throwInvalidIdTokenException();
		}

		// 11. If a nonce value was sent in the Authentication Request,
		// a nonce Claim MUST be present and its value checked to verify
		// that it is the same value as the one that was sent in the Authentication Request.
		// The Client SHOULD check the nonce value for replay attacks.
		// The precise method for detecting replay attacks is Client specific.
		// TODO Depends on gh-4442

	}

	private static void throwInvalidIdTokenException() {
		OAuth2Error invalidIdTokenError = new OAuth2Error(INVALID_ID_TOKEN_ERROR_CODE);
		throw new OAuth2AuthenticationException(invalidIdTokenError, invalidIdTokenError.toString());
	}
}
