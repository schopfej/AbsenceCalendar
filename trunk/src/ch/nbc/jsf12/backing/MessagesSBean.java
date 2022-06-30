/**
 * Copyright (C) 2011 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.jsf12.backing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import ch.nbc.jsf12.auth.AccessPhaseListener;
import ch.nbc.jsf12.model.Message;
import ch.nbc.jsf12.utils.FacesUtils;

public class MessagesSBean {

	private final Set<Message> messages = new HashSet<Message>();

	public Set<Message> getMessages() {
		return messages;
	}

	/**
	 * Get read only list.
	 * 
	 * @return
	 */
	public List<Message> getList() {
		List<Message> list = new ArrayList<Message>(messages);
		Collections.sort(list, new Comparator<Message>() {
			public int compare(Message one, Message another) {
				Message oneMessage = one;
				Message anotherMessage = another;
				return anotherMessage.getTimestamp().compareTo(oneMessage.getTimestamp());
			}
		});
		return list;

	}

	public String reset() {
		messages.clear();
		FacesContext ctx = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = ctx.getExternalContext().getSessionMap();
		Object accessor = sessionMap.get("accessor");
		Object newCookieHashCode = sessionMap.get(AccessPhaseListener.TICKET_HASH_KEY);
		sessionMap.clear();

		FacesUtils.putSessionObjectByKey("accessor", accessor);
		FacesUtils.putSessionObjectByKey(AccessPhaseListener.TICKET_HASH_KEY, newCookieHashCode);
		return null;
	}

}
