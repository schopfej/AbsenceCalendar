/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.backing;

import java.util.TimeZone;

public class TimeZoneABean {

	private String timeZone;

	public String getCurrent() {
		if (timeZone == null) {
			timeZone = TimeZone.getDefault().getID();
		}
		return timeZone;
	}

}
