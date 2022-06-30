/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.auth;

import java.io.IOException;

import javax.el.ELException;
import javax.el.PropertyNotFoundException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import ch.nbc.jsf12.backing.ConfigABean;
import ch.nbc.jsf12.backing.LoginSBean;
import ch.nbc.jsf12.backing.MessagesSBean;
import ch.nbc.jsf12.exception.AuthenticationException;
import ch.nbc.jsf12.exception.AuthorizationException;
import ch.nbc.jsf12.model.Message;
import ch.nbc.jsf12.utils.FacesUtils;

public class AccessPhaseListener implements PhaseListener {

	private static final long serialVersionUID = 165619359786330141L;

	private static final Logger logger = Logger.getLogger(AccessPhaseListener.class);

	private static final String LOGIN_VIEW_ID = "/login.jspx";
	private static final String MESSAGE_VIEW_ID = "/applicationMessages.jspx";
	private static final String INFO_VIEW_ID = "/applicationInfo.jspx";

	private static final String SSO_TICKET_NAME = "MYSAPSSO2";

	protected static final String SSO_USERNAME = "$" + SSO_TICKET_NAME + "$";

	public static final String TICKET_HASH_KEY = "ticketHash";

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	@Override
	public void beforePhase(final PhaseEvent event) {
		// remains empty
	}

	@Override
	public void afterPhase(final PhaseEvent event) {
		verifyAccess(event);
	}

	private void verifyAccess(final PhaseEvent event) {
		if (logger.isInfoEnabled()) {
			logger.info("invoked");
		}

		try {
			FacesContext facesContext = event.getFacesContext();
			UIViewRoot viewRoot = facesContext.getViewRoot();
			if (viewRoot == null) {
				return;
			}
			String viewId = viewRoot.getViewId();
			if (logger.isDebugEnabled()) {
				logger.debug("viewId: " + viewId + ", " + event.getPhaseId());
			}
			if (LOGIN_VIEW_ID.equals(viewId) || MESSAGE_VIEW_ID.equals(viewId) || INFO_VIEW_ID.equals(viewId)
					|| viewId.startsWith("/org/richfaces/")) {
				logger.info("permission granted");
				return;
			}

			logger.debug("validate access to restricted resources");
			AuthenticationProvider accessor = getAccessor();
			Integer ticketHash = (Integer) FacesUtils.getSessionObjectByKey(TICKET_HASH_KEY);
			if (ticketHash != null) {
				logger.debug("validate mysap cookie ticket");
				processSsoCookie(facesContext, accessor);
				logger.debug("login and user init was SUCCESSFUL");
			} else if (accessor.isValidLogin()) {
				logger.debug("has valid login: proceed with page access");
//				FacesUtils.getManagedBean("user", UserSBean.class).validate();
			} else {
				logger.debug("try sso login");
				processSsoCookie(facesContext, accessor);
				logger.debug("login and user init was SUCCESSFUL");
			}

		} catch (AuthenticationException ex) {
			logger.error("Die Authentifizierung am Backend-System ist fehlgeschlagen.");
			LoginSBean loginBean = FacesUtils.getManagedBean("login", LoginSBean.class);
			if (loginBean.isPortalViewMode()) {
				Message message = new Message(Message.TYPE.ERROR, ex.getMessage());
				addToMessageBean(message);
				gotoMessageView();
			} else {
				logger.error(ex.getMessage(), ex);
				FacesUtils.addErrorMessageToContext("Sitzung terminiert.");
				FacesContext ctx = FacesContext.getCurrentInstance();
				ExternalContext eCtx = ctx.getExternalContext();
				HttpServletResponse response = (HttpServletResponse) eCtx.getResponse();
				try {
					response.sendRedirect(".");
					ctx.responseComplete();
					logger.info("successful redirect");
				} catch (IOException ioEx) {
					logger.error(ioEx.getMessage(), ioEx);
				}
			}
		} catch (AuthorizationException ex) {
			logger.error("Ungenügende Backend-Berechtigungen");
			Message message = new Message(Message.TYPE.ERROR, ex.getMessage());
			addToMessageBean(message);
			gotoMessageView();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		if (logger.isInfoEnabled()) {
			logger.info("processed");
		}
	}

	private void addToMessageBean(Message message) {
		try {
			MessagesSBean messageBean = FacesUtils.getManagedBean("messages", MessagesSBean.class);
			messageBean.getMessages().add(message);
		} catch (NullPointerException ex) {
			// catch silently
		} catch (PropertyNotFoundException ex) {
			// catch silently
		} catch (ELException ex) {
			// catch silently
		}
	}

	private void gotoMessageView() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application application = facesContext.getApplication();
		NavigationHandler navigationHandler = application.getNavigationHandler();
		navigationHandler.handleNavigation(facesContext, null, "toMessagesView");
	}

