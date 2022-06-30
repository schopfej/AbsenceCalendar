/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.auth;

import java.util.Locale;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoCustomDestination;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;

import ch.nbc.jsf12.exception.AuthenticationException;
import ch.nbc.jsf12.utils.FacesUtils;

/**
 * An implementation of the abstract <code>AuthenticationProvider</code> class that reads data from and writes data to a
 * SAP ABAP system.
 */
public class R3UserAuthenticationProvider extends AuthenticationProvider {

	private static final Logger logger = Logger.getLogger(R3UserAuthenticationProvider.class);

	// private volatile Date validLoginSince;
	private final String language;
	private final transient JCoDestination baseDestination;
	private transient JCoCustomDestination workDestination;

	public R3UserAuthenticationProvider(String destination) throws AuthenticationException {
		Locale viewLocale = FacesUtils.getViewLocale();
		language = viewLocale.getLanguage().toUpperCase();
		try {
			baseDestination = JCoDestinationManager.getDestination(destination);
			if (logger.isInfoEnabled()) {
				logger.info(baseDestination.getProperties());
			}
		} catch (JCoException ex) {
			throw new AuthenticationException(ex.getMessage());
		}
	}

	public String getLanguage() {
		return language;
	}

	public boolean isValidLogin() {
		try {
			if (workDestination == null) {
				return false;
			}
			workDestination.ping();
			return true;
		} catch (JCoException ex) {
			logger.error("Key: " + ex.getKey() + ", Group: " + ex.getGroup());
			logger.error(ex.getMessage(), ex);
			return false;
		}
	}

	public JCoCustomDestination getDestination() {
		return workDestination;
	}

	private void createJCoClient(String username, String password) throws JCoException {
		logger.info("method createJCoClient() called");

		JCoCustomDestination customDestination = baseDestination.createCustomDestination();
		JCoCustomDestination.UserData logonData = customDestination.getUserLogonData();
		if (AccessPhaseListener.SSO_USERNAME.equals(username)) {
			logonData.setSSOTicket(password);
		} else {
			logonData.setUser(username);
			logonData.setPassword(password);
		}
		logonData.setLanguage(language);
		customDestination.ping();
		customDestination.setThroughput(JCo.createThroughput());
		workDestination = customDestination;
	}

	public synchronized void login(String username, String password) throws AuthenticationException {
		logger.info("method checkUser() called for user " + username);
		try {
			workDestination = null;
			createJCoClient(username, password);
			if (workDestination == null) {
				throw new AuthenticationException("workDestination is not initialized.");
			}
		} catch (JCoException ex) {
			StringBuilder sb = new StringBuilder();
			sb.append(ex.getMessage());
			sb.append(" [");
			sb.append(ex.getKey());
			sb.append(" - ");
			sb.append(ex.getGroup());
			sb.append("]");
			String message = sb.toString();
			logger.error(message, ex);
			throw new AuthenticationException(message);
		} catch (Exception ex) {
			logger.error("Exception: " + ex.getMessage());
			throw new AuthenticationException(ex.getMessage());
		}
		logger.info("login for user " + username + " was successful");
	}

}
