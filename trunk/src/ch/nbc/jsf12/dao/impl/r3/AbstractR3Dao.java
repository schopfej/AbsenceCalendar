/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.dao.impl.r3;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.JCoThroughput;

import ch.nbc.jsf12.auth.R3UserAuthenticationProvider;
import ch.nbc.jsf12.exception.DataSourceException;
import ch.nbc.jsf12.model.Message;
import ch.nbc.jsf12.model.Messages;

public abstract class AbstractR3Dao implements DataAccessObject {

	private static final Logger LOGGER = Logger.getLogger(AbstractR3Dao.class);
	private static final Date sapRfcDateMax = new Date(253402297170000l); // "99991231"

	private final R3UserAuthenticationProvider provider;

	public AbstractR3Dao(R3UserAuthenticationProvider provider) {
		this.provider = provider;
	}

	protected static String formatDateForRfc(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("date argument must not be null");
		}
		if (sapRfcDateMax.before(date)) {
			date = sapRfcDateMax;
		}
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}

	protected static boolean isHarmlessException(DataSourceException ex) {
		Throwable cause = ex.getCause();
		if (cause instanceof JCoException) {
			JCoException subEx = (JCoException) cause;
			switch (subEx.getGroup()) {
			case JCoException.JCO_ERROR_SYSTEM_FAILURE:
				// failure in back end system (fall through)
			case JCoException.JCO_ERROR_LOGON_FAILURE:
				// login process failed
				return true;
			default:
				return false;
			}
		}
		return false;
	}

	protected abstract class RfcExecuteTemplate {

		private static final long CALL_WARN_THRESHOLD = 250;

		private final String functionName;

		protected RfcExecuteTemplate(String functionName) {
			this.functionName = functionName;
		}

		public String getFunctionName() {
			return functionName;
		}

		public void enableAbapDebugMode(boolean enable) {
			provider.getDestination().setUseSapGui(enable);
		}

		public void execute() {
			LOGGER.info("execute() invoked");
			JCoDestination destination = provider.getDestination();

			JCoFunction function = null;
			String inputPayload = null;
			try {
				JCoRepository repository = destination.getRepository();
				function = repository.getFunction(functionName);

				if (function != null) {
					JCoParameterList importParameter = function.getImportParameterList();
					JCoParameterList changingParameter = function.getChangingParameterList();
					JCoParameterList tableParameter = function.getTableParameterList();

					before(importParameter, changingParameter, tableParameter);
					inputPayload = function.toXML();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("execute " + function);
					}

					function.execute(destination);
					JCoParameterList exportParameter = function.getExportParameterList();
					after(exportParameter, changingParameter, tableParameter);
				} else {
					String message = "RFC Module " + functionName + " doesn't exist";
					if (destination.getR3Name() != null) {
						message += "on system " + destination.getR3Name();
					}
					message += "!";
					LOGGER.error(message);
					throw new DataSourceException(message);
				}
			} catch (JCoException ex) {
				throw new DataSourceException(ex.getMessage(), null, ex);
			} finally {
				JCoThroughput throughput = destination.getThroughput();
				if (throughput != null && LOGGER.isEnabledFor(Level.WARN)) {
					logThroughput(function, inputPayload, throughput);
					throughput.reset();
				}
				LOGGER.info("execute() leaving");
			}
		}

		private void logThroughput(JCoFunction function, String inputPayload, JCoThroughput throughput) {
			if (LOGGER.isEnabledFor(Level.WARN) && throughput.getTotalTime() > CALL_WARN_THRESHOLD) {
				StringBuilder message = new StringBuilder(512);
				message.append("Execution time (");
				message.append(throughput.getTotalTime());
				message.append(" ms) of RFC Module ");
				message.append(functionName);
				message.append(" is longer than ");
				message.append(CALL_WARN_THRESHOLD);
				message.append(" ms:\n");
				message.append(throughput);
				if (inputPayload != null) {
					message.append("\nJCo Call Payload:\n");
					message.append(inputPayload);
				}
				LOGGER.warn(message.toString());
			} else {
				if (LOGGER.isInfoEnabled()) {
					StringBuilder message = new StringBuilder(512);
					message.append("Throughput of RFC Module ");
					message.append(functionName);
					message.append('\n');
					message.append(throughput);
					if (LOGGER.isDebugEnabled() && inputPayload != null) {
						message.append("\nJCo Call Payload:\n");
						message.append(inputPayload);
					}
					if (LOGGER.isTraceEnabled() && function != null) {
						message.append("\nJCo Return Payload:\n");
						message.append(function.toXML());
					}
					LOGGER.info(message.toString());
				}
			}
		}

		protected Messages getMessagesFromReturnTable(JCoParameterList tables, String tableName) {
			JCoTable returnTable = tables.getTable(tableName);
			Messages messages = new Messages();
			if (returnTable == null) {
				throw new DataSourceException("Table " + tableName + " NOT found");
			}
			LOGGER.info("number of rows in table return: " + returnTable.getNumRows());
			if (returnTable.getNumRows() > 0) {
				returnTable.firstRow();
				do {
					String type = returnTable.getString("TYPE").trim();
					String msg = returnTable.getString("MESSAGE").trim();
					Message message;
					if ("1".equals(type) || "S".equals(type) || "I".equals(type)) {
						message = new Message(Message.TYPE.INFO, msg);
						messages.addMessage(message);
					} else if ("2".equals(type) || "W".equals(type)) {
						message = new Message(Message.TYPE.WARN, msg);
						messages.addMessage(message);
					} else {
						message = new Message(Message.TYPE.ERROR, msg);
						messages.addMessage(message);
					}
				} while (returnTable.nextRow());
			} else {
				LOGGER.debug("Return table " + tableName + " is empty.");
			}
			return messages;
		}

		/**
		 * This method will be processed before the remote function call is executed.
		 * 
		 * @param importParam RFC import parameter list
		 * @param changing    RFC changing list
		 * @param tables      RFC table list
		 * @throws DataSourceException Exceptions that will occur during the processing of this method.
		 */
		protected void before(JCoParameterList importParam, JCoParameterList changing, JCoParameterList tables) {
			// do nothing
		}

		/**
		 * This method will be processed after the remote function call execution.
		 * 
		 * @param exportParam RFC export parameter list
		 * @param changing    RFC changing list
		 * @param tables      RFC table list
		 * @throws DataSourceException Exceptions that will occur during the processing of this method.
		 */
		protected abstract void after(JCoParameterList exportParam, JCoParameterList changing, JCoParameterList tables);
	}

}
