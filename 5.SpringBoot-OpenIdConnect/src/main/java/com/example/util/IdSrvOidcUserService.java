package com.example.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class IdSrvOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
	private Logger logger = LoggerFactory.getLogger(IdSrvOidcUserService.class);

	private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";
	private final Set<String> userInfoScopes = new HashSet<>(
			Arrays.asList(OidcScopes.PROFILE, OidcScopes.EMAIL, OidcScopes.ADDRESS, OidcScopes.PHONE));
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = new DefaultOAuth2UserService();

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		Assert.notNull(userRequest, "userRequest cannot be null");
		OidcUserInfo userInfo = null;
		if (this.shouldRetrieveUserInfo(userRequest)) {
			OAuth2User oauth2User = this.oauth2UserService.loadUser(userRequest);
			userInfo = new OidcUserInfo(oauth2User.getAttributes());

			// https://openid.net/specs/openid-connect-core-1_0.html#UserInfoResponse

			// 1) The sub (subject) Claim MUST always be returned in the UserInfo Response
			if (userInfo.getSubject() == null) {
				OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE);
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}

			// 2) Due to the possibility of token substitution attacks (see Section 16.11),
			// the UserInfo Response is not guaranteed to be about the End-User
			// identified by the sub (subject) element of the ID Token.
			// The sub Claim in the UserInfo Response MUST be verified to exactly match
			// the sub Claim in the ID Token; if they do not match,
			// the UserInfo Response values MUST NOT be used.
			if (!userInfo.getSubject().equals(userRequest.getIdToken().getSubject())) {
				OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE);
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
		}

		// Set<GrantedAuthority> authorities = Collections.singleton(new
		// OidcUserAuthority(userRequest.getIdToken(), userInfo));
		Set<GrantedAuthority> authorities = new HashSet<>();
		// 1) Fetch the authority information from the protected resource using
		// accessToken
		// 2) Map the authority information to one or more GrantedAuthority's and add it
		// to mappedAuthorities
		String userId = null;
		String userName = null;
		String userTenant = null;
		String userEmail = null;
		String userPhone = null;
		String UserDisplayName = null;
		List<String> roleIds = new ArrayList<String>();
		List<String> roleNames = new ArrayList<String>();
		List<String> authorityIds = new ArrayList<String>();
		Map<String, Object> claims = userInfo.getClaims();
		if (userRequest.getIdToken() != null && userRequest.getIdToken().getClaims().size() > 0)
		{
			claims = userRequest.getIdToken().getClaims();
		}
		for (String key : claims.keySet()) {
			Object claim = claims.get(key);
			if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_Id)) {
				userId = claim.toString();
			} else if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_Name)) {
				userName = claim.toString();
			} else if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_Email)) {
				userEmail = claim.toString();
			} else if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_Phone)) {
				userPhone = claim.toString();
			} else if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_DisplayName)) {
				UserDisplayName = claim.toString();
			} else if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_TenantName)) {
				userTenant = claim.toString();
			} else if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_RoleId)) {
				if (claim instanceof String) {
					roleIds.add(claim.toString());
				} else if (claim instanceof List<?>) {
					roleIds.addAll((List<String>) claim);
				}
			} else if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_RoleName)) {
				if (claim instanceof String) {
					roleNames.add(claim.toString());
				} else if (claim instanceof List<?>) {
					roleNames.addAll((List<String>) claim);
				}
			} else if (key.equalsIgnoreCase(OpenIdConnectConstants.ClaimTypes_AuthorityIds)) {
				if (claim instanceof String) {
					authorityIds.add(claim.toString());
				} else if (claim instanceof List<?>) {
					authorityIds.addAll((List<String>) claim);
				}
			} 
//			else {
//				if (claim instanceof String) {
//					GrantedAuthority authority = new IdSrvGrantedAuthority(key, claim.toString(), userRequest.getIdToken(), userInfo);
//					authorities.add(authority);
//				} else if (claim instanceof List<?>) {
//					List<String> claimList = (List<String>) claim;
//					for (String ca : claimList) {
//						GrantedAuthority authority = new IdSrvGrantedAuthority(key, ca, userRequest.getIdToken(), userInfo);
//						authorities.add(authority);
//					}
//				}
//			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (String ca : authorityIds) {
			GrantedAuthority authority = new IdSrvGrantedAuthority(ca, userRequest.getIdToken(), userInfo);
			authorities.add(authority);
			sb.append(authority.getAuthority() + ", ");
		}
		System.out.println("----IdSrvOidcUserService.loadUser authorities: " + sb.toString());

		// 3) Create a copy of oidcUser but use the mappedAuthorities instead
		IdSrvOidcUser user = new IdSrvOidcUser(authorities, userRequest.getIdToken(), userInfo);
		user.setId(userId);
		user.setName(userName);
		user.setEmail(userEmail);
		user.setPhone(userPhone);
		user.setTenantName(userTenant);
		user.setDisplayName(UserDisplayName);
		user.setRoleIds(roleIds);
		user.setRoleNames(roleNames);
		user.setAuthorityIds(authorityIds);
		return user;
	}

	private boolean shouldRetrieveUserInfo(OidcUserRequest userRequest) {
		// Auto-disabled if UserInfo Endpoint URI is not provided
		if (StringUtils
				.isEmpty(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())) {

			return false;
		}

		// The Claims requested by the profile, email, address, and phone scope values
		// are returned from the UserInfo Endpoint (as described in Section 5.3.2),
		// when a response_type value is used that results in an Access Token being
		// issued.
		// However, when no Access Token is issued, which is the case for the
		// response_type=id_token,
		// the resulting Claims are returned in the ID Token.
		// The Authorization Code Grant Flow, which is response_type=code, results in an
		// Access Token being issued.
		if (AuthorizationGrantType.AUTHORIZATION_CODE
				.equals(userRequest.getClientRegistration().getAuthorizationGrantType())) {

			// Return true if there is at least one match between the authorized scope(s)
			// and UserInfo scope(s)
			return CollectionUtils.containsAny(userRequest.getAccessToken().getScopes(), this.userInfoScopes);
		}

		return false;
	}

	/**
	 * Sets the {@link OAuth2UserService} used when requesting the user info
	 * resource.
	 *
	 * @since 5.1
	 * @param oauth2UserService the {@link OAuth2UserService} used when requesting
	 *                          the user info resource.
	 */
	public final void setOauth2UserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService) {
		Assert.notNull(oauth2UserService, "oauth2UserService cannot be null");
		this.oauth2UserService = oauth2UserService;
	}
}