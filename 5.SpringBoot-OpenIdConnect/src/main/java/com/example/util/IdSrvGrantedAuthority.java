package com.example.util;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

public class IdSrvGrantedAuthority extends OidcUserAuthority {

	private static final long serialVersionUID = 1L;
	private final String authorityId;

    public IdSrvGrantedAuthority(String authorityId, OidcIdToken idToken, OidcUserInfo userInfo) {
    	super(authorityId, idToken, userInfo);
        this.authorityId = authorityId;
    }

    public String getAuthorityId() {
        return authorityId;
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
        if (authorityId.equals(target.getAuthorityId())) 
        	return true;
        return false;
    }

    @Override
    public int hashCode() {
    	int result = super.hashCode();
    	result = 31 * result + authorityId != null ? authorityId.hashCode() : 0;
        return result;
    }
}
