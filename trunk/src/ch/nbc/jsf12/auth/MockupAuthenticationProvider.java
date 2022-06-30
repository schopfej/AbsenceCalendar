/**
 * Copyright (C) 2017 - NOVO Business Consultants AG
 * 
 * $LastChangedDate: 2017-09-25 11:28:22 +0200 (Mo, 25 Sep 2017) $
 * $LastChangedRevision: 4184 $
 * $LastChangedBy: bes02 $
 */
package ch.nbc.jsf12.auth;

import org.apache.log4j.Logger;

import ch.nbc.jsf12.exception.AuthenticationException;

public class MockupAuthenticationProvider extends AuthenticationProvider {

	private static final Logger logger = Logger.getLogger(MockupAuthenticationProvider.class);

	@Override
	public void login(String username, String password) throws AuthenticationException {
		logger.info("login invoked");
	}

	@Override
	public boolean isValidLogin() {
		logger.info("isValidLogin invoked");
		return false;
	}

}
