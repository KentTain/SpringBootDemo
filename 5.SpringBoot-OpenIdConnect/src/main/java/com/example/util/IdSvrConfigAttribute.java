package com.example.util;

import org.springframework.security.access.prepost.PreInvocationAttribute;

import com.example.StringExtensions;

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
		if(!StringExtensions.isNullOrEmpty(claimKey) && !StringExtensions.isNullOrEmpty(claimValue))
			return claimKey + "-" + claimValue;
		
		if(!StringExtensions.isNullOrEmpty(claimKey))
			return claimKey;
		
		return "";
	}

	@Override
	public String getAttribute() {
		return this.toString();
	}
}
