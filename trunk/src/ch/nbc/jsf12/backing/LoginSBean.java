/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.backing;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ch.nbc.jsf12.auth.AuthenticationProvider;
import ch.nbc.jsf12.exception.AuthenticationException;
import ch.nbc.jsf12.exception.DataSourceException;
import ch.nbc.jsf12.model.Destination;
import ch.nbc.jsf12.model.User;
import ch.nbc.jsf12.utils.FacesUtils;

public class LoginSBean {

	private static final Logger logger = Logger.getLogger(LoginSBean.class);

	// referenced backing beans
	private ConfigABean configBean;
	private PortalSBean portalBean;

	private User user;

	private boolean portalViewMode;

	private AuthenticationProvider provider;

	private String destinationKey;
	private String username;
	private String password;

	public boolean isPortalViewMode() {
		return portalViewMode;
	}

	public String login() {
		logger.info("method doLogin() called");

		Map<String, Destination> destinations = configBean.getDestinationMap();

		Destination destination = destinations.get(this.destinationKey);
		if (destination == null) {
			// unknown destination, check the application configuration
			return "failure";
		}

		String providerName = destination.getProvider();
		String destinationId = destination.getId();
		try {

			provider = AuthenticationProvider.getInstance(providerName, destinationId);
			provider.login(username, password);

			// preparing accessor
			FacesUtils.putSessionObjectByKey("accessor", provider);

			boolean isLoginFine = provider.isValidLogin();

			if (isLoginFine) {
//				UserSBean userBean = FacesUtils.getManagedBean("user", UserSBean.class);
//				userBean.validate();
			}

			return isLoginFine ? "success" : "failure";
		} catch (DataSourceException ex) {
			FacesUtils.addExceptionMessageToContext(ex);
			return "failure";
		} catch (AuthenticationException ex) {
			FacesUtils.addErrorMessageToContext(ex.getMessage());
			return "failure";
		} finally {
			password = "";
		}
	}

	/**
	 * Clean and destroy user session.
	 */
	public void logout() {
		final FacesContext ctx = FacesContext.getCurrentInstance();
		final ExternalContext extCtx = ctx.getExternalContext();

		// redirect to start page
		try {
			extCtx.redirect(".");
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
		// Invalidate HTTP Session
		HttpSession servletSession = (HttpSession) extCtx.getSession(false);
		servletSession.invalidate();
	}

	public AuthenticationProvider getAccessor() {
		return provider;
	}

	public void setDestination(String destination) {
		this.destinationKey = destination;
	}

	public String getDestination() {
		if (destinationKey == null) {
			destinationKey = configBean.getDefaultDestination();
		}
		return destinationKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PortalSBean getPortalBean() {
		return portalBean;
	}

	public void setPortalBean(PortalSBean portalBean) {
		// evaluate and set SAP Portal flag
		String referrer = FacesUtils.getRequestHeaderMap().get("referer");

		// portal view mode is active when the portal integration flag is set once to true or ...
		portalViewMode = portalBean.isPortalIntegration()
				// ... a referrer is coming from a known portal host
				|| (referrer != null && referrer.contains(portalBean.getHostname()));

		if (portalViewMode) {
			portalBean.setPortalIntegration(portalViewMode);
		}

		this.portalBean = portalBean;
	}

	public ConfigABean getConfig() {
		return configBean;
	}

	public void setConfig(ConfigABean config) {
		this.configBean = config;
	}
}