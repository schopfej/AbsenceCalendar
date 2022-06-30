/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.auth;

import ch.nbc.jsf12.exception.AuthenticationException;

public class FailAuthenticationProvider extends AuthenticationProvider {

	@Override
	public void login(String username, String password) throws AuthenticationException {
		throw new AuthenticationException(
				"Authentication failed. Please configure a valid destination and authentication provider for the application.");
	}

	@Override
	public boolean isValidLogin() {
		return false;
	}

}
