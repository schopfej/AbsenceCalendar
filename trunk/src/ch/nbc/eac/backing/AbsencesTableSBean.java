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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.application.FacesMessage;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import ch.nbc.eac.dao.DaoFactory;
import ch.nbc.eac.dao.business.CatsDataDao;
import ch.nbc.eac.dao.business.EmployeeDao;
import ch.nbc.eac.dao.business.OmDao;
import ch.nbc.eac.model.DateSpan;
import ch.nbc.eac.model.Employee;
import ch.nbc.jsf12.utils.FacesUtils;

public class AbsencesTableSBean implements Serializable {

	private static final long serialVersionUID = -9083022844940544612L;
	private static final Logger logger = Logger.getLogger(AbsencesTableSBean.class);

	/**
	 * Maximal number of days displayed in the table
	 */
	private static int DAY_SPAN_MAX = 700;

	// referenced backing beans
	private AbsencesTimeRangeSBean timeRange;
	private EmployeePoolSBean employeePool;
	private UserSBean user;

	private List<Date> dateList;
	private Map<Integer, Map<Date, Date>> dates;

	private Collection<DateSpan> yearSpans;
	private Collection<DateSpan> monthSpans;
	private Collection<DateSpan> weekSpans;
	private List<Integer> employeeIds;

	private final List<String> weekdayClasses;

	private int dateRangeStateHash;

	public AbsencesTableSBean() {
		logger.info("invoked");
		weekdayClasses = new ArrayList<String>();
		weekdayClasses.add("");
		weekdayClasses.add("");
		weekdayClasses.add("");
		weekdayClasses.add("");
		weekdayClasses.add("");
		weekdayClasses.add("we");
		weekdayClasses.add("we");
		logger.info("processed");
	}

	public void refresh() {

	}

	public List<Integer> getEmployeeIds() {
		logger.info("invoked");
		if (employeeIds == null || employeeIds.isEmpty()) {
			logger.info("compute");
			EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
			int employeeId = user.getEmployeeId();
			OmDao omDao = DaoFactory.getOmDao();
			Integer topNodeId = omDao.getRootOuId(employeeId);
			List<Integer> employeeIds = new ArrayList<Integer>();
			if (topNodeId != null) {
				employeeIds.addAll(employeeDao.getAllIdByOu(topNodeId));
				final Map<Integer, Employee> employeeMap = employeePool.getMap();
				Collections.sort(employeeIds, new Comparator<Integer>() {
					private Collator collator = Collator.getInstance(Locale.GERMAN);
					{
						collator.setStrength(Collator.SECONDARY);
					}

					@Override
					public int compare(Integer oneId, Integer anotherId) {
						Employee oneEmployee = employeeMap.get(oneId);
						Employee anotherEmployee = employeeMap.get(anotherId);
						String oneName = oneEmployee.getLastname();
						String anotherName = anotherEmployee.getLastname();
						return collator.compare(oneName, anotherName);
					}
				});
			}
			this.employeeIds = employeeIds;
		}
		logger.info("processed");
		return employeeIds;
	}

	public Map<Integer, Map<Date, Date>> getDateMap() {
		logger.info("invoked");
		if (dates == null) {
			logger.info("compute");
			Map<Integer, Map<Date, Date>> map = new HashMap<Integer, Map<Date, Date>>();

			Date begin = timeRange.getBegin();
			Date end = timeRange.getEnd();

			CatsDataDao catsDataDao = DaoFactory.getCatsDataDao();
			Map<Integer, Set<Date>> fetchedMap = catsDataDao.getAbsences(begin, end);

			for (Entry<Integer, Set<Date>> entry : fetchedMap.entrySet()) {
				Integer employeeId = entry.getKey();
				HashMap<Date, Date> employeeDateMap = new HashMap<Date, Date>();
				map.put(employeeId, employeeDateMap);
				for (Date date : entry.getValue()) {
					employeeDateMap.put(date, date);
				}
			}
			dates = map;
		}
		logger.info("processed");
		return dates;
	}

