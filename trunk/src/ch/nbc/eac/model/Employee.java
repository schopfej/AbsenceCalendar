/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.model;

import java.util.Date;

public class Employee {

	private final int id;
	private final String firstname;
	private final String lastname;
	private Date birthday;
	private String email;
	private volatile String displayName;

	public Employee(int employeeId, String firstname, String lastname) {
		this.id = employeeId;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		if (displayName == null) {
			StringBuilder sb = new StringBuilder(firstname);
			sb.append(" ");
			sb.append(lastname);
			displayName = sb.toString();
		}
		return displayName;
	}

}
