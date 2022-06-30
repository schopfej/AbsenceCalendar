/**
 * Copyright (C) 2012 - NOVO Business Consultants AG
 * 
 * $LastChangedDate: $
 * $LastChangedRevision: $
 * $LastChangedBy: $
 */
package ch.nbc.jsf12.dao.impl.r3;

public interface DataAccessObject {

	/**
	 * Must be implemented.
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * Must be implemented.
	 */
	@Override
	int hashCode();
}