	public List<Date> getDateList() {
		logger.info("invoked");
		if (dateList == null || dateRangeStateHash != timeRange.hashCode()) {
			logger.info("compute");
			List<Date> list = new ArrayList<Date>();

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(timeRange.getBegin());
			calendar = DateUtils.truncate(calendar, Calendar.DATE);

			Date endDate = timeRange.getEnd();
			int i = 0;
			for (Date date = calendar.getTime(); date.before(endDate); date = calendar.getTime()) {
				list.add(date);
				calendar.add(Calendar.DATE, 1);
				if (i++ > DAY_SPAN_MAX) {
					String messageText = "Es werden ab Beginn-Datum maximal 700 Tage angezeigt.";
					FacesUtils.addMessageToContext(messageText, null, FacesMessage.SEVERITY_WARN);
					break;
				}
			}
			generateHeader(list);
			dates = null;
			dateList = list;
			dateRangeStateHash = timeRange.hashCode();
		}
		logger.info("processed");
		return dateList;
	}

	private void generateHeader(List<Date> list) {
		logger.info("invoked");
		Map<Date, DateSpan> yearSpanMap = new HashMap<Date, DateSpan>();
		Map<Date, DateSpan> monthSpanMap = new HashMap<Date, DateSpan>();
		Map<Date, DateSpan> weekSpanMap = new HashMap<Date, DateSpan>();

		Calendar calendar = Calendar.getInstance();
		int firstDayOfWeek = calendar.getFirstDayOfWeek();
		for (Date date : list) {
			Date year = DateUtils.truncate(date, Calendar.YEAR);
			Date month = DateUtils.truncate(date, Calendar.MONTH);

			calendar.setTime(date);
			calendar = DateUtils.truncate(calendar, Calendar.HOUR);
			calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
			Date week = calendar.getTime();

			DateSpan yearSpan = yearSpanMap.get(year);
			if (yearSpan == null) {
				yearSpan = new DateSpan(year);
				yearSpanMap.put(year, yearSpan);
			}
			yearSpan.increment();

			DateSpan monthSpan = monthSpanMap.get(month);
			if (monthSpan == null) {
				monthSpan = new DateSpan(month);
				monthSpanMap.put(month, monthSpan);
			}
			monthSpan.increment();

			DateSpan weekSpan = weekSpanMap.get(week);
			if (weekSpan == null) {
				weekSpan = new DateSpan(week);
				weekSpanMap.put(week, weekSpan);
			}
			weekSpan.increment();
		}

		List<DateSpan> yearSpanList = new ArrayList<DateSpan>(yearSpanMap.values());
		Collections.sort(yearSpanList);
		yearSpans = yearSpanList;
		List<DateSpan> monthSpanList = new ArrayList<DateSpan>(monthSpanMap.values());
		Collections.sort(monthSpanList);
		monthSpans = monthSpanList;
		List<DateSpan> weekSpanList = new ArrayList<DateSpan>(weekSpanMap.values());
		Collections.sort(weekSpanList);
		weekSpans = weekSpanList;
		logger.info("processed");
	}

	public Collection<DateSpan> getYearSpans() {
		logger.info("invoked");
		getDateList();
		logger.info("processed");
		return yearSpans;
	}

	public Collection<DateSpan> getMonthSpans() {
		return monthSpans;
	}

	public Collection<DateSpan> getWeekSpans() {
		return weekSpans;
	}

	public String[] getColumnClasses() {
		logger.info("invoked");
		List<String> list = new ArrayList<String>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timeRange.getBegin());

		// first day in list --> rotate column classes
		int distance = calendar.get(Calendar.DAY_OF_WEEK) + 4;

		for (@SuppressWarnings("unused")
		DateSpan span : weekSpans) {
			list.addAll(weekdayClasses);
		}
		Collections.rotate(list, -distance);
		logger.info("processed");
		return list.toArray(new String[0]);
	}

	public EmployeePoolSBean getEmployeePool() {
		return employeePool;
	}

	public void setEmployeePool(EmployeePoolSBean employeePool) {
		this.employeePool = employeePool;
	}

	public UserSBean getUser() {
		return user;
	}

	public void setUser(UserSBean user) {
		this.user = user;
	}

	public void setTimeRange(AbsencesTimeRangeSBean timeRange) {
		this.timeRange = timeRange;
	}

	public AbsencesTimeRangeSBean getTimeRange() {
		return timeRange;
	}

}
