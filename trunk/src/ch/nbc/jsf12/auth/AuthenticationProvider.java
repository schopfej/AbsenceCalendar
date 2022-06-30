/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.auth;

import ch.nbc.jsf12.exception.AuthenticationException;

public abstract class AuthenticationProvider {

	/**
	 * Perform an authentication check in the backend system.
	 * 
	 * @param username the name of the user
	 * @param password the password of the user
	 */
	public abstract void login(String username, String password) throws AuthenticationException;

	/**
	 * 
	 * @return true when the call of login passed without an exception.
	 * @throws AuthenticationException
	 */
	public abstract boolean isValidLogin() throws AuthenticationException;

	public static AuthenticationProvider getInstance(String type, String destinationId) throws AuthenticationException {
		AuthenticationProvider provider = null;
		if (R3UserAuthenticationProvider.class.getCanonicalName().equals(type)) {
			provider = new R3UserAuthenticationProvider(destinationId);
		} else if (MockupAuthenticationProvider.class.getCanonicalName().equals(type)) {
			provider = new MockupAuthenticationProvider();
		} else {
			provider = new FailAuthenticationProvider();
		}
		return provider;
	}

}