	public static AuthenticationProvider getAccessor() throws AuthenticationException {
		AuthenticationProvider accessor = (AuthenticationProvider) FacesUtils.getSessionObjectByKey("accessor");
		if (accessor == null) {
			ConfigABean configBean = FacesUtils.getManagedBean("config", ConfigABean.class);
			String destinationId = configBean.getDefaultDestination();
			String providerName = configBean.getDestinationMap().get(destinationId).getProvider();
			accessor = AuthenticationProvider.getInstance(providerName, destinationId);
			FacesUtils.putSessionObjectByKey("accessor", accessor);
		}
		return accessor;
	}

	private void processSsoCookie(FacesContext facesContext, AuthenticationProvider accessor)
			throws AuthenticationException, AuthorizationException {
		if (logger.isInfoEnabled()) {
			logger.info("invoked");
		}

		Cookie[] cookies = ((HttpServletRequest) (facesContext.getExternalContext()).getRequest()).getCookies();
		Cookie ssoTicketCookie = null;
		Integer oldCookieHashCode = (Integer) FacesUtils.getSessionObjectByKey(TICKET_HASH_KEY);
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (SSO_TICKET_NAME.equals(cookie.getName())) {
					ssoTicketCookie = cookie;
					break;
				}
			}
		}

		if (ssoTicketCookie == null) {
			if (oldCookieHashCode != null) {
				FacesUtils.removeSessionObjectByKey(TICKET_HASH_KEY);
			}
			throw new AuthenticationException("No SSO Ticket found.");
		}

		String ssoTicketCookieVaule = ssoTicketCookie.getValue();
		if (StringUtils.isBlank(ssoTicketCookieVaule)) {
			throw new AuthenticationException("SSO Ticket Value is blank.");
		}

		Integer newCookieHashCode = ssoTicketCookieVaule.hashCode();
		if (!newCookieHashCode.equals(oldCookieHashCode)) {
			// try to authenticate
			String ticketString = unescapeTicket(ssoTicketCookieVaule);
			logger.info("Cookie '" + SSO_TICKET_NAME + "' found");
			logger.trace("SSO Ticket Value: " + ticketString);
			accessor.login(SSO_USERNAME, ticketString);

			resetSession();

			FacesUtils.putSessionObjectByKey("accessor", accessor);
			FacesUtils.putSessionObjectByKey(TICKET_HASH_KEY, newCookieHashCode);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Is the same SSO Ticket as before.");
			}
		}
//		FacesUtils.getManagedBean("user", UserSBean.class).validate();
		if (logger.isInfoEnabled()) {
			logger.info("processed");
		}
	}

	public void resetSession() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getExternalContext().getSessionMap().clear();
	}

	/**
	 * Convert <code>%</code><i>hh</i> sequences to single characters.
	 * 
	 * @param value A string that may contain <code>%</code><i>hh</i> sequences.
	 * @return The unescaped string.
	 */
	private static String unescapeTicket(String value) {
		int len = value.length();
		StringBuilder b = new StringBuilder(512);
		for (int i = 0; i < len; ++i) {
			char c = value.charAt(i);
			if (c == '%' && i + 2 < len) {
				Integer v = Integer.decode("0x" + value.substring(i + 1, i + 3));
				c = (char) v.byteValue();
				i += 2;
			}
			b.append(c);
		}
		return b.toString();
	}

}
