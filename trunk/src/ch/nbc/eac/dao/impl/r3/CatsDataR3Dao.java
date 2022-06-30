/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.dao.impl.r3;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import ch.nbc.eac.dao.business.CatsDataDao;
import ch.nbc.jsf12.auth.R3UserAuthenticationProvider;
import ch.nbc.jsf12.dao.impl.r3.AbstractR3Dao;

public class CatsDataR3Dao extends AbstractR3Dao implements CatsDataDao {

	public CatsDataR3Dao(R3UserAuthenticationProvider accessor) {
		super(accessor);
	}

	@Override
	public Map<Integer, Set<Date>> getAbsences(final Date begin, final Date end) {
		final Map<Integer, Set<Date>> employeeAbsencesMap = new HashMap<Integer, Set<Date>>();
		RfcExecuteTemplate template = new RfcExecuteTemplate("/NOVOBC/A2_APP_CATS_DATA_GET") {

			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				JCoStructure sRoleInfo = importParam.getStructure("ROLE_INFO");
				sRoleInfo.setValue("TYP_CODE", "A");
				String beginDate = formatDateForRfc(begin);
				importParam.setValue("BEGDA", beginDate);
				String endDate = formatDateForRfc(end);
				importParam.setValue("ENDDA", endDate);
			}

			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				JCoTable personalDataTable = tables.getTable("TAB_TIMESHEET");
				if (personalDataTable.isEmpty()) {
					return;
				}
				personalDataTable.firstRow();
				do {
					String hourString = personalDataTable.getString("CATSHOURS").trim();
					try {
						float hours = Float.parseFloat(hourString);
						if (hours < 0.1f) {
							continue;
						}
					} catch (NumberFormatException ex) {
						continue;
					}

					Date workDate = personalDataTable.getDate("WORKDATE");
					int employeeId = personalDataTable.getInt("PERNR");
					Set<Date> employeeDateSet = employeeAbsencesMap.get(employeeId);
					if (employeeDateSet == null) {
						employeeDateSet = new HashSet<Date>();
						employeeAbsencesMap.put(employeeId, employeeDateSet);
					}
					employeeDateSet.add(workDate);
				} while (personalDataTable.nextRow());
			}

		};
		template.execute();
		return employeeAbsencesMap;
	}

}
