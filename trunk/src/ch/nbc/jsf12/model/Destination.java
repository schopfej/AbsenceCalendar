/**
 * Copyright (C) 2020 - NOVO Business Consultants AG
 * 
 * $LastChangedDate:  $
 * $LastChangedRevision: $
 * $LastChangedBy: $
 */
package ch.nbc.jsf12.model;

public class Destination {

	private final String id;
	private final String provider;
	private String label;

	public Destination(String id, String provider, String label) {
		this.id = id;
		this.provider = provider;
		this.label = label;
	}

	public Destination(String id, String provider) {
		this(id, provider, null);
	}

	public String getId() {
		return id;
	}

	public String getProvider() {
		return provider;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
