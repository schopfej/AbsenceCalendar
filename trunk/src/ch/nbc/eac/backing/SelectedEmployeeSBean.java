/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.backing;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.nbc.eac.dao.DaoFactory;
import ch.nbc.eac.dao.business.EmployeeDao;
import ch.nbc.eac.dao.business.OmDao;
import ch.nbc.eac.model.Employee;
import ch.nbc.jsf12.utils.FacesUtils;

public class SelectedEmployeeSBean implements Serializable {

	private static final long serialVersionUID = 7108597421563590173L;
	private static final Logger logger = Logger.getLogger(SelectedEmployeeSBean.class);

	private List<Integer> selected = new ArrayList<Integer>();
	private UserSBean user;

	public List<Integer> getSelected() {
		logger.info("invoked");
		if (selected.isEmpty()) {
			OmDao omDao = DaoFactory.getOmDao();
			int employeeId = user.getEmployeeId();
			Integer userOuId = omDao.getOuId(employeeId);
			if (userOuId == null) {
				FacesUtils
						.addErrorMessageToContext("OM-Query fehlgeschlagen: Om.getOuId(employeeId)");
			} else {
				EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
				Set<Integer> employeeIds = omDao.getEmployeesOfOu(userOuId);
				Set<Integer> units = omDao.getOuIdsOfOu(userOuId);
				for (Integer unitId : units) {
					employeeIds.addAll(omDao.getEmployeesOfOu(unitId));
				}
				Integer parentOuId = omDao.getParentOuId(userOuId);
				if (parentOuId != null) {
					employeeIds.add(employeeDao.getLeaderIdByOu(parentOuId));
					Set<Integer> nearbyOus = omDao.getOuIdsOfOu(parentOuId);
					for (Integer nearbyOu : nearbyOus) {
						employeeIds.add(employeeDao.getLeaderIdByOu(nearbyOu));
					}
				}
				employeeIds.remove(null);
				selected.addAll(employeeIds);

				EmployeePoolSBean employeePool = (EmployeePoolSBean) FacesUtils
						.getManagedBean("employeePool");
				final Map<Integer, Employee> employeeMap = employeePool.getMap();
				Collections.sort(selected, new Comparator<Integer>() {
					private Collator collator = Collator.getInstance(Locale.GERMAN);
					{
						collator.setStrength(Collator.SECONDARY);
					}

					@Override
					public int compare(Integer oneId, Integer anotherId) {
						Employee oneEmployee = employeeMap.get(oneId);
						Employee anotherEmployee = employeeMap.get(anotherId);
						String oneName = oneEmployee == null ? "" : oneEmployee.getLastname();
						String anotherName = anotherEmployee == null ? "" : anotherEmployee
								.getLastname();
						return collator.compare(oneName, anotherName);
					}
				});
			}
		}
		logger.info("processed");
		return selected;
	}

	public void reset() {
		selected.clear();
	}

	public void setSelected(List<Integer> selected) {
		this.selected = selected;
	}

	public UserSBean getUser() {
		return user;
	}

	public void setUser(UserSBean user) {
		this.user = user;
	}

}
