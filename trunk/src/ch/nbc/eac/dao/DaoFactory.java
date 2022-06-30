/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.dao;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import ch.nbc.eac.dao.business.CatsDataDao;
import ch.nbc.eac.dao.business.EmployeeDao;
import ch.nbc.eac.dao.business.OmDao;
import ch.nbc.eac.dao.impl.r3.CatsDataR3Dao;
import ch.nbc.eac.dao.impl.r3.EmployeeR3Dao;
import ch.nbc.eac.dao.impl.r3.OmR3Dao;
import ch.nbc.jsf12.auth.R3UserAuthenticationProvider;

public class DaoFactory {

	public static EmployeeDao getEmployeeDao() {
		Object sessionAccessor = getSessionAccessor();
		EmployeeR3Dao dao = null;
		if (sessionAccessor instanceof R3UserAuthenticationProvider) {
			R3UserAuthenticationProvider accessor = (R3UserAuthenticationProvider) sessionAccessor;
			dao = new EmployeeR3Dao(accessor);
		}
		return dao;
	}

	public static CatsDataDao getCatsDataDao() {
		Object sessionAccessor = getSessionAccessor();
		CatsDataDao dao = null;
		if (sessionAccessor instanceof R3UserAuthenticationProvider) {
			R3UserAuthenticationProvider accessor = (R3UserAuthenticationProvider) sessionAccessor;
			dao = new CatsDataR3Dao(accessor);
		}
		return dao;
	}

	private static Object getSessionAccessor() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext eCtx = ctx.getExternalContext();
		return eCtx.getSessionMap().get("accessor");
	}

	public static OmDao getOmDao() {
		Object sessionAccessor = getSessionAccessor();
		OmDao dao = null;
		if (sessionAccessor instanceof R3UserAuthenticationProvider) {
			R3UserAuthenticationProvider accessor = (R3UserAuthenticationProvider) sessionAccessor;
			dao = new OmR3Dao(accessor);
		}
		return dao;
	}

}
