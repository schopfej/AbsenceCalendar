/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.exception;

import ch.nbc.jsf12.model.Messages;

/**
 * An exception class to report data source errors.
 */
public class DataSourceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Messages messages;

	public DataSourceException(String message) {
		super(message);
		this.messages = null;
	}

	public DataSourceException(String message, Messages messages) {
		super(message);
		this.messages = messages;
	}

	public DataSourceException(String message, Messages messages, Throwable throwable) {
		super(message, throwable);
		this.messages = messages;
	}

	public Messages getMessages() {
		return messages;
	}
}
