package com.example.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

public class IdSrvOidcUser extends DefaultOAuth2User implements OidcUser {
	/**
	 * Constructs a {@code DefaultOidcUser} using the provided parameters.
	 *
	 * @param authorities the authorities granted to the user
	 * @param idToken the {@link OidcIdToken ID Token} containing claims about the user
	 */
	public IdSrvOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken) {
		this(authorities, idToken, IdTokenClaimNames.SUB);
	}

	/**
	 * Constructs a {@code DefaultOidcUser} using the provided parameters.
	 *
	 * @param authorities the authorities granted to the user
	 * @param idToken the {@link OidcIdToken ID Token} containing claims about the user
	 * @param nameAttributeKey the key used to access the user's &quot;name&quot; from {@link #getAttributes()}
	 */
	public IdSrvOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, String nameAttributeKey) {
		this(authorities, idToken, null, nameAttributeKey);
	}

	/**
	 * Constructs a {@code DefaultOidcUser} using the provided parameters.
	 *
	 * @param authorities the authorities granted to the user
	 * @param idToken the {@link OidcIdToken ID Token} containing claims about the user
	 * @param userInfo the {@link OidcUserInfo UserInfo} containing claims about the user, may be {@code null}
	 */
	public IdSrvOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo) {
		this(authorities, idToken, userInfo, IdTokenClaimNames.SUB);
	}

	/**
	 * Constructs a {@code DefaultOidcUser} using the provided parameters.
	 *
	 * @param authorities the authorities granted to the user
	 * @param idToken the {@link OidcIdToken ID Token} containing claims about the user
	 * @param userInfo the {@link OidcUserInfo UserInfo} containing claims about the user, may be {@code null}
	 * @param nameAttributeKey the key used to access the user's &quot;name&quot; from {@link #getAttributes()}
	 */
	public IdSrvOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
							OidcUserInfo userInfo, String nameAttributeKey) {
		super(authorities, collectClaims(idToken, userInfo), nameAttributeKey);
		this.idToken = idToken;
		this.userInfo = userInfo;
	}

	private String id;
	private String name;
	private String email;
	private String phone;
	private String displayName;
	private String tenantName;
	private List<String> roleIds = new ArrayList<String>();
	private List<String> roleNames = new ArrayList<String>();
	private List<String> authorityIds = new ArrayList<String>();
	
	private final OidcIdToken idToken;
	private final OidcUserInfo userInfo;
	
 	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhone() {
		return this.phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDisplayName() {
		return this.displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTenantName() {
		return this.tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public List<String> getRoleIds() {
		return this.roleIds;
	}
	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}
	
	public List<String> getRoleNames() {
		return this.roleNames;
	}
	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}
	
	public List<String> getAuthorityIds() {
		return this.authorityIds;
	}
	public void setAuthorityIds(List<String> authorityIds) {
		this.authorityIds = authorityIds;
	}
	
	@Override
	public OidcIdToken getIdToken() {
		return this.idToken;
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return this.userInfo;
	}

	@Override
	public Map<String, Object> getClaims() {
		return super.getAttributes();
	}
	
	private static Map<String, Object> collectClaims(OidcIdToken idToken, OidcUserInfo userInfo) {
		Assert.notNull(idToken, "idToken cannot be null");
		Map<String, Object> claims = new HashMap<>();
		if (userInfo != null) {
			claims.putAll(userInfo.getClaims());
		}
		claims.putAll(idToken.getClaims());
		return claims;
	}
}
