/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.model;

import java.io.Serializable;
import java.util.Date;

public class DateSpan implements Comparable<DateSpan>, Serializable {

	private static final long serialVersionUID = -2930456056037717119L;

	private final Date date;
	private int length;

	public DateSpan(Date date) {
		this.date = date;
	}

	public int getLength() {
		return length;
	}

	public void increment() {
		length++;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public int compareTo(DateSpan other) {
		return date.compareTo(other.getDate());
	}

}
