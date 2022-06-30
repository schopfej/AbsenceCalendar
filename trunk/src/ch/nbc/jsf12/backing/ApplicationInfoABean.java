/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.backing;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class ApplicationInfoABean {

	public Set<Locale> getSupportedLocales() {
		Set<Locale> locales = new HashSet<Locale>();
		FacesContext ctx = FacesContext.getCurrentInstance();
		Application application = ctx.getApplication();

		for (Iterator<Locale> i = application.getSupportedLocales(); i.hasNext();) {
			Locale locale = i.next();
			locales.add(locale);
		}
		locales.add(application.getDefaultLocale());
		return locales;
	}

	public String getWorkingDirectory() {
		return new File("").getAbsolutePath();
	}

	public Date getNow() {
		return new Date();
	}
}
