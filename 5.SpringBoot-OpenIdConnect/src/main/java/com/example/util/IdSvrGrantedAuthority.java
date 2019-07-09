package com.example.util;

import org.springframework.security.core.GrantedAuthority;

public class IdSvrGrantedAuthority implements GrantedAuthority {

    private final String claimKey;

    private final String claimValue;

    public IdSvrGrantedAuthority(String key, String value) {
        this.claimKey = key;
        this.claimValue = value;
    }

    @Override
    public String getAuthority() {
        return claimKey + "-" + claimValue;
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
        if (o == null || getClass() != o.getClass()) return false;

        IdSvrGrantedAuthority target = (IdSvrGrantedAuthority) o;
        if (claimKey.equals(target.getClaimKey()) && claimValue.equals(target.getClaimValue())) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int result = claimKey != null ? claimKey.hashCode() : 0;
        result = 31 * result + (claimValue != null ? claimValue.hashCode() : 0);
        return result;
    }
}
