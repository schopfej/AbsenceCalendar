/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.eac.dao.business;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface CatsDataDao {

	Map<Integer, Set<Date>> getAbsences(Date begin, Date end);

}
