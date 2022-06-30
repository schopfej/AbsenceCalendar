/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.backing;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ch.nbc.eac.model.Employee;

public class EmployeePoolABean {

	public EmployeePoolABean() {
		Cache<Integer, Employee> c = CacheBuilder.newBuilder().expireAfterWrite(36, TimeUnit.HOURS).build();
		pool = c.asMap();
	}

	// cache entries for 36 hours
	private final Map<Integer, Employee> pool;

	public Map<Integer, Employee> getPool() {
		return pool;
	}

}
