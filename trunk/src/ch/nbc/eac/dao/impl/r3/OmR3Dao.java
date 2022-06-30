/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.dao.impl.r3;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import ch.nbc.eac.dao.business.OmDao;
import ch.nbc.jsf12.auth.R3UserAuthenticationProvider;
import ch.nbc.jsf12.dao.impl.r3.AbstractR3Dao;

public class OmR3Dao extends AbstractR3Dao implements OmDao {

	private static final Logger logger = Logger.getLogger(OmR3Dao.class);

	public OmR3Dao(R3UserAuthenticationProvider accessor) {
		super(accessor);
	}

	@Override
	public Integer getRootOuId(final int employeeId) {
		final Integer[] value = new Integer[1];
		RfcExecuteTemplate template = new RfcExecuteTemplate("BAPI_ORGUNITEXT_DATA_GET") {
			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				importParam.setValue("PLVAR", "01");
				importParam.setValue("OTYPE", "P");
				importParam.setValue("OBJID", employeeId);
				importParam.setValue("EVALPATH", "P-S-O-O");
				importParam.setValue("EVALDEPTH", 10);
			}

			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				JCoTable personalDataTable = tables.getTable("OBJECTSDATA");
				if (personalDataTable.isEmpty()) {
					return;
				}
				personalDataTable.lastRow();
				String objectType = personalDataTable.getString("OBJECTTYPE");
				if ("O".equals(objectType)) {
					value[0] = personalDataTable.getInt("OBJECT_ID");
				}

				JCoStructure messageStructure = exportParam.getStructure("RETURN");
				String messageType = messageStructure.getString("TYPE");
				if (StringUtils.isNotBlank(messageType)) {
					logger.warn("return message by rfc function: " + getFunctionName() + "\n" + messageStructure);
				}
			}
		};
		template.execute();
		return value[0];
	}

	@Override
	public Integer getOuId(final int employeeId) {
		final Integer[] value = new Integer[1];
		RfcExecuteTemplate template = new RfcExecuteTemplate("BAPI_ORGUNITEXT_DATA_GET") {
			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				importParam.setValue("PLVAR", "01");
				importParam.setValue("OTYPE", "P");
				importParam.setValue("OBJID", employeeId);
				importParam.setValue("EVALPATH", "P-S-O");
				importParam.setValue("EVALDEPTH", 3);
			}

			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				JCoTable personalDataTable = tables.getTable("OBJECTSDATA");
				if (personalDataTable.isEmpty()) {
					return;
				}
				personalDataTable.lastRow();
				String objectType = personalDataTable.getString("OBJECTTYPE");
				if ("O".equals(objectType)) {
					value[0] = personalDataTable.getInt("OBJECT_ID");
				}
			}
		};
		template.execute();
		return value[0];
	}

	@Override
	public Set<Integer> getEmployeesOfOu(final int organisationId) {
		final Set<Integer> set = new HashSet<Integer>();
		RfcExecuteTemplate template = new RfcExecuteTemplate("BAPI_ORGUNITEXT_DATA_GET") {
			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				importParam.setValue("PLVAR", "01");
				importParam.setValue("OTYPE", "O");
				importParam.setValue("OBJID", organisationId);
				importParam.setValue("EVALPATH", "O-S-P");
				importParam.setValue("EVALDEPTH", "3");
			}

			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				JCoTable personalDataTable = tables.getTable("OBJECTSDATA");
				if (personalDataTable.isEmpty()) {
					return;
				}
				personalDataTable.firstRow();
				do {
					String objectType = personalDataTable.getString("OBJECTTYPE");
					if ("P".equals(objectType)) {
						set.add(personalDataTable.getInt("OBJECT_ID"));
					}
				} while (personalDataTable.nextRow());
			}
		};
		template.execute();
		return set;
	}

	@Override
	public Set<Integer> getOuIdsOfOu(final int organisationId) {
		final Set<Integer> set = new HashSet<Integer>();
		RfcExecuteTemplate template = new RfcExecuteTemplate("BAPI_ORGUNITEXT_DATA_GET") {
			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				importParam.setValue("PLVAR", "01");
				importParam.setValue("OTYPE", "O");
				importParam.setValue("OBJID", organisationId);
				importParam.setValue("EVALPATH", "O-S-P");
				importParam.setValue("EVALDEPTH", 2);
			}

			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				JCoTable personalDataTable = tables.getTable("OBJECTSDATA");
				if (personalDataTable.getNumRows() < 2) {
					return;
				}
				personalDataTable.firstRow();
				personalDataTable.nextRow();
				do {
					String objectType = personalDataTable.getString("OBJECTTYPE");
					if ("O".equals(objectType)) {
						set.add(personalDataTable.getInt("OBJECT_ID"));
					}
				} while (personalDataTable.nextRow());
			}
		};
		template.execute();
		return set;
	}

	@Override
	public Integer getParentOuId(final int organisationId) {
		final Integer[] holderArray = new Integer[1];
		RfcExecuteTemplate template = new RfcExecuteTemplate("BAPI_ORGUNITEXT_DATA_GET") {
			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				importParam.setValue("PLVAR", "01");
				importParam.setValue("OTYPE", "O");
				importParam.setValue("OBJID", organisationId);
				importParam.setValue("EVALPATH", "O-O");
				importParam.setValue("EVALDEPTH", 2);
			}

			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				JCoTable personalDataTable = tables.getTable("OBJECTSDATA");
				if (personalDataTable.getNumRows() < 2) {
					return;
				}
				personalDataTable.firstRow();
				personalDataTable.nextRow();
				holderArray[0] = personalDataTable.getInt("OBJECT_ID");
			}
		};
		template.execute();
		return holderArray[0];
	}

}
