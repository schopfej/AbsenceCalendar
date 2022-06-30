/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.backing;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class DayRBean {

	private final Date yesterday;

	public DayRBean() {
		Date date = DateUtils.addDays(new Date(), -1);
		yesterday = DateUtils.truncate(date, Calendar.DATE);
	}

	public Date getYesterday() {
		return yesterday;
	}

}
