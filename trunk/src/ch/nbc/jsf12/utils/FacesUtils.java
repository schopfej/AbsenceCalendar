/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.utils;


import java.text.Collator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.collections.CollectionUtils;

import ch.nbc.jsf12.model.Message;
import ch.nbc.jsf12.model.Messages;

public class FacesUtils {

	private FacesUtils() {
		// Hide Constructor
	}

	public static void addMessagesToContext(Messages messages, boolean evalError, boolean evalWarn, boolean evalInfo) {
		addMessagesToContext(messages, null, evalError, evalWarn, evalInfo);
	}

	public static void addMessagesToContext(Messages messages, boolean evalError, boolean evalWarn) {
		addMessagesToContext(messages, null, evalError, evalWarn, false);
	}

	public static void addMessagesToContext(Messages messages, String componentId, boolean evalError,
			boolean evalWarn) {
		addMessagesToContext(messages, componentId, evalError, evalWarn, false);
	}

	public static void addMessagesToContext(Messages messages, String componentId, boolean evalError, boolean evalWarn,
			boolean evalInfo) {

		if (messages == null) {
			return;
		}
		Collection<Message> allMessages = messages.getMessages();
		if (CollectionUtils.isNotEmpty(allMessages)) {
			for (Message message : allMessages) {
				String messageText = message.getText();
				Severity severity = null;
				if (message.getType() == Message.TYPE.ERROR && evalError) {
					severity = FacesMessage.SEVERITY_ERROR;
				} else if (message.getType() == Message.TYPE.WARN && evalWarn) {
					severity = FacesMessage.SEVERITY_WARN;
				} else if (message.getType() == Message.TYPE.INFO && evalInfo) {
					severity = FacesMessage.SEVERITY_INFO;
				} else if (message.getType() == Message.TYPE.FATAL) {
					severity = FacesMessage.SEVERITY_FATAL;
				} else {
					break;
				}
				addMessageToContext(messageText, componentId, severity);
			}
		}
	}

	public static void addMessageToContext(String messageText, String componentId, Severity severity) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Iterator<FacesMessage> iterator = ctx.getMessages(null);
		while (iterator.hasNext()) {
			FacesMessage message = iterator.next();
			if (message.getSummary().equals(messageText)) {
				return;
			}
		}
		FacesMessage facesMessage = new FacesMessage(severity, messageText, messageText);
		ctx.addMessage(componentId, facesMessage);
	}

	public static void addErrorMessageToContext(String messageText) {
		addErrorMessageToContext(messageText, null);
	}

	public static void addExceptionMessageToContext(Throwable tr) {
		addErrorMessageToContext(tr.getMessage(), null);
	}

	public static void addErrorMessageToContext(String messageText, String componentId) {
		Severity severity = FacesMessage.SEVERITY_ERROR;
		addMessageToContext(messageText, componentId, severity);
	}

	public static ServletContext getServletContext() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (ctx == null) {
			return null;
		}
		ExternalContext extCtx = ctx.getExternalContext();
		return (ServletContext) extCtx.getContext();
	}

	public static String getInitParameter(String parameterName) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ext = ctx.getExternalContext();
		return ext.getInitParameter(parameterName);
	}

	public static String getRequestParameter(String parameterName) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ext = ctx.getExternalContext();
		Map<String, String> requestParameterMap = ext.getRequestParameterMap();
		return requestParameterMap.get(parameterName);
	}

	public static Object getManagedBean(String beanName) {
		return getManagedBean(beanName, Object.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getManagedBean(String beanName, Class<T> clazz) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		return (T) application.evaluateExpressionGet(context, "#{" + beanName + "}", clazz);
	}

	public static Object resolveExpression(String expression, Class<?> expectedType) {
		FacesContext vFacesContext = FacesContext.getCurrentInstance();
		Application vApplication = vFacesContext.getApplication();
		ELContext elContext = vFacesContext.getELContext();
		ExpressionFactory expressionFactoy = vApplication.getExpressionFactory();
		ValueExpression valueExpression = expressionFactoy.createValueExpression(elContext, expression, expectedType);
		return valueExpression.getValue(elContext);
	}

	public static Object getRequestObjectByKey(String key) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getExternalContext().getRequestMap().get(key);
	}

	public static Object getSessionObjectByKey(String key) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getExternalContext().getSessionMap().get(key);
	}

	public static Object removeSessionObjectByKey(String key) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getExternalContext().getSessionMap().remove(key);
	}

	public static Object putSessionObjectByKey(String key, Object obj) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getExternalContext().getSessionMap().put(key, obj);
	}

	public static Object getApplicationObjectByKey(String key) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getExternalContext().getApplicationMap().get(key);
	}

	public static Object getParameterByKey(String key) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getExternalContext().getRequestParameterMap().get(key);
	}

	public static Map<String, String> getRequestHeaderMap() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ext = ctx.getExternalContext();
		return ext.getRequestHeaderMap();
	}

	public static Locale getViewLocale() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return ctx.getViewRoot().getLocale();
	}

	public static Collator getViewCollator() {
		return Collator.getInstance(getViewLocale());
	}

}
