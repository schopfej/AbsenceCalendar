/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.dao.business;

import java.util.Set;

public interface OmDao {

	Integer getRootOuId(int employeeId);

	Integer getOuId(int employeeId);

	Set<Integer> getEmployeesOfOu(int organisationId);

	Set<Integer> getOuIdsOfOu(int organisationId);

	Integer getParentOuId(int organisationId);

}
