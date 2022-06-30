/**
 * Copyright (C) 2012 - NOVO Business Consultants AG
 * 
 * $LastChangedDate: 2017-09-25 11:28:22 +0200 (Mo, 25 Sep 2017) $
 * $LastChangedRevision: 4184 $
 * $LastChangedBy: bes02 $
 */
package ch.nbc.jsf12.exception;

/**
 * An exception class to report authorization errors.
 */
public class AuthorizationException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthorizationException(String message) {
		super(message);
	}
}
