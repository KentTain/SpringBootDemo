package com.example.util;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

public class IdSrvGrantedAuthority extends OidcUserAuthority {

    private final String claimKey;
    private final String claimValue;
    
    public IdSrvGrantedAuthority(String key, String value, OidcIdToken idToken, OidcUserInfo userInfo) {
    	super(value != null ? key + "-" + value : key, idToken, userInfo);
        this.claimKey = key;
        this.claimValue = value;
    }
    
    public IdSrvGrantedAuthority(String key, OidcIdToken idToken, OidcUserInfo userInfo) {
    	this(key, null, idToken, userInfo);
    }

    public String getClaimKey() {
        return claimKey;
    }

    public String getClaimValue() {
        return claimValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) 
        	return false;
        
        if (!super.equals(o)) {
			return false;
		}

        IdSrvGrantedAuthority target = (IdSrvGrantedAuthority) o;
        if (claimKey.equals(target.getClaimKey()) && claimValue.equals(target.getClaimValue())) 
        	return true;
        return false;
    }

    @Override
    public int hashCode() {
    	int result = super.hashCode();
        result = claimKey != null ? claimKey.hashCode() : 0;
        result = 31 * result + (claimValue != null ? claimValue.hashCode() : 0);
        return result;
    }
}
