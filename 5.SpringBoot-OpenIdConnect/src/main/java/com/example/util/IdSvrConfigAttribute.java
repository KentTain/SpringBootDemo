package com.example.util;

import org.springframework.security.access.prepost.PreInvocationAttribute;

public class IdSvrConfigAttribute implements PreInvocationAttribute {

	private final String claimKey;
	private final String claimValue;

	IdSvrConfigAttribute(String claimKey) {
		this(claimKey, null);
	}

	IdSvrConfigAttribute(String claimKey, String claimValue)  {
		this.claimKey = claimKey;
		this.claimValue = claimValue;
	}

	/**
	 * The parameter name of the target argument (must be a Collection) to which filtering
	 * will be applied.
	 *
	 * @return the method parameter name
	 */
	String getClaimKey() {
		return claimKey;
	}
	
	String getClaimValue() {
		return claimValue;
	}

	@Override
	public String toString() {
		return getClaimKey() + "-" + getClaimValue();
	}

	@Override
	public String getAttribute() {
		// TODO Auto-generated method stub
		return null;
	}
}
