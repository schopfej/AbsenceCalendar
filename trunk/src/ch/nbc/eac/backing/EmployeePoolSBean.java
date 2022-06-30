/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.backing;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.nbc.eac.dao.DaoFactory;
import ch.nbc.eac.dao.business.EmployeeDao;
import ch.nbc.eac.model.Employee;

public class EmployeePoolSBean implements Serializable {

	private static final long serialVersionUID = -7491365780442649155L;
	private static final Logger logger = Logger.getLogger(EmployeePoolSBean.class);

	private EmployeePoolABean applicationData;

	private final Map<Integer, Employee> poolWrapper = new Map<Integer, Employee>() {

		@Override
		public void clear() {
			applicationData.getPool().clear();
		}

		@Override
		public boolean containsKey(Object key) {
			return applicationData.getPool().containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return applicationData.getPool().containsValue(value);
		}

		@Override
		public Set<Map.Entry<Integer, Employee>> entrySet() {
			return applicationData.getPool().entrySet();
		}

		@Override
		public Employee get(Object key) {
			logger.info("invoked");
			Integer id = null;
			if (key instanceof Integer || key instanceof Long) {
				Number number = (Number) key;
				id = number.intValue();
			} else {
				return null;
			}

			if (size() < 2) {
				EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
				List<Employee> employeeList = employeeDao.getAll();
				for (Employee employee : employeeList) {
					put(employee.getId(), employee);
				}
			}

			Employee employee = applicationData.getPool().get(id);
			if (employee == null) {
				EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
				employee = employeeDao.getById(id);
				if (employee != null) {
					put(id, employee);
				}
			}
			logger.info("processed");
			return employee;
		}

		@Override
		public boolean isEmpty() {
			return applicationData.getPool().isEmpty();
		}

		@Override
		public Set<Integer> keySet() {
			return applicationData.getPool().keySet();
		}

		@Override
		public Employee put(Integer key, Employee value) {
			return applicationData.getPool().put(key, value);

		}

		@Override
		public void putAll(Map<? extends Integer, ? extends Employee> m) {
			applicationData.getPool().putAll(m);
		}

		@Override
		public Employee remove(Object key) {
			return applicationData.getPool().remove(key);
		}

		@Override
		public int size() {
			return applicationData.getPool().size();
		}

		@Override
		public Collection<Employee> values() {
			return applicationData.getPool().values();
		}
	};

	public Map<Integer, Employee> getMap() {
		return poolWrapper;
	}

	public int getSize() {
		return poolWrapper.size();
	}

	public EmployeePoolABean getApplicationData() {
		return applicationData;
	}

	public void setApplicationData(EmployeePoolABean applicationData) {
		this.applicationData = applicationData;
	}

}
