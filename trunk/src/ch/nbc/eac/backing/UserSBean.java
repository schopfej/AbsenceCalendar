/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.backing;

import java.io.Serializable;

import org.apache.log4j.Logger;

import ch.nbc.eac.dao.DaoFactory;
import ch.nbc.eac.dao.business.EmployeeDao;

public class UserSBean implements Serializable {

	private static final long serialVersionUID = -3855764050814295281L;
	private static final Logger logger = Logger.getLogger(UserSBean.class);

	private int employeeId;

	public int getEmployeeId() {
		logger.info("invoked");
		if (employeeId == 0) {
			EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
			employeeId = employeeDao.getIdByUser();
		}
		logger.info("processed");
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

}
