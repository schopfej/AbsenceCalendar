/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.dao.impl.r3;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import ch.nbc.eac.dao.business.EmployeeDao;
import ch.nbc.eac.model.Employee;
import ch.nbc.jsf12.auth.R3UserAuthenticationProvider;
import ch.nbc.jsf12.dao.impl.r3.AbstractR3Dao;

public class EmployeeR3Dao extends AbstractR3Dao implements EmployeeDao {

	private static final Logger logger = Logger.getLogger(EmployeeR3Dao.class);

	public EmployeeR3Dao(R3UserAuthenticationProvider accessor) {
		super(accessor);
	}

	public List<Employee> getAll() {
		final List<Employee> employees = new ArrayList<Employee>();
		RfcExecuteTemplate template = new RfcExecuteTemplate("BAPI_EMPLOYEE_GETDATA") {
			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				importParam.setValue("USERID", "%");
			}

			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				{
					JCoTable personalDataTable = tables.getTable("PERSONAL_DATA");
					if (personalDataTable.isEmpty()) {
						return;
					}
					personalDataTable.firstRow();
					do {
						logger.info("invoked");
						int employeeId = personalDataTable.getInt("PERNO");
						String firstname = personalDataTable.getString("FIRSTNAME");
						String lastname = personalDataTable.getString("LAST_NAME");
						Date birthday = personalDataTable.getDate("BIRTHDATE");
						Employee employee = new Employee(employeeId, firstname, lastname);
						employee.setBirthday(birthday);
						logger.info("leave");
						employees.add(employee);
					} while (personalDataTable.nextRow());
				}
				// statement and focus separation
				{
					JCoTable communicationTable = tables.getTable("COMMUNICATION");
					if (communicationTable.isEmpty()) {
						return;
					}
					communicationTable.firstRow();
					Map<Integer, String> emailAddresses = new HashMap<Integer, String>();
					do {
						logger.info("invoked");
						String subType = communicationTable.getString("SUBTYPE");
						if (!"0010".equals(subType)) {
							continue;
						}
						int employeeId = communicationTable.getInt("PERNO");
						String emailAddress = communicationTable.getString("USRID_LONG").toLowerCase().intern();
						emailAddresses.put(employeeId, emailAddress);
						logger.info("leave");
					} while (communicationTable.nextRow());
					for (Employee employee : employees) {
						String mailAddress = emailAddresses.get(employee.getId());
						if (StringUtils.isNotBlank(mailAddress)) {
							employee.setEmail(mailAddress);
						}
					}
				}
			}
		};
		template.execute();
		return employees;
	}

	public Employee getById(final int id) {
		final Employee[] employees = new Employee[1];
		RfcExecuteTemplate template = new RfcExecuteTemplate("BAPI_EMPLOYEE_GETDATA") {
			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				importParam.setValue("EMPLOYEE_ID", id);
			}

			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				JCoTable personalDataTable = tables.getTable("PERSONAL_DATA");
				if (personalDataTable.isEmpty()) {
					return;
				}
				personalDataTable.firstRow();
				do {
					int employeeId = personalDataTable.getInt("PERNO");
					String firstname = personalDataTable.getString("FIRSTNAME");
					String lastname = personalDataTable.getString("LAST_NAME");
					Date birthday = personalDataTable.getDate("BIRTHDATE");
					Employee employee = new Employee(employeeId, firstname, lastname);
					employee.setBirthday(birthday);
					employees[0] = employee;
				} while (personalDataTable.nextRow());
				JCoTable communicationTable = tables.getTable("COMMUNICATION");
				if (communicationTable.isEmpty()) {
					return;
				}
				communicationTable.firstRow();
				do {
					logger.info("invoked");
					String subType = communicationTable.getString("SUBTYPE");
					if (!"0010".equals(subType)) {
						continue;
					}
					String email = communicationTable.getString("USRID_LONG");
					employees[0].setEmail(email.toLowerCase().intern());
					logger.info("leave");
				} while (communicationTable.nextRow());

			}
		};
		template.execute();
		return employees[0];
	}

	@Override
	public Set<Integer> getAllIdByOu(final int topNodeId) {
		final Set<Integer> employees = new HashSet<Integer>();
		RfcExecuteTemplate template = new RfcExecuteTemplate("BAPI_ORGUNITEXT_DATA_GET") {
			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				importParam.setValue("PLVAR", "01");
				importParam.setValue("OTYPE", "O");
				importParam.setValue("OBJID", topNodeId);
				importParam.setValue("EVALPATH", "O-S-P");
				importParam.setValue("EVALDEPTH", 10);
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
					if (!"P".equals(objectType)) {
						continue;
					}
					int employeeId = personalDataTable.getInt("OBJECT_ID");
					employees.add(employeeId);
				} while (personalDataTable.nextRow());
			}
		};
		template.execute();
		return employees;
	}

	@Override
	public int getIdByUser() {
		final int[] value = new int[1];
		RfcExecuteTemplate template = new RfcExecuteTemplate("/NOVOBC/A2_USER_DATA_GET") {
			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				value[0] = exportParam.getInt("PERSNO");
			}
		};
		template.execute();
		return value[0];
	}

	/**
	 * This implementation return the first found person as the leading employee.
	 */
	@Override
	public Integer getLeaderIdByOu(final int organisationUnitId) {
		final Integer[] holderArray = new Integer[1];
		RfcExecuteTemplate template = new RfcExecuteTemplate("BAPI_ORGUNITEXT_DATA_GET") {
			@Override
			protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
				importParam.setValue("PLVAR", "01");
				importParam.setValue("OTYPE", "O");
				importParam.setValue("OBJID", organisationUnitId);
				importParam.setValue("EVALPATH", "O-S-P");
				importParam.setValue("EVALDEPTH", 3);
			}

			@Override
			protected void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables) {
				JCoTable personalDataTable = tables.getTable("OBJECTSDATA");
				if (personalDataTable.getNumRows() < 3) {
					return;
				}
				personalDataTable.firstRow();
				personalDataTable.nextRow();
				do {
					String objectType = personalDataTable.getString("OBJECTTYPE");
					if ("P".equals(objectType)) {
						holderArray[0] = personalDataTable.getInt("OBJECT_ID");
						break;
					}
				} while (personalDataTable.nextRow());
			}
		};
		template.execute();
		return holderArray[0];
	}

}
