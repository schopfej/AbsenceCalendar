/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LoggingUtilities;
import com.sap.tc.logging.Severity;

/**
 * By extending the AppenderSkeleton you're able to define your own destination
 * for the log4j log messages. This SapLogAppender writes the log4j messages to
 * the NetWeaver logging API. The appender routes the messages to the NetWeaver
 * logging API by using the SAP API as it meant to be.
 */
public class NetWeaverLogAppender extends AppenderSkeleton {
	/** Name for the category under "\System\Applications\<category_Name>" */
	private String categoryName;

	/**
	 * Writes the logging events of log4j to SAP logging API.
	 * 
	 * @param event
	 *            log4j log event written to log4j logger
	 */
	protected void append(LoggingEvent event) {

		// Text of the log4j message
		String msg = event.getMessage() == null ? null : event.getMessage().toString();

		// The Throwable of this logging event (if there's one)
		Throwable ta = event.getThrowableInformation() == null ? null : event
				.getThrowableInformation().getThrowable();
		// map SAP logging Severity
		int severity = mapSeverity(event.getLevel());

		// write log message
		logToSap(event.getLoggerName(), msg, ta, severity);

	}

	private void logToSap(String loggerName, String msg, Throwable ta, int severity) {
		// Location for the SAP log messages
		Location loc = Location.getLocation(loggerName);
		Category category = Category.getCategory(Category.APPLICATIONS, getCategoryName());
		// in case of a unknown severity -> set severity WARNING and add
		// information
		if (severity == -1) {
			severity = Severity.WARNING;
			msg += " (Couldn't identify severity of log4j-Logging Event!)";
		}

		// log-level DEBUG will be written to default trace
		if (severity == Severity.DEBUG) {
			if (ta == null) {
				loc.logT(severity, msg);
			} else {
				// for log messages with throwables
				loc.traceThrowableT(severity, msg, ta);
			}

			// everything else will be written to the application.log in the
			// configured category
		} else {

			if (ta == null) {
				category.logT(severity, loc, msg);
			} else {
				// for log messages with throwables
				ClassLoader classLoader = getClass().getClassLoader();
				String id = null;
				String dc = LoggingUtilities.getCsnComponentByClassLoader(classLoader);
				String csn = LoggingUtilities.getDcNameByClassLoader(classLoader);
				Object[] args = new Object[0];
				LoggingUtilities.logAndTrace(severity, category, loc, ta, id, dc, csn, msg, args);
			}
		}
	}

	/**
	 * Maps log4j's level to SAP NetWeaver logging severity.
	 * 
	 * @param level
	 *            -object of log4j-LoggingEvent
	 * @return Mapped SAP severity; -1 for levels that couldn't be mapped
	 */
	private int mapSeverity(Level level) {
		switch (level.toInt()) {

		case Priority.DEBUG_INT:
			return Severity.DEBUG;

		case Priority.INFO_INT:
			return Severity.INFO;

		case Priority.WARN_INT:
			return Severity.WARNING;

		case Priority.ERROR_INT:
			return Severity.ERROR;

		case Priority.FATAL_INT:
			return Severity.FATAL;

		default: // unknown log level
			return -1;
		}
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	public void close() {
		// remains empty
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String string) {
		categoryName = string;
	}

}
