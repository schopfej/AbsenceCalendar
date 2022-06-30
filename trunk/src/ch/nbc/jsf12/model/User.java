/**
 * Copyright (C) 2020 - NOVO Business Consultants AG
 * 
 * $LastChangedDate: $
 * $LastChangedRevision: $
 * $LastChangedBy: $
 */
package ch.nbc.jsf12.model;

import ch.nbc.jsf12.exception.AuthenticationException;

public interface User {

	void validate() throws AuthenticationException;

}
