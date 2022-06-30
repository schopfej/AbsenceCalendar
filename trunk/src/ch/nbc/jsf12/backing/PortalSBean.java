/**
 * Copyright (C) 2020 - NOVO Business Consultants AG
 * 
 * $LastChangedDate: $
 * $LastChangedRevision: $
 * $LastChangedBy: $
 */
package ch.nbc.jsf12.backing;

public class PortalSBean {

	private boolean portalIntegration;

	public String getHostname() {
		String sysNam = System.getProperty("SAPSYSTEMNAME");
		if (sysNam == null || sysNam.startsWith("EP")) {
			return "s005";
		} else if (sysNam.startsWith("PP")) {
			return "s006";
		}
		return "";
	}

	public boolean isPortalIntegration() {
		return portalIntegration;
	}

	public void setPortalIntegration(boolean portalIntegration) {
		this.portalIntegration = portalIntegration;
	}

}
