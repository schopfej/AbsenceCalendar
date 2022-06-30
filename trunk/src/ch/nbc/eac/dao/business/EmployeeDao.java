/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.dao.business;

import java.util.List;
import java.util.Set;

import ch.nbc.eac.model.Employee;

public interface EmployeeDao {

	List<Employee> getAll();

	Employee getById(final int id);

	Set<Integer> getAllIdByOu(int topNodeId);

	int getIdByUser();

	Integer getLeaderIdByOu(int organisationUnitId);

}
